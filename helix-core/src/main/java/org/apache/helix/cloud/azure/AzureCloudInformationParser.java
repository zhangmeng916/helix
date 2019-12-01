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

import java.io.IOException;
import java.util.List;
import org.apache.helix.common.cloud.CloudInformationParser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.helix.cloud.azure.AzureCloudConstants.*;


public class AzureCloudInformationParser implements CloudInformationParser {
  private final List<String> _responses;
  private static final Logger LOG = LoggerFactory.getLogger(AzureCloudInformationParser.class.getName());

  public AzureCloudInformationParser(List<String> responses) {
    _responses = responses;
  }

  /**
   * Parse Azure cloud information. It includes validating the response and populating the corresponding fields.
   * @return azure cloud information
   */
  @Override
  public AzureCloudInformation parseCloudInformation() {
    AzureCloudInformation azureCloudInformation = null;
    for (String response:_responses) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        JsonNode jsonNode = mapper.readTree(response);
        JsonNode computeNode = jsonNode.path(FIELD_COMPUTE);
        if (!computeNode.isMissingNode()) {
          String platformFaultDomain = computeNode.path(FILED_PLATFORM_FAULT_DOMAIN).getValueAsText();
          azureCloudInformation.setPlatformFaultDomain(platformFaultDomain);
          String vmssName = computeNode.path(FILED_VMSS_NAME).getValueAsText();
          azureCloudInformation.setVmssName(vmssName);
        }
      } catch (IOException e) {
        LOG.error("Error in parsing cloud information: {}", response, e);
      }
    }

    //validate azure cloud information
    if (azureCloudInformation.getPlatformFaultDomain().isEmpty()) {
      LOG.error("Azure platform fault domain information is missing");
    }
    if (azureCloudInformation.getVmssName().isEmpty()) {
      LOG.error("Azure VMSS name is missing");
    }
    return azureCloudInformation;
  }
}
