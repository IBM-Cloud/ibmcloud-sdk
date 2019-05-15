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
import com.ibm.cloud.sdk.services.ResourceControllerService;

public abstract class IBMCloudClient {

    public enum Environment {
        test, prod;
    }

    public static IBMCloudClient createNew() {
        return createNew(null, Environment.prod);
    }

    public static IBMCloudClient createNew(Environment env) {
        return createNew(null, env);
    }

    public static IBMCloudClient createNew(String clientInfo) {
        return createNew(clientInfo, Environment.prod);
    }

    public static IBMCloudClient createNew(String clientInfo, Environment env) {
        return new IBMCloudClientImpl(clientInfo, env);
    }

    /**
     * Returns null if all prerequisites are met, otherwise a short message
     * indicating a prerequisite that is missing.
     */
    public static String checkPrereqs() {
        try {
            Class.forName("com.ibm.cloud.sdk.impl.IBMCloudSessionImpl");
        } catch (ClassNotFoundException cnfe) {
            return "Could not find implementation class com.ibm.cloud.sdk.impl.IBMCloudSessionImpl";
        }
        return null;
    }

    // --- authentication ---

    public abstract boolean isAuthenticated();

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

    public abstract String getCurrentAccountId();

    // --- account management ---
    public abstract AccountService getAccountService();

    // --- resource controller ---
    public abstract ResourceControllerService getResourceControllerService();

    // --- IAM API ---
    public abstract IAMService getIAMService();

    // --- functions API ---
    public abstract FunctionsService getFunctionsService();

    // --- container services ---

    // --- CF API ---

}
