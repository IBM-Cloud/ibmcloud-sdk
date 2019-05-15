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

public class IntrospectResult {

    private String iamId;
    private String realm;
    private String identifier;
    private String subject;
    private String accountId;

    public IntrospectResult(final String iamId, final String realm, final String identifier, final String subject, final String accountId) {
        this.iamId = iamId;
        this.realm = realm;
        this.identifier = identifier;
        this.subject = subject;
        this.accountId = accountId;
    }

    public String getIamId() {
        return this.iamId;
    }

    public String getRealm() {
        return this.realm;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getAccountId() {
        return this.accountId;
    }

}
