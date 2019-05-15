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

public class ResourceGroup {

    private String id;
    private String crn;
    private String name;
    private String state;
    private boolean dfault;
    private String quotaId;
    private String quotaUrl;
    private String paymentMethodsUrl;
    private String teamsUrl;
    private long createdAt;
    private long updatedAt;

    public ResourceGroup(final String id, final String crn, final String name, final String state, final boolean dfault, final String quotaId, final String quotaUrl,
            final String paymentMethodsUrl, final String teamsUrl, final long createdAt, final long updatedAt) {
        this.id = id;
        this.crn = crn;
        this.name = name;
        this.state = state;
        this.dfault = dfault;
        this.quotaId = quotaId;
        this.quotaUrl = quotaUrl;
        this.paymentMethodsUrl = paymentMethodsUrl;
        this.teamsUrl = teamsUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return this.id;
    }

    public String getCrn() {
        return this.crn;
    }

    public String getName() {
        return this.name;
    }

    public String getState() {
        return this.state;
    }

    public boolean isDefault() {
        return this.dfault;
    }

    public String getQuotaId() {
        return this.quotaId;
    }

    public String getQuotaUrl() {
        return this.quotaUrl;
    }

    public String getPaymentMethodsUrl() {
        return this.paymentMethodsUrl;
    }

    public String getTeamsUrl() {
        return this.teamsUrl;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public String toString() {
        return "ResourceGroup [id=" + id + ", crn=" + crn + ", name=" + name + ", state=" + state + ", dfault=" + dfault + ", quotaId=" + quotaId + ", quotaUrl=" + quotaUrl
                + ", paymentMethodsUrl=" + paymentMethodsUrl + ", teamsUrl=" + teamsUrl + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
}
