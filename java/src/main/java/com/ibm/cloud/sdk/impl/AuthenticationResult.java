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

import com.ibm.cloud.sdk.authentication.Authenticator;

public class AuthenticationResult {

    final private String iamAccessToken;
    final private String iamRefreshToken;

    final private long expiration;

    final private boolean hasUaaToken;
    final private String uaaAccessToken;
    final private String uaaRefreshToken;

    final private boolean hasImsToken;
    final private String imsPortalToken;
    final private int imsUserId;

    final Authenticator reauthenticator;

    private AuthenticationResult(final String iamAccessToken, final String iamRefreshToken, final long expiration, final boolean hasUaaToken, final String uaaAccessToken,
            final String uaaRefreshToken, final boolean hasImsToken, final String imsPortalToken, final int imsUserId, final Authenticator reauthenticator) {
        this.iamAccessToken = iamAccessToken;
        this.iamRefreshToken = iamRefreshToken;
        this.expiration = expiration;

        this.hasUaaToken = hasUaaToken;
        this.uaaAccessToken = uaaAccessToken;
        this.uaaRefreshToken = uaaRefreshToken;

        this.hasImsToken = hasImsToken;
        this.imsPortalToken = imsPortalToken;
        this.imsUserId = imsUserId;

        this.reauthenticator = reauthenticator;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getIamAccessToken() {
        return this.iamAccessToken;
    }

    public String getIamRefreshToken() {
        return this.iamRefreshToken;
    }

    public long getExpiration() {
        return this.expiration;
    }

    public boolean hasUaaToken() {
        return this.hasUaaToken;
    }

    public String getUaaAccessToken() {
        return this.uaaAccessToken;
    }

    public String getUaaRefreshToken() {
        return this.uaaRefreshToken;
    }

    public boolean hasImsToken() {
        return this.hasImsToken;
    }

    public String getImsPortalToken() {
        return this.imsPortalToken;
    }

    public int getImsUserId() {
        return this.imsUserId;
    }

    public Authenticator getReauthenticator() {
        return this.reauthenticator;
    }

    @Override
    public String toString() {
        return "AuthenticationResult [iamAccessToken=" + iamAccessToken + ", iamRefreshToken=" + iamRefreshToken + ", expiration=" + expiration + ", hasUaaToken=" + hasUaaToken
                + ", uaaAccessToken=" + uaaAccessToken + ", uaaRefreshToken=" + uaaRefreshToken + ", hasImsToken=" + hasImsToken + ", imsPortalToken=" + imsPortalToken
                + ", imsUserId=" + imsUserId + ", reauthenticator=" + reauthenticator + "]";
    }

    public static class Builder {

        private String iamAccessToken = null;
        private String iamRefreshToken = null;
        private long expiration = 0L;

        private boolean hasUaaToken = false;
        private String uaaAccessToken = null;
        private String uaaRefreshToken = null;

        private boolean hasImsToken = false;
        private String imsPortalToken = null;
        private int imsUserId = 0;

        private Authenticator reauthenticator = null;

        public Builder iamResult(final String iamAccessToken, final String iamRefreshToken, final long expiration) {
            this.iamAccessToken = iamAccessToken;
            this.iamRefreshToken = iamRefreshToken;
            this.expiration = expiration;
            return this;
        }

        public Builder uaaResult(final String uaaAccessToken, final String uaaRefreshToken) {
            this.hasUaaToken = true;
            this.uaaAccessToken = uaaAccessToken;
            this.uaaRefreshToken = uaaRefreshToken;
            return this;
        }

        public Builder imsResult(final String imsPortalToken, int imsUserId) {
            this.hasImsToken = true;
            this.imsPortalToken = imsPortalToken;
            this.imsUserId = imsUserId;
            return this;
        }

        public Builder reauthenticator(final Authenticator reauthenticator) {
            this.reauthenticator = reauthenticator;
            return this;
        }

        public AuthenticationResult build() {
            return new AuthenticationResult(iamAccessToken, iamRefreshToken, expiration, hasUaaToken, uaaAccessToken, uaaRefreshToken, hasImsToken, imsPortalToken, imsUserId,
                    reauthenticator);
        }

    }
}
