import sys
sys.path.append('ibmcloud')
import ibmcloud
import test_credentials

print("Initializing SDK with test IAM API Key")
ibmcloudClient = ibmcloud.IBMCloud(test_credentials.apikey, client_info='ibmcloud SDK test')

print("Logging on to IBM Cloud using IAM API Key")
ibmcloudClient.logon()

print("Getting account ID for initialized IAM API Key")
print(ibmcloudClient.get_account_id())

print("Getting account ID for provided IAM API Key")
print(ibmcloudClient.get_account_id(test_credentials.apikey))

print("Getting default resource group ID for account of initialized IAM API Key")
print(ibmcloudClient.get_default_rersource_group_id())

print("Creating test IAM function namespace")
ibmcloudClient.functions.create_namespace("test_namepsace", "This is a test")

print("Trying to create function IAM function namespace with same name again")
try:
    ibmcloudClient.functions.create_namespace("test_namepsace", "This is another test")
except RuntimeError as e:
    print(e)

print("Getting namespace ID")
print(ibmcloudClient.functions.get_namespace_id("test_namepsace"))

print("Deleting test IAM function namespace again")
ibmcloudClient.functions.delete_namespace("test_namepsace")
