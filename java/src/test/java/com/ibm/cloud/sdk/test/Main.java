package com.ibm.cloud.sdk.test;

import com.ibm.cloud.sdk.IBMCloudClient;
import com.ibm.cloud.sdk.authentication.Authenticator;
import com.ibm.cloud.sdk.exceptions.AlreadyExistsException;
import com.ibm.cloud.sdk.exceptions.AmbiguousNamespaceException;
import com.ibm.cloud.sdk.exceptions.AuthenticationError;
import com.ibm.cloud.sdk.exceptions.ServiceError;

public class Main {

    public static void main(String[] args) throws AuthenticationError, AlreadyExistsException, ServiceError, AmbiguousNamespaceException {

        System.setProperty("https.protocols", "TLSv1.2");

        String test_credentials_apikey = args[0];

        System.out.println("Initializing SDK with test IAM API Key");
        IBMCloudClient ibmcloudClient = IBMCloudClient.createNew("ibmcloud SDK test");

        System.out.println("Logging on to IBM Cloud using IAM API Key");
        Authenticator auth = Authenticator.newBuilder().useApiKey(test_credentials_apikey).build();
        ibmcloudClient.authenticate(auth);

        System.out.println("Getting account ID for initialized IAM API Key");
        System.out.println(ibmcloudClient.getAccountId());

        System.out.println("Getting account ID for provided IAM API Key");
        System.out.println(ibmcloudClient.getIAMService().introspectApiKey(test_credentials_apikey).getAccountId());

        System.out.println("Getting default resource group ID for account of initialized IAM API Key");
        String defaultResourceGroupId = ibmcloudClient.getResourcesService().getDefaultResourceGroupId();
        System.out.println(defaultResourceGroupId);

        System.out.println("Creating test IAM function namespace");
        ibmcloudClient.getFunctionsService().createNamespace("test_namepsace", "This is a test");

        System.out.println("Trying to create function IAM function namespace with same name again");
        try {
            ibmcloudClient.getFunctionsService().createNamespace("test_namepsace", "This is another test");
        } catch (AlreadyExistsException aee) {
            System.out.println(aee);
        }

        System.out.println("Getting namespace ID");
        System.out.println(ibmcloudClient.getFunctionsService().getNamespaceId("test_namepsace"));

        System.out.println("Deleting test IAM function namespace again");
        ibmcloudClient.getFunctionsService().deleteNamespaceByName("test_namepsace");
    }
}
