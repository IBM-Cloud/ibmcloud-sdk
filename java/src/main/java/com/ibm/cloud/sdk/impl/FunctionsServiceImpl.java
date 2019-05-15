/* -----------------------------------------------------------------------------
 * Copyright IBM Corp. 2018
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ----------------------------------------------------------------------------- */

package com.ibm.cloud.sdk.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import com.ibm.cloud.sdk.exceptions.AlreadyExistsException;
import com.ibm.cloud.sdk.exceptions.AmbiguousNamespaceException;
import com.ibm.cloud.sdk.exceptions.ServiceError;
import com.ibm.cloud.sdk.services.FunctionsService;
import com.ibm.cloud.sdk.util.Communication;
import com.ibm.cloud.sdk.util.JsonHelper;

public class FunctionsServiceImpl implements FunctionsService {

    private IBMCloudClientImpl ibmCloudClientImpl;

    public FunctionsServiceImpl(final IBMCloudClientImpl ibmCloudClientImpl) {
        this.ibmCloudClientImpl = ibmCloudClientImpl;
    }

    @Override
    public void createNamespace(String name, String description) throws ServiceError {
        String defaultResourceGroupId = ibmCloudClientImpl.getResourceControllerService().getDefaultResourceGroupId();
        createNamespace(name, description, defaultResourceGroupId);
    }

    @Override
    public void createNamespace(String name, String description, String resourceGroupId) throws ServiceError {

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = ibmCloudClientImpl.getClientInfo();
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }
            String authorizationHeaderValue = "Bearer " + ibmCloudClientImpl.getIAMAccessToken();

            // body
            JsonObjectBuilder bodyBuilder = Json.createObjectBuilder();
            bodyBuilder.add("name", name);
            if (description != null) {
                bodyBuilder.add("description", description);
            }
            bodyBuilder.add("resource_group_id", resourceGroupId);
            bodyBuilder.add("resource_plan_id", "functions-base-plan");
            JsonObject body = bodyBuilder.build();

            String url = "https://us-south.functions." + ibmCloudClientImpl.getEndpoint() + "/api/v1/namespaces";
            Invocation.Builder ivb = client.target(url).request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            ivb.header("Authorization", authorizationHeaderValue);

            res = ivb.post(Entity.json(body));
            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status != 200) {
                throw new ServiceError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }
    }

    @Override
    public List<String> getNamespaceId(String name) throws ServiceError {
        String defaultResourceGroupId = ibmCloudClientImpl.getResourceControllerService().getDefaultResourceGroupId();
        return getNamespaceId(name, defaultResourceGroupId);
    }

    @Override
    public List<String> getNamespaceId(String name, String resourceGroupId) throws ServiceError {
        List<String> result = new ArrayList<>();
        List<Namespace> allNamespaces = getNamespaces();
        for (Namespace namespace : allNamespaces) {
            if (name != null && resourceGroupId != null && name.equals(namespace.getName()) && resourceGroupId.equals(namespace.getResourceGroupId())) {
                result.add(namespace.getId());
            }
        }
        return result;
    }

    @Override
    public void createUniqueNamespace(String name, String description) throws AlreadyExistsException, ServiceError {
        String defaultResourceGroupId = ibmCloudClientImpl.getResourceControllerService().getDefaultResourceGroupId();
        createUniqueNamespace(name, description, defaultResourceGroupId);
    }

    @Override
    public void createUniqueNamespace(String name, String description, String resourceGroupId) throws AlreadyExistsException, ServiceError {
        List<String> namespaces = getNamespaceId(name, resourceGroupId);
        if (namespaces.size() > 0) {
            throw new AlreadyExistsException();
        }
        createNamespace(name, description, resourceGroupId);
    }

    @Override
    public String getUniqueNamespaceId(String name) throws AmbiguousNamespaceException, ServiceError {
        String defaultResourceGroupId = ibmCloudClientImpl.getResourceControllerService().getDefaultResourceGroupId();
        return getUniqueNamespaceId(name, defaultResourceGroupId);
    }

    @Override
    public String getUniqueNamespaceId(String name, String resourceGroupId) throws AmbiguousNamespaceException, ServiceError {
        List<String> namespaceIds = getNamespaceId(name, resourceGroupId);
        if (namespaceIds.size() > 1) {
            throw new AmbiguousNamespaceException();
        }
        return namespaceIds.get(0);
    }

    @Override
    public void deleteUniqueNamespaceByName(String name) throws AmbiguousNamespaceException, ServiceError {
        String defaultResourceGroupId = ibmCloudClientImpl.getResourceControllerService().getDefaultResourceGroupId();
        deleteUniqueNamespaceByName(name, defaultResourceGroupId);
    }

    @Override
    public void deleteUniqueNamespaceByName(String name, String resourceGroupId) throws AmbiguousNamespaceException, ServiceError {

        String namespaceId = getUniqueNamespaceId(name, resourceGroupId);
        deleteNamespaceById(namespaceId);
    }

    public void deleteNamespaceById(String namespaceId) throws ServiceError {

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = ibmCloudClientImpl.getClientInfo();
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }
            String authorizationHeaderValue = "Bearer " + ibmCloudClientImpl.getIAMAccessToken();

            String url = "https://us-south.functions." + ibmCloudClientImpl.getEndpoint() + "/api/v1/namespaces/" + namespaceId;
            Invocation.Builder ivb = client.target(url).request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            ivb.header("Authorization", authorizationHeaderValue);

            res = ivb.delete();
            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status != 200) {
                throw new ServiceError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }
    }

    public List<Namespace> getNamespaces() throws ServiceError {
        ibmCloudClientImpl.verifyAccountSelected();

        List<Namespace> result = null;

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = ibmCloudClientImpl.getClientInfo();
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }
            String authorizationHeaderValue = "Bearer " + ibmCloudClientImpl.getIAMAccessToken();

            String url = "https://us-south.functions." + ibmCloudClientImpl.getEndpoint() + "/api/v1/namespaces?limit=0&offset=0";
            Invocation.Builder ivb = client.target(url).request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            ivb.header("Authorization", authorizationHeaderValue);

            res = ivb.get();

            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status == 200) {
                ArrayList<Namespace> nameSpacesResult = new ArrayList<>();
                JsonObject obj = Json.createReader(new StringReader(response)).readObject();
                JsonArray jArr = obj.getJsonArray("namespaces");
                for (int i = 0; i < jArr.size(); i++) {
                    JsonObject resource = jArr.getJsonObject(i);
                    String id = JsonHelper.getStringOrDefault(resource, "id", null);
                    String location = JsonHelper.getStringOrDefault(resource, "location", null);
                    String name = JsonHelper.getStringOrDefault(resource, "name", null);
                    String description = JsonHelper.getStringOrDefault(resource, "description", null);
                    String resourceGroupId = JsonHelper.getStringOrDefault(resource, "resource_group_id", null);
                    String resourcePlanId = JsonHelper.getStringOrDefault(resource, "resource_plan_id", null);
                    String classicSpaceGuid = JsonHelper.getStringOrDefault(resource, "classic_spaceguid", null);
                    int classicType = JsonHelper.getIntOrDefault(resource, "classic_type", 0);
                    Namespace nameSpace = new Namespace(id, location, name, description, resourceGroupId, resourcePlanId, classicSpaceGuid, classicType);
                    nameSpacesResult.add(nameSpace);
                }
                result = nameSpacesResult;

            } else {
                throw new ServiceError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }

        return result;
    }
}
