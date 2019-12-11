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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.helix.HelixCloudProperties;
import org.apache.helix.HelixCloudPropertiesFactory;
import org.apache.helix.api.cloud.CloudInstanceInformationProcessor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AzureCloudInstanceInformationProcessor implements CloudInstanceInformationProcessor<String> {
  private CloseableHttpClient _closeableHttpClient;
  private HelixCloudProperties _helixCloudProperties;

  public AzureCloudInstanceInformationProcessor() {
    _closeableHttpClient = VMSSHttpUtil.getHttpClient();
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(AzureCloudInstanceInformationProcessor.class.getName());

  /**
   * fetch the raw Azure cloud instance information
   * @return raw Azure cloud instance information
   */
  @Override
  public List<String> fetchCloudInstanceInformation() {
    _helixCloudProperties = HelixCloudPropertiesFactory.getInstance().getHelixCloudProperties();
    String url = _helixCloudProperties.getProperty("CLOUD_INFO_SOURCE");
    List<String> response = new ArrayList<>();
    response.add(getAzureCloudInformationFromUrl(url));
    return response;
  }

  /**
   * @return raw Azure cloud instance information
   */
  private String getAzureCloudInformationFromUrl(String url) {
    HttpGet httpGet = new HttpGet(url);
    httpGet.setHeader("Metadata", "true");

    try {
      CloseableHttpResponse response = _closeableHttpClient.execute(httpGet);

      if (response == null || response.getStatusLine().getStatusCode() != 200) {
        LOG.error("Failed to get an HTTP Response for the request. Response: {}. Status code: {}",
            (response == null ? "NULL" : response.getStatusLine().getReasonPhrase()),
            response.getStatusLine().getStatusCode());
      }

      String responseString = EntityUtils.toString(response.getEntity());
      LOG.info("VMSS virtual machine instance information query result: {}", responseString);
      return responseString;
    } catch (IOException e) {
      LOG.error("Failed to get Azure cloud information from url {}", url);
    }
    return null;
  }

  /**
   * Parse raw Azure cloud instance information.
   * @return required azure cloud instance information
   */
  @Override
  public AzureCloudInstanceInformation parseCloudInstanceInformation(List<String> responses) {
    AzureCloudInstanceInformation azureCloudInstanceInformation = null;
    for (String response : responses) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        JsonNode jsonNode = mapper.readTree(response);
        JsonNode computeNode = jsonNode.path(_helixCloudProperties.getProperty("FILED_COMPUTE_INFO"));
        if (!computeNode.isMissingNode()) {
          String vmName = computeNode.path(_helixCloudProperties.getProperty("FILED_VM_NAME")).getTextValue();
          String platformFaultDomain =
              computeNode.path(_helixCloudProperties.getProperty("FILED_PLATFORM_FAULT_DOMAIN"))
                  .getTextValue();
          String vmssName =
              computeNode.path(_helixCloudProperties.getProperty("FILED_VMSS_NAME")).getValueAsText();
          AzureCloudInstanceInformation.Builder builder =
              new AzureCloudInstanceInformation.Builder();
          builder.setInstanceSetName(vmName).setFaultDomain(platformFaultDomain)
              .setInstanceSetName(vmssName);
          azureCloudInstanceInformation = builder.build();
        }
      } catch (IOException e) {
        LOG.error("Error in parsing cloud information: {}", response, e);
      }
    }

    return azureCloudInstanceInformation;
  }
}
