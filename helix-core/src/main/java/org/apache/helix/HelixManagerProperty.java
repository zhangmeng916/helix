package org.apache.helix;

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

import java.util.Properties;
import org.apache.helix.model.CloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hold Helix participant properties. The participant properties further hold Helix cloud properties and some other properties
 * specific for the participant.
 */
public class HelixManagerProperty {
  private static final Logger LOG = LoggerFactory.getLogger(HelixManagerProperty.class.getName());
  private String _version;
  private long _healthReportLatency;
  private HelixCloudProperty _helixCloudProperty;

  /**
   * Initialize Helix manager property with default value
   * @param properties helix manager related properties input as a map
   * @param cloudConfig cloudConfig read from Zookeeper
   */
  public HelixManagerProperty(Properties properties, CloudConfig cloudConfig) {
    if (cloudConfig != null) {
      _helixCloudProperty = new HelixCloudProperty(cloudConfig);
    }
    setVersion(properties.getProperty(SystemPropertyKeys.HELIX_MANAGER_VERSION));
    setHealthReportLatency(properties.getProperty(SystemPropertyKeys.PARTICIPANT_HEALTH_REPORT_LATENCY));
  }

  public HelixCloudProperty getHelixCloudProperty() {
    return _helixCloudProperty;
  }

  public String getVersion() {
    return _version;
  }

  public long getHealthReportLatency() {
    return _healthReportLatency;
  }

  private void setHelixCloudProperty(HelixCloudProperty helixCloudProperty) {
    _helixCloudProperty = helixCloudProperty;
  }

  private void setVersion(String version) {
    _version = version;
  }

  private void setHealthReportLatency(String latency) {
    _healthReportLatency = Long.valueOf(latency);
  }

  //TODO: migrate all other participant related properties to this file.
}
