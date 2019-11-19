package org.apache.helix.cloud.azure;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.List;
import org.apache.helix.common.cloud.CloudInformation;
import org.apache.helix.common.cloud.CloudInformationParser;
import org.codehaus.jackson.JsonNode;


public class AzureCloudInformationParser implements CloudInformationParser {

  private static List<String> _responses;

  public AzureCloudInformationParser(List<String> responses) {
    _responses = responses;
  }


  /**
   * Validate the response against the fields defined in Azure cloud information
   * @return whether the response is valid
   */
  @Override
  public boolean validate(CloudInformation azureCloudInformation) {
    List<JsonNode> jsonNodes = new ArrayList<>();
    _responses.stream().forEach(response ->  jsonNodes.add(convert(response)));
    for(JsonNode _jsonResponse: jsonNodes) {
      // TODO: validate against Azure cloud information to make sure all required fields are present
    }
    return true;
  }

  /**
   * Get a specific field value from Azure response by specifying the key
   * @param key the key of the field
   * @return the value of the required field
   */
  @Override
  public String getValue(String key) {
    //TODO: get the value from Azure cloud information with a given key
    return null;
  }

  /**
   * Convert the string response to a JsonNode for further validation
   * @return a json object
   */
  public JsonNode convert(String jsonString) {
    //TODO: convert the string into a json node
    return null;
  }
}
