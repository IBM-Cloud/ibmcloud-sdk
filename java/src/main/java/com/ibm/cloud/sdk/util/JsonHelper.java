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

import javax.json.JsonObject;

public class JsonHelper {

    public static String getStringOrDefault(JsonObject obj, String name, String defValue) {
        if (obj.containsKey(name)) {
            return obj.getString(name);
        } else {
            return defValue;
        }
    }

    public static int getIntOrDefault(JsonObject obj, String name, int defValue) {
        if (obj.containsKey(name)) {
            return obj.getInt(name);
        } else {
            return defValue;
        }
    }

}
