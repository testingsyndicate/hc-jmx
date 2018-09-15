package com.testingsyndicate.hc.jmx;

public interface PoolStatsMXBean {

  int getMaxTotal();

  int getDefaultMaxPerRoute();

  int getLeased();

  int getPending();

  int getAvailable();

  int getMax();

  int getRoutesTotal();

}
