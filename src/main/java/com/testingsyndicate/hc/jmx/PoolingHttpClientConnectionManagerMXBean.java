package com.testingsyndicate.hc.jmx;

import javax.management.StandardMBean;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

class PoolingHttpClientConnectionManagerMXBean extends StandardMBean implements PoolStatsMXBean {

  private final PoolingHttpClientConnectionManager connectionManager;

  PoolingHttpClientConnectionManagerMXBean(PoolingHttpClientConnectionManager connectionManager) {
    super(PoolStatsMXBean.class, true);
    this.connectionManager = connectionManager;
  }

  @Override
  public int getMaxTotal() {
    return connectionManager.getMaxTotal();
  }

  @Override
  public void setMaxTotal(int max) {
    connectionManager.setMaxTotal(max);
  }

  @Override
  public int getDefaultMaxPerRoute() {
    return connectionManager.getDefaultMaxPerRoute();
  }

  @Override
  public void setDefaultMaxPerRoute(int max) {
    connectionManager.setDefaultMaxPerRoute(max);
  }

  @Override
  public int getLeased() {
    return connectionManager.getTotalStats().getLeased();
  }

  @Override
  public int getPending() {
    return connectionManager.getTotalStats().getPending();
  }

  @Override
  public int getAvailable() {
    return connectionManager.getTotalStats().getAvailable();
  }

  @Override
  public int getRoutesTotal() {
    return connectionManager.getRoutes().size();
  }
}
