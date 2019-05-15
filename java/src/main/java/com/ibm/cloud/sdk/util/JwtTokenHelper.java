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

package com.ibm.cloud.sdk.util;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.cloud.sdk.IntrospectResult;

public class JwtTokenHelper {

    public static IntrospectResult parseIntrospectResult(String response) {
        JsonObject responseObj = Json.createReader(new StringReader(response)).readObject();
        String iamId = JsonHelper.getStringOrDefault(responseObj, "iam_id", null);
        String realm = JsonHelper.getStringOrDefault(responseObj, "realmId", null);
        String identifier = JsonHelper.getStringOrDefault(responseObj, "identifier", null);
        String subject = JsonHelper.getStringOrDefault(responseObj, "sub", null);
        String accountId = null;
        if (responseObj.containsKey("account")) {
            accountId = buildAccountId(responseObj.getJsonObject("account"));
        }
        return new IntrospectResult(iamId, realm, identifier, subject, accountId);
    }

    public static String buildAccountId(JsonObject accountObj) {
        if (accountObj.containsKey("bss")) {
            return accountObj.getString("bss");
        } else if (accountObj.containsKey("ims")) {
            return "SL-" + accountObj.getString("ims");
        } else {
            return null;
        }
    }

}
