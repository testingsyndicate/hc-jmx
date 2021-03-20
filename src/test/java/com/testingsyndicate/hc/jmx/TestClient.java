package com.testingsyndicate.hc.jmx;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class TestClient extends CloseableHttpClient {
  @Override
  protected CloseableHttpResponse doExecute(
      HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext)
      throws IOException, ClientProtocolException {
    return null;
  }

  @Override
  public void close() throws IOException {}

  @Override
  public HttpParams getParams() {
    return null;
  }

  @Override
  public ClientConnectionManager getConnectionManager() {
    return null;
  }
}
