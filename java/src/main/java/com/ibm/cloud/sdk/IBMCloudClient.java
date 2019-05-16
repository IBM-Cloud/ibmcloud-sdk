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

package com.ibm.cloud.sdk;

import com.ibm.cloud.sdk.authentication.Authenticator;
import com.ibm.cloud.sdk.exceptions.AuthenticationError;
import com.ibm.cloud.sdk.impl.IBMCloudClientImpl;
import com.ibm.cloud.sdk.services.AccountService;
import com.ibm.cloud.sdk.services.FunctionsService;
import com.ibm.cloud.sdk.services.ResourcesService;

public abstract class IBMCloudClient {

    public static IBMCloudClient createNew() {
        return createNew(null);
    }

    public static IBMCloudClient createNew(String clientInfo) {
        return createNew(clientInfo, "cloud.ibm.com");
    }

    public static IBMCloudClient createNew(String clientInfo, String endpoint) {
        return new IBMCloudClientImpl(clientInfo, endpoint);
    }

    // --- authentication ---

    public abstract boolean isAuthenticated();

    public abstract void authenticateUseApiKey(String apikey) throws AuthenticationError;

    public abstract void authenticate(Authenticator authenticator) throws AuthenticationError;

    public abstract void reauthenticate() throws AuthenticationError;

    public abstract String getIAMAccessToken();

    public abstract String getIAMRefreshToken();

    public abstract String getUAAAccessToken();

    public abstract String getUAARefreshToken();

    public abstract boolean hasUAATokens();

    public abstract String getIMSPortalToken();

    public abstract int getIMSUserId();

    public abstract boolean hasIMSPortalToken();

    public abstract String getAccountId();

    // --- account management ---
    public abstract AccountService getAccountService();

    // --- resource controller ---
    public abstract ResourcesService getResourcesService();

    // --- IAM API ---
    public abstract IAMService getIAMService();

    // --- functions API ---
    public abstract FunctionsService getFunctionsService();

    // --- container services ---

    // --- CF API ---

}
