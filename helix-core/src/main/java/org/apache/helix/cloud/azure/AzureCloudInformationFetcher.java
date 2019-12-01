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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.helix.common.cloud.CloudInformationFetcher;
import org.apache.helix.common.cloud.SourceType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AzureCloudInformationFetcher implements CloudInformationFetcher<String> {
  private final CloseableHttpClient _closeableHttpClient;
  private final List<String> _urls;
  private final List<String> _filePaths;
  private final List<String> _zookeeperPaths;

  public AzureCloudInformationFetcher(HashMap<SourceType, List<String>> sourceMap) {
    _urls = sourceMap.get(SourceType.URL);
    _filePaths = sourceMap.get(SourceType.FILE_PATH);
    _zookeeperPaths = sourceMap.get(SourceType.ZOOKEEPER_PATH);
    _closeableHttpClient = VMSSHttpUtil.getHttpClient();
  }

  private static final Logger LOG = LoggerFactory.getLogger(AzureCloudInformationFetcher.class.getName());

  /**
   * Get the Azure cloud information
   * @return Azure cloud information as a list of string
   */
  @Override
  public List<String> get() {
    List<String> response = new ArrayList<>();
    for (String url : _urls) {
      response.add(getAzureCloudInformationFromUrl(url));
    }
    for (String filePath : _filePaths) {
      response.add(getAzureCloudInformationFromFilePath(filePath));
    }
    for (String zookeeprPath : _zookeeperPaths) {
      response.add(getAzureCloudInformationFromZookeeperPath(zookeeprPath));
    }
    return response;
  }

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

  private String getAzureCloudInformationFromFilePath(String filePath) {
    //TODO: implement logic to retrieve information based on file path
    return null;
  }

  private String getAzureCloudInformationFromZookeeperPath(String zookeeperPath) {
    //TODO: implement logic to retrieve information based on zookeeper path
    return null;
  }
}


