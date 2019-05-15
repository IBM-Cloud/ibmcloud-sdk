# ------------------------------------------------------------------------------
# Copyright IBM Corp. 2018
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

import urllib
from tornado.escape import json_decode
from tornado.httpclient import HTTPClient, HTTPError
from tornado.httputil import HTTPHeaders
import sys
import json
import jwt


class IBMCloud():
    def __init__(self, client_info='', endpoint='cloud.ibm.com'):
        self.functions = self.Functions(self)
		self.iam = self.Iam(self)
		self.resource_controller = self.Resource_Controller(self)

        if client_info == '':
            self.user_agent = 'IBM Cloud Python SDK'
        else:
            self.user_agent = client_info
        self.endpoint = endpoint
        self.client = HTTPClient()
        self.request_headers = HTTPHeaders({'Content-Type': 'application/json'})
        self.request_headers.add('Accept', 'application/json')
        self.request_headers.add('User-Agent', self.user_agent)
        self.request_headers_xml_content = HTTPHeaders({'Content-Type': 'application/x-www-form-urlencoded'})
        self.request_headers_xml_content.add('Accept', 'application/json')
        self.request_headers_xml_content.add('User-Agent', self.user_agent)
        self.logged_on = False

    def authenticate(self, api_key):
        self.api_key = api_key
        if sys.version_info >= (3, 0):
            data = urllib.parse.urlencode({'grant_type': 'urn:ibm:params:oauth:grant-type:apikey', 'apikey': self.api_key})
        else:
            data = urllib.urlencode({'grant_type': 'urn:ibm:params:oauth:grant-type:apikey', 'apikey': self.api_key})

        response = self.client.fetch(
            'https://iam.' + self.endpoint + '/identity/token',
            method='POST',
            headers=self.request_headers_xml_content,
            validate_cert=False,
            body=data)

        if response.code == 200:
            # print("Authentication successful")
            bearer_response = json_decode(response.body)
			access_token_decoded = jwt.decode(bearer_response['access_token'], verify=False)
			self.current_account_id = access_token_decoded['account']['bss']
            self.bearer_token = 'Bearer ' + bearer_response['access_token']
            self.request_headers = HTTPHeaders({'Content-Type': 'application/json'})
            self.request_headers.add('Accept', 'application/json')
            self.request_headers.add('User-Agent', self.user_agent)
            self.request_headers.add('authorization', self.bearer_token)
            self.logged_on = True

        else:
            print("Authentication failed with http code {}".format(response.code))

    def get_current_account_id(self):
	    return self.current_account_id


    class Iam(object):
	    def __init__(self, parent_instance):
		    self.sdk = parent_instance
			
		def introspect(self, token='', apikey=''):
		    if token == '':
                if sys.version_info >= (3, 0):
                    data = urllib.parse.urlencode({'apikey': apikey})
                else:
                    data = urllib.urlencode({'apikey': apikey})
            else
                if sys.version_info >= (3, 0):
                    data = urllib.parse.urlencode({'token': token})
                else:
                    data = urllib.urlencode({'apikey': token})

            response = self.sdk.client.fetch(
                'https://iam.' + self.sdk.endpoint + '/identity/introspect',
                method='POST',
                headers=self.sdk.request_headers_xml_content,
                validate_cert=False,
                body=data)

            if response.code != 200:
                raise RuntimeError("Error: http code {} while introspecting IAM API key or token.".format(response.code))

			return json_decode(response.body)


    class Resource_Controller(object):
	    def __init__(self, parent_instance):
		    self.sdk = parent_instance
			
		def get_default_resource_group_id(self)
			if not self.sdk.logged_on:
				print("You are not logged on to IBM Cloud")
				return

			account_id_to_introspect = self.sdk.get_current_account_id()

			response = self.sdk.client.fetch(
				'https://resource-controller.' + self.sdk.endpoint + '/v2/resource_groups?account_id=' + account_id_to_introspect,
				method='GET',
				headers=self.sdk.request_headers,
				validate_cert=False)

			if response.code != 200:
				raise RuntimeError("Error: http code {} while listing resource groups.".format(response.code))

			resource_groups = json_decode(response.body)

			for group in resource_groups['resources']:
				if group['default']:
					return group['id']

			raise RuntimeError("No default resource group found.")

			
    class Functions(object):
        def __init__(self, parent_instance):
            self.sdk = parent_instance

        def create_unique_namespace(self, namespace, namespace_description='', resource_group_id=''):
            if not self.sdk.logged_on:
                print("You are not logged on to IBM Cloud")
                return

            resource_group_id_to_introspect=resource_group_id
            if resource_group_id_to_introspect == '':
                resource_group_id_to_introspect = self.sdk.resource_controller.get_default_resource_group_id()

            data = json.dumps({'description': namespace_description,
                               'name': namespace,
                               'resource_group_id': resource_group_id_to_introspect,
                               'resource_plan_id': 'functions-base-plan'})

            response = self.sdk.client.fetch(
                'https://us-south.functions.' + self.sdk.endpoint + '/api/v1/namespaces?limit=0&offset=0',
                method='GET',
                headers=self.sdk.request_headers,
                validate_cert=False)

            if response.code != 200:
                raise RuntimeError("Error: http code {} while listing namespaces.".format(response.code))

            namespaces = json_decode(response.body)

            for current_namespace in namespaces['namespaces']:
                if 'name' in current_namespace and current_namespace['name'] == namespace:
                    raise RuntimeError("Namespace \"{}\" already exists with ID {}."
                                        .format(namespace, current_namespace['id']))

            # Create new namespace:
            response = self.sdk.client.fetch(
                'https://us-south.functions.' + self.sdk.endpoint + '/api/v1/namespaces',
                method='POST',
                headers=self.sdk.request_headers,
                validate_cert=False,
                body=data)

            if response.error is not None:
                raise RuntimeError("Error: http code {} with error {} while creating function namespace."
                                    .format(response.code, response.error))

        def get_unique_namespace_id(self, namespace):
            if not self.sdk.logged_on:
                print("You are not logged on to IBM Cloud")
                return

            response = self.sdk.client.fetch(
                'https://us-south.functions.' + self.sdk.endpoint + '/api/v1/namespaces?limit=0&offset=0',
                method='GET',
                headers=self.sdk.request_headers,
                validate_cert=False)

            if response.code != 200:
                raise RuntimeError("Error: http code {} while listing namespaces.".format(response.code))

            namespaces = json_decode(response.body)

            namespace_id = ''
            for current_namespace in namespaces['namespaces']:
                if 'name' in current_namespace and current_namespace['name'] == namespace:
                    if namespace_id != '':
                        raise RuntimeError("Error: namespace  with name \"{}\" exists more than once".format(namespace))
                    namespace_id=current_namespace['id']

            if namespace_id != '':
                return namespace_id

            raise RuntimeError("No namespace \"{}\" found.".format(namespace))

        def delete_unique_namespace(self, namespace):
            if not self.sdk.logged_on:
                print("You are not logged on to IBM Cloud")
                return

            response = self.sdk.client.fetch(
                'https://us-south.functions.' + self.sdk.endpoint + '/api/v1/namespaces/{}'.format(self.get_unique_namespace_id(namespace)),
                method='DELETE',
                headers=self.sdk.request_headers,
                validate_cert=False)

            if response.code != 200:
                raise RuntimeError("Error: http code {} while deleting namespace.".format(response.code))