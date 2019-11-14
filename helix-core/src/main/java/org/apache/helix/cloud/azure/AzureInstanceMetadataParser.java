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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.apache.helix.common.cloud.CloudInformationParser;


public class AzureInstanceMetadataParser implements CloudInformationParser {

  private static Object _response;
  private static AzureInstanceComputeInfo _azureInstanceComputeInfo;

  public AzureInstanceMetadataParser(Object response) {
      _response = response;
  }

  /**
   * Validate the response from Azure against the schema defined in AIMS https://docs.microsoft.com/en-us/azure/virtual-machines/windows/instance-metadata-service
   * @return whether the response is a valid one from Azure
   */
  @Override
  public boolean validate() {
    try {
      Gson gson =
          new GsonBuilder()
              .registerTypeAdapter(AzureInstanceComputeInfo.class, new AzureComputeInfoDeserializer())
              .create();
      _azureInstanceComputeInfo = gson.fromJson(_response.toString(), AzureInstanceComputeInfo.class);
    } catch (Exception e) {
      return false;
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
    //TODO: get the value from _azureComputeInfo with a given key
    return null;
  }

  class AzureComputeInfoDeserializer implements JsonDeserializer<AzureInstanceComputeInfo>
  {
    @Override
    public  AzureInstanceComputeInfo deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException
    {
      JsonElement computeInfo = je.getAsJsonObject().get("compute");
      return new Gson().fromJson(computeInfo, AzureInstanceComputeInfo.class);

    }
  }

}
