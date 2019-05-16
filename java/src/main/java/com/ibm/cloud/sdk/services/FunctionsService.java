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

package com.ibm.cloud.sdk.services;

import java.util.List;

import com.ibm.cloud.sdk.exceptions.AlreadyExistsException;
import com.ibm.cloud.sdk.exceptions.AmbiguousNamespaceException;
import com.ibm.cloud.sdk.exceptions.ServiceError;

public interface FunctionsService {

    void createNamespace(String name, String description) throws AlreadyExistsException, ServiceError;

    String getNamespaceId(String name) throws AmbiguousNamespaceException, ServiceError;

    List<String> getNamespaceIds(String name) throws ServiceError;

    void deleteNamespaceByName(String name) throws AmbiguousNamespaceException, ServiceError;

    void deleteNamespaceById(String id) throws ServiceError;
}
