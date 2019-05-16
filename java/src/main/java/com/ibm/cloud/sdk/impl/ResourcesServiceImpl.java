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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import com.ibm.cloud.sdk.ResourceGroup;
import com.ibm.cloud.sdk.exceptions.ServiceError;
import com.ibm.cloud.sdk.services.ResourcesService;
import com.ibm.cloud.sdk.util.Communication;
import com.ibm.cloud.sdk.util.Iso8601Helper;

public class ResourcesServiceImpl implements ResourcesService {

    private IBMCloudClientImpl ibmCloudClientImpl;

    public ResourcesServiceImpl(final IBMCloudClientImpl ibmCloudClientImpl) {
        this.ibmCloudClientImpl = ibmCloudClientImpl;
    }

    @Override
    public String getDefaultResourceGroupId() throws ServiceError {
        String result = null;
        List<ResourceGroup> resourceGroups = getResourceGroups();
        for (ResourceGroup resourceGroup : resourceGroups) {
            if (resourceGroup.isDefault()) {
                result = resourceGroup.getId();
                break;
            }
        }
        return result;
    }

    public List<ResourceGroup> getResourceGroups() throws ServiceError {
        ibmCloudClientImpl.verifyAccountSelected();

        List<ResourceGroup> result = null;

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = ibmCloudClientImpl.getClientInfo();
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }
            String authorizationHeaderValue = "Bearer " + ibmCloudClientImpl.getIAMAccessToken();
            String requestUrl = "https://resource-controller." + ibmCloudClientImpl.getEndpoint() + "/v2/resource_groups?account_id=" + ibmCloudClientImpl.getAccountId();
            Invocation.Builder ivb = client.target(requestUrl).request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            ivb.header("Authorization", authorizationHeaderValue);
            res = ivb.get();
            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status == 200) {
                ArrayList<ResourceGroup> resGrpResult = new ArrayList<>();
                JsonObject obj = Json.createReader(new StringReader(response)).readObject();
                JsonArray jArr = obj.getJsonArray("resources");
                for (int i = 0; i < jArr.size(); i++) {
                    JsonObject resource = jArr.getJsonObject(i);
                    String id = resource.getString("id");
                    String crn = resource.getString("crn");
                    String name = resource.getString("name");
                    String state = resource.getString("state");
                    boolean isDefault = resource.getBoolean("default");
                    String quotaId = resource.getString("quota_id");
                    String quotaUrl = resource.getString("quota_url");
                    String paymentMethodsUrl = resource.getString("payment_methods_url");
                    String teamsUrl = resource.getString("teams_url");
                    long createdAt = Iso8601Helper.getTime(resource.getString("created_at"));
                    long updatedAt = Iso8601Helper.getTime(resource.getString("updated_at"));
                    ResourceGroup rg = new ResourceGroup(id, crn, name, state, isDefault, quotaId, quotaUrl, paymentMethodsUrl, teamsUrl, createdAt, updatedAt);
                    resGrpResult.add(rg);
                }
                result = resGrpResult;

            } else {
                throw new ServiceError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }

        return result;
    }

}
