package com.testingsyndicate.hc.jmx;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PoolingHttpClientConnectionManagerMXBeanTest {

  private PoolingHttpClientConnectionManager mockConnectionManager;
  private PoolStats mockPoolStats;
  private PoolingHttpClientConnectionManagerMXBean sut;

  @BeforeEach
  void beforeEach() {

    mockPoolStats = mock(PoolStats.class);
    mockConnectionManager = mock(PoolingHttpClientConnectionManager.class);

    given(mockConnectionManager.getTotalStats()).willReturn(mockPoolStats);

    sut = new PoolingHttpClientConnectionManagerMXBean(mockConnectionManager);
  }

  @Test
  void returnsMaxTotal() {
    // given
    given(mockConnectionManager.getMaxTotal()).willReturn(99);

    // when
    int actual = sut.getMaxTotal();

    // then
    then(mockConnectionManager).should().getMaxTotal();
    assertThat(actual).isEqualTo(99);
  }

  @Test
  void returnsDefaultMaxPerRoute() {
    // given
    given(mockConnectionManager.getDefaultMaxPerRoute()).willReturn(100);

    // when
    int actual = sut.getDefaultMaxPerRoute();

    // then
    then(mockConnectionManager).should().getDefaultMaxPerRoute();
    assertThat(actual).isEqualTo(100);
  }

  @Test
  void returnsLeased() {
    // given
    given(mockPoolStats.getLeased()).willReturn(101);

    // when
    int actual = sut.getLeased();

    // then
    then(mockPoolStats).should().getLeased();
    assertThat(actual).isEqualTo(101);
  }

  @Test
  void returnsPending() {
    // given
    given(mockPoolStats.getPending()).willReturn(102);

    // when
    int actual = sut.getPending();

    // then
    then(mockPoolStats).should().getPending();
    assertThat(actual).isEqualTo(102);
  }

  @Test
  void returnsAvailable() {
    // given
    given(mockPoolStats.getAvailable()).willReturn(103);

    // when
    int actual = sut.getAvailable();

    // then
    then(mockPoolStats).should().getAvailable();
    assertThat(actual).isEqualTo(103);
  }

  @Test
  void returnsRoutesTotal() {
    // given
    Set<HttpRoute> mockRoutes = mock(Set.class);
    given(mockRoutes.size()).willReturn(3);
    given(mockConnectionManager.getRoutes()).willReturn(mockRoutes);

    // when
    int actual = sut.getRoutesTotal();

    // then
    then(mockConnectionManager).should().getRoutes();
    then(mockRoutes).should().size();
    assertThat(actual).isEqualTo(3);
  }

  @Test
  void setsMaxTotal() {
    // given
    int max = 3;

    // when
    sut.setMaxTotal(max);

    // then
    verify(mockConnectionManager).setMaxTotal(max);
  }

  @Test
  void setsDefaultMaxPerRoute() {
    // given
    int max = 5;

    // when
    sut.setDefaultMaxPerRoute(max);

    // then
    verify(mockConnectionManager).setDefaultMaxPerRoute(max);
  }

}
