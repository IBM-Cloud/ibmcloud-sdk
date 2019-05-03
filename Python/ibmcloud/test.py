import sys
sys.path.append('ibmcloud')
import ibmcloud
import test_credentials

ibmcloudClient = ibmcloud.IBMCloud(test_credentials.apikey, client_info='ibmcloud SDK test')

print("Loggin on to IBM Cloud using IAM API Key")
ibmcloudClient.logon()
