package org.apache.helix.cloud.azure;

import org.apache.helix.HelixCloudPropertiesFactory;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class VMSSHttpUtil {

  private static Logger LOG = LoggerFactory.getLogger(VMSSHttpUtil.class.getName());

  static CloseableHttpClient getHttpClient() {
    return getHttpClient(Integer.parseInt(
        HelixCloudPropertiesFactory.getInstance().getHelixCloudProperties().getProperties()
            .getProperty("REQUEST_TIMEOUT_MS")));
  }

  private static CloseableHttpClient getHttpClient(int requestTimeout) {
    RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(requestTimeout)
        .setConnectTimeout(Integer.parseInt(
            HelixCloudPropertiesFactory.getInstance().getHelixCloudProperties().getProperties()
                .getProperty("CONNECTION_TIMEOUT_MS"))).build();
    return HttpClients.custom().setDefaultRequestConfig(config).setRetryHandler(getRetryHandler())
        .build();
  }

  private static HttpRequestRetryHandler getRetryHandler() {
    return (IOException exception, int executionCount, HttpContext context) -> {
      LOG.warn("Execution count: " + executionCount + ".", exception);
      return !(executionCount >= Integer.parseInt(
          HelixCloudPropertiesFactory.getInstance().getHelixCloudProperties().getProperties()
              .getProperty("RETRY_MAX")) || exception instanceof InterruptedIOException
          || exception instanceof UnknownHostException);
    };
  }
}