package org.apache.helix.cloud.azure;

import org.apache.helix.common.cloud.SourceType;


public enum AzureSourceType implements SourceType {
  URL,
  FILE_PATH,
  ZOOKEEPER_PATH
}
