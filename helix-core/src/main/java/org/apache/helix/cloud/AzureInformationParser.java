package org.apache.helix.cloud;

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


public class AzureInformationParser implements CloudInformationParser {

  private static Object _response;
  private static AzureComputeInfo  _azureComputeInfo;

  public AzureInformationParser(Object response) {
      _response = response;
  }

  @Override
  public boolean validate() {
    try {
      Gson gson =
          new GsonBuilder()
              .registerTypeAdapter(AzureComputeInfo.class, new AzureComputeInfoDeserializer())
              .create();
      _azureComputeInfo = gson.fromJson(_response.toString(), AzureComputeInfo.class);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public String getValue(String key) {
    //TODO: get the value from _azureComputeInfo with a given key
    return null;
  }

  class AzureComputeInfoDeserializer implements JsonDeserializer<AzureComputeInfo>
  {
    @Override
    public  AzureComputeInfo deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException
    {
      JsonElement computeInfo = je.getAsJsonObject().get("compute");
      return new Gson().fromJson(computeInfo, AzureComputeInfo.class);

    }
  }

}
