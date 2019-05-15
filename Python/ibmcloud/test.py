import sys
sys.path.append('ibmcloud')
import ibmcloud
import test_credentials

print("Initializing SDK with test IAM API Key")
ibmcloudClient = ibmcloud.IBMCloud(client_info='ibmcloud SDK test')

print("Logging on to IBM Cloud using IAM API Key")
ibmcloudClient.authenticate(apikey=test_credentials.apikey)

print("Getting account ID for initialized IAM API Key")
print(ibmcloudClient.get_current_account_id())

print("Getting account ID for provided IAM API Key")
print(ibmcloudClient.iam.introspect(apikey=test_credentials.apikey)['account']['bss'])

print("Getting default resource group ID for account of initialized IAM API Key")
print(ibmcloudClient.resource_controller.get_default_resource_group_id())

print("Creating test IAM function namespace")
ibmcloudClient.functions.create_unique_namespace("test_namepsace", "This is a test")

print("Trying to create function IAM function namespace with same name again")
try:
    ibmcloudClient.functions.create_unique_namespace("test_namepsace", "This is another test")
except RuntimeError as e:
    print(e)

print("Getting namespace ID")
print(ibmcloudClient.functions.get_unique_namespace_id("test_namepsace"))

print("Deleting test IAM function namespace again")
ibmcloudClient.functions.delete_unique_namespace(name="test_namepsace")
