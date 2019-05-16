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
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.cloud.sdk.IAMService;
import com.ibm.cloud.sdk.IBMCloudClient;
import com.ibm.cloud.sdk.authentication.Authenticator;
import com.ibm.cloud.sdk.exceptions.AuthenticationError;
import com.ibm.cloud.sdk.services.AccountService;
import com.ibm.cloud.sdk.services.FunctionsService;
import com.ibm.cloud.sdk.services.ResourcesService;
import com.ibm.cloud.sdk.util.JwtTokenHelper;

public class IBMCloudClientImpl extends IBMCloudClient {

    private String endpoint;
    private String clientInfo;

    private AuthenticationResult authResult = null;

    public IBMCloudClientImpl(String clientInfo, String endpoint) {
        this.clientInfo = clientInfo;
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public String getClientId() {
        verifyAuthenticated();
        return this.authResult.getReauthenticator().getClientId();
    }

    public String getClientSecret() {
        verifyAuthenticated();
        return this.authResult.getReauthenticator().getClientSecret();
    }

    // --- authentication ---

    @Override
    public boolean isAuthenticated() {
        return this.authResult != null;
    }

    @Override
    public void authenticate(Authenticator authenticator) throws AuthenticationError {
        this.authResult = authenticator.authenticate("https://iam." + endpoint, clientInfo);
    }

    @Override
    public void reauthenticate() throws AuthenticationError {
        authenticate(this.authResult.getReauthenticator());
    }

    @Override
    public String getIAMAccessToken() {
        verifyAuthenticated();
        return this.authResult.getIamAccessToken();
    }

    @Override
    public String getIAMRefreshToken() {
        verifyAuthenticated();
        return this.authResult.getIamRefreshToken();
    }

    @Override
    public String getUAAAccessToken() {
        verifyUaaToken();
        return this.authResult.getUaaAccessToken();
    }

    @Override
    public String getUAARefreshToken() {
        verifyUaaToken();
        return this.authResult.getUaaRefreshToken();
    }

    @Override
    public boolean hasUAATokens() {
        verifyAuthenticated();
        return this.authResult.hasUaaToken();
    }

    @Override
    public String getIMSPortalToken() {
        verifyImsToken();
        return this.authResult.getImsPortalToken();
    }

    @Override
    public int getIMSUserId() {
        verifyImsToken();
        return this.authResult.getImsUserId();
    }

    @Override
    public boolean hasIMSPortalToken() {
        verifyAuthenticated();
        return this.authResult.hasImsToken();
    }

    public void verifyAuthenticated() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("not authenticated");
        }
    }

    public void verifyAccountSelected() {
        String accountId = getAccountId();
        if (accountId == null) {
            throw new IllegalStateException("no account selected");
        }
    }

    public void verifyUaaToken() {
        verifyAuthenticated();
        if (!this.authResult.hasUaaToken()) {
            throw new IllegalStateException("UAA token not retrieved");
        }
    }

    public void verifyImsToken() {
        verifyAuthenticated();
        if (!this.authResult.hasImsToken()) {
            throw new IllegalStateException("IMS token not retrieved");
        }
    }

    @Override
    public String getAccountId() {
        verifyAuthenticated();
        String result = null;
        JsonObject claims = parseClaimsFromJwtToken(getIAMAccessToken());
        if (claims.containsKey("account")) {
            result = JwtTokenHelper.buildAccountId(claims.getJsonObject("account"));
        }
        return result;
    }

    @Override
    public AccountService getAccountService() {
        return null; // TODO
    }

    @Override
    public ResourcesService getResourcesService() {
        return new ResourcesServiceImpl(this);
    }

    @Override
    public FunctionsService getFunctionsService() {
        return new FunctionsServiceImpl(this);
    }

    @Override
    public IAMService getIAMService() {
        return new IAMServiceImpl(this);
    }

    private JsonObject parseClaimsFromJwtToken(String iamAccessToken) {
        JsonObject result = null;
        int firstDot = iamAccessToken.indexOf('.');
        int secondDot = iamAccessToken.substring(firstDot + 1).indexOf('.');
        if (firstDot != -1 && secondDot != -1) {
            String bodyEncoded = iamAccessToken.substring(firstDot + 1, firstDot + 1 + secondDot);
            byte[] bodyBytes = Base64.getUrlDecoder().decode(bodyEncoded);
            String body = new String(bodyBytes, StandardCharsets.UTF_8);
            result = Json.createReader(new StringReader(body)).readObject();
        }
        return result;
    }

}
