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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.ibm.cloud.sdk.IAMService;
import com.ibm.cloud.sdk.IntrospectResult;
import com.ibm.cloud.sdk.exceptions.ServiceError;
import com.ibm.cloud.sdk.util.Communication;
import com.ibm.cloud.sdk.util.JwtTokenHelper;

public class IAMServiceImpl implements IAMService {

    private IBMCloudClientImpl ibmCloudClientImpl;

    public IAMServiceImpl(final IBMCloudClientImpl ibmCloudClientImpl) {
        this.ibmCloudClientImpl = ibmCloudClientImpl;
    }

    @Override
    public IntrospectResult introspectApiKey(String apiKey) throws ServiceError {

        IntrospectResult result = null;

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = ibmCloudClientImpl.getClientInfo();
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }
            String authorizationHeaderValue = null;
            if (ibmCloudClientImpl.getClientId() != null) {
                authorizationHeaderValue = "Basic "
                        + Base64.getEncoder().encodeToString((ibmCloudClientImpl.getClientId() + ":" + ibmCloudClientImpl.getClientSecret()).getBytes(StandardCharsets.UTF_8));
            }

            String requestUrl = "https://iam." + ibmCloudClientImpl.getEndpoint() + "/identity/introspect";

            Form f = new Form();
            f.param("apikey", apiKey);

            Invocation.Builder ivb = client.target(requestUrl).request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            ivb.header("Authorization", authorizationHeaderValue);
            if (authorizationHeaderValue != null) {
                ivb.header("Authorization", authorizationHeaderValue);
            }
            res = ivb.post(Entity.form(f));
            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status == 200) {
                result = JwtTokenHelper.parseIntrospectResult(response);
            } else {
                throw new ServiceError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }

        return result;
    }

}
