package com.testingsyndicate.hc.jmx;

public interface PoolStatsMXBean {

  int getMaxTotal();

  void setMaxTotal(int max);

  int getDefaultMaxPerRoute();

  void setDefaultMaxPerRoute(int max);

  int getLeased();

  int getPending();

  int getAvailable();

  int getRoutesTotal();

}
