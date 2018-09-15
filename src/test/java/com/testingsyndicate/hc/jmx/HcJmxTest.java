package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HcJmxTest {

  private MBeanServer mockServer;
  private PoolingHttpClientConnectionManager mockConnectionManager;
  private HcJmx sut;

  @Before
  public void setup() {
    mockServer = mock(MBeanServer.class);
    mockConnectionManager = mock(PoolingHttpClientConnectionManager.class);

    sut = new HcJmx(mockServer);
  }

  @Test
  public void staticInstanceUsesPlatformMBeanServer() {
    // given

    // when
    sut = HcJmx.getInstance();

    // then
    assertThat(sut)
        .hasFieldOrPropertyWithValue("server", ManagementFactory.getPlatformMBeanServer());
  }

  @Test
  public void registersMBeanWithName() throws JMException {
    // given

    // when
    ObjectName actual = sut.register(mockConnectionManager, "wibble");

    // then
    assertThat(actual.toString())
        .isEqualTo("org.apache.httpcomponents.httpclient:name=wibble,type=PoolingHttpClientConnectionManager");
    verify(mockServer).registerMBean(any(PoolingHttpClientConnectionManagerMXBean.class), eq(actual));
  }

  @Test
  public void registersMBeanWithPool() throws JMException {
    // given

    // when
    sut.register(mockConnectionManager, "wibble");
    ArgumentCaptor<PoolingHttpClientConnectionManagerMXBean> captor = ArgumentCaptor.forClass(PoolingHttpClientConnectionManagerMXBean.class);
    verify(mockServer).registerMBean(captor.capture(), any(ObjectName.class));
    PoolingHttpClientConnectionManagerMXBean actual = captor.getValue();

    // then
    assertThat(actual)
        .isNotNull()
        .hasFieldOrPropertyWithValue("connectionManager", mockConnectionManager);
  }

  @Test
  public void providesDefaultNameOnRegistration() throws JMException {
    // given

    // when
    ObjectName actual = sut.register(mockConnectionManager);

    // then
    assertThat(actual.toString())
        .matches("org\\.apache\\.httpcomponents\\.httpclient:name=default-([a-f0-9-]{36}),type=PoolingHttpClientConnectionManager");
  }

  @Test
  public void unregistersMBean() throws JMException {
    // given
    ObjectName name = ObjectName.getInstance("com.testingsyndicate:name=wibble-wobble");
    when(mockServer.isRegistered(any(ObjectName.class))).thenReturn(true);

    // when
    sut.unregister(name);

    // then
    verify(mockServer).isRegistered(eq(name));
    verify(mockServer).unregisterMBean(eq(name));
  }

  @Test
  public void doesntUnregisterMissingMBean() throws JMException {
    // given
    ObjectName name = ObjectName.getInstance("com.testingsyndicate:name=wibble-wobble");
    when(mockServer.isRegistered(any(ObjectName.class))).thenReturn(false);

    // when
    sut.unregister(name);

    // then
    verify(mockServer).isRegistered(eq(name));
    verify(mockServer, never()).unregisterMBean(any(ObjectName.class));
  }

}
