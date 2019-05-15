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

package com.ibm.cloud.sdk.authentication;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.ibm.cloud.sdk.exceptions.AuthenticationError;
import com.ibm.cloud.sdk.impl.AuthenticationResult;
import com.ibm.cloud.sdk.util.Communication;

public class Authenticator {

    public enum GrantType {
        apiKey
    };

    final private String clientId;
    final private String clientSecret;
    final private GrantType grantType;
    final private String apiKey;
    final private boolean requestUAATokens;
    final private String uaaClientId;
    final private String uaaClientSecret;
    final private String uaaRedirectUri;
    final private boolean requestIMSPortalToken;
    final private String accountId;

    private Authenticator(String clientId, String clientSecret, GrantType grantType, String apiKey, boolean requestUAATokens, String uaaClientId, String uaaClientSecret,
            String uaaRedirectUri, boolean requestIMSPortalToken, String accountId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.apiKey = apiKey;
        this.requestUAATokens = requestUAATokens;
        this.uaaClientId = uaaClientId;
        this.uaaClientSecret = uaaClientSecret;
        this.uaaRedirectUri = uaaRedirectUri;
        this.requestIMSPortalToken = requestIMSPortalToken;
        this.accountId = accountId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public GrantType getGrantType() {
        return this.grantType;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public boolean requestUAATokens() {
        return this.requestUAATokens;
    }

    public String getUAAClientId() {
        return this.uaaClientId;
    }

    public String getUAAClientSecret() {
        return this.uaaClientSecret;
    }

    public String getUAARedirectUri() {
        return this.uaaRedirectUri;
    }

    public boolean requestIMSPortalToken() {
        return this.requestIMSPortalToken;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public AuthenticationResult authenticate(String iamBaseUrl, String clientInfo) throws AuthenticationError {

        AuthenticationResult result = null;

        Client client = null;
        Response res = null;

        try {

            client = ClientBuilder.newClient();

            String userAgent = clientInfo;
            if (userAgent == null || userAgent.isEmpty()) {
                userAgent = "IBM Cloud SDK 1.0";
            }

            String authorizationHeaderValue = null;
            if (getClientId() != null) {
                authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString((getClientId() + ":" + getClientSecret()).getBytes(StandardCharsets.UTF_8));
            }

            Authenticator.Builder reauthenticatorBuilder = Authenticator.newBuilder();

            // Form
            boolean requestTypeUaa = false;
            boolean requestTypeImsPortal = false;
            Form f = new Form();
            if (getGrantType() == GrantType.apiKey) {
                f.param("grant_type", "urn:ibm:params:oauth:grant-type:apikey");
                f.param("apikey", getApiKey());
                reauthenticatorBuilder.useApiKey(getApiKey());
            }

            if (requestUAATokens()) {
                requestTypeUaa = true;
                f.param("uaa_client_id", getUAAClientId());
                f.param("uaa_client_secret", getUAAClientSecret());
                if (getUAARedirectUri() != null) {
                    f.param("uaa_redirect_uri", getUAARedirectUri());
                }
                reauthenticatorBuilder.requestUAAToken(getUAAClientId(), getUAAClientSecret(), getUAARedirectUri());
            }

            if (requestIMSPortalToken()) {
                requestTypeImsPortal = true;
                reauthenticatorBuilder.requestIMSPortalToken();
            }

            String responseType = "cloud_iam";
            if (requestTypeUaa) {
                responseType += " uaa";
            }
            if (requestTypeImsPortal) {
                responseType += " ims_portal";
            }
            f.param("response_type", responseType);

            Invocation.Builder ivb = client.target(iamBaseUrl + "/identity/token").request();
            ivb.header("User-Agent", userAgent);
            ivb.header("Accept", "application/json");
            if (authorizationHeaderValue != null) {
                ivb.header("Authorization", authorizationHeaderValue);
            }

            res = ivb.post(Entity.form(f));
            int status = res.getStatus();
            String response = res.readEntity(String.class);

            if (status == 200) {
                AuthenticationResult.Builder authResultBuilder = AuthenticationResult.newBuilder();
                Authenticator reauthenticator = reauthenticatorBuilder.build();
                authResultBuilder.reauthenticator(reauthenticator);

                JsonObject obj = Json.createReader(new StringReader(response)).readObject();
                String iamAccessToken = obj.getString("access_token");
                String iamRefreshToken = obj.getString("refresh_token");
                long iamAccessTokenExpiration = obj.getInt("expiration") * 1000L;
                authResultBuilder.iamResult(iamAccessToken, iamRefreshToken, iamAccessTokenExpiration);

                if (requestTypeUaa) {
                    String uaaAccessToken = obj.getString("uaa_access_token");
                    String uaaRefreshToken = obj.getString("uaa_refresh_token");
                    authResultBuilder.uaaResult(uaaAccessToken, uaaRefreshToken);
                }
                if (requestTypeImsPortal) {
                    String imsPortalToken = obj.getString("ims_portal");
                    int imsUserId = obj.getInt("ims_user_id");
                    authResultBuilder.imsResult(imsPortalToken, imsUserId);
                }

                result = authResultBuilder.build();

            } else {
                throw new AuthenticationError(status, response);
            }

        } finally {
            Communication.close(res, client);
        }

        return result;
    }

    public static class Builder {
        private String clientId = null;
        private String clientSecret = null;
        private GrantType grantType = null;
        private String apiKey = null;
        private boolean requestUAATokens = false;
        private String uaaClientId = null;
        private String uaaClientSecret = null;
        private String uaaRedirectUri = null;
        private boolean requestIMSPortalToken = false;
        private String accountId = null;

        public Builder useApiKey(String apiKey) {
            cleanupAuthenticationParameters();
            this.grantType = GrantType.apiKey;
            this.apiKey = apiKey;
            return this;
        }

        public Builder requestUAAToken(String uaaClientId, String uaaClientSecret, String uaaRedirectUri) {
            this.requestUAATokens = true;
            this.uaaClientId = uaaClientId;
            this.uaaClientSecret = uaaClientSecret;
            this.uaaRedirectUri = uaaRedirectUri;
            return this;
        }

        public Builder doNotRequestUAAToken() {
            this.requestUAATokens = false;
            this.uaaClientId = null;
            this.uaaClientSecret = null;
            this.uaaRedirectUri = null;
            return this;
        }

        public Builder requestIMSPortalToken() {
            this.requestIMSPortalToken = true;
            return this;
        }

        public Builder doNotRequestIMSPortalToken() {
            this.requestIMSPortalToken = false;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        private void cleanupAuthenticationParameters() {
            this.grantType = null;
            this.apiKey = null;
            // this.username = null;
            // this.password = null;
        }

        public Authenticator build() {
            return new Authenticator(clientId, clientSecret, grantType, apiKey, requestUAATokens, uaaClientId, uaaClientSecret, uaaRedirectUri, requestIMSPortalToken, accountId);
        }
    }

}
