package com.testingsyndicate.hc.jmx;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.management.*;
import java.lang.management.ManagementFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HcJmxTest {

  private MBeanServer mockServer;
  private PoolingHttpClientConnectionManager mockConnectionManager;
  private HcJmx sut;

  @BeforeEach
  void beforeEach() {
    mockServer = mock(MBeanServer.class);
    mockConnectionManager = mock(PoolingHttpClientConnectionManager.class);

    sut = new HcJmx(mockServer);
  }

  @Test
  void staticInstanceUsesPlatformMBeanServer() {
    // given

    // when
    sut = HcJmx.getInstance();

    // then
    assertThat(sut)
        .hasFieldOrPropertyWithValue("server", ManagementFactory.getPlatformMBeanServer());
  }

  @Test
  void registersMBeanWithName() throws JMException {
    // given

    // when
    ObjectName actual = sut.register(mockConnectionManager, "wibble");

    // then
    assertThat(actual.toString())
        .isEqualTo("org.apache.httpcomponents.httpclient:name=wibble,type=PoolingHttpClientConnectionManager");
    verify(mockServer).registerMBean(any(PoolingHttpClientConnectionManagerMXBean.class), eq(actual));
  }

  @Test
  void registersMBeanWithPool() throws JMException {
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
  void providesDefaultNameOnRegistration() {
    // given

    // when
    ObjectName actual = sut.register(mockConnectionManager);

    // then
    assertThat(actual.toString())
        .matches("org\\.apache\\.httpcomponents\\.httpclient:name=default-([a-f0-9-]{36}),type=PoolingHttpClientConnectionManager");
  }

  @Test
  void unregistersMBean() throws JMException {
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
  void doesntUnregisterMissingMBean() throws JMException {
    // given
    ObjectName name = ObjectName.getInstance("com.testingsyndicate:name=wibble-wobble");
    when(mockServer.isRegistered(any(ObjectName.class))).thenReturn(false);

    // when
    sut.unregister(name);

    // then
    verify(mockServer).isRegistered(eq(name));
    verify(mockServer, never()).unregisterMBean(any(ObjectName.class));
  }

  @Test
  void wrapsExceptionWhenServerException() throws JMException {
    // given
    MBeanRegistrationException cause = new MBeanRegistrationException(new RuntimeException());
    when(mockServer.registerMBean(any(), any(ObjectName.class)))
        .thenThrow(cause);

    // when
    try {
      sut.register(mockConnectionManager);
      shouldHaveThrown(HcJmxException.class);
    } catch (HcJmxException actual) {
      // then
      assertThat(actual)
          .hasMessage("Unable to register")
          .hasCause(cause);
    }
  }

  @Test
  void throwsExceptionWhenNoManager() {
    // given
    HttpClient client = new NoManager();

    // when
    try {
      sut.register(client);
      shouldHaveThrown(HcJmxException.class);
    } catch (HcJmxException actual) {
      // then
      assertThat(actual)
          .hasMessage("Unable to extract ConnectionManager from HttpClient")
          .hasCauseInstanceOf(NoSuchFieldException.class);
    }
  }

  @Test
  void throwsExceptionWhenNullManager() {
    // given
    HttpClient client = new HasManager(null);

    // when
    try {
      sut.register(client);
      shouldHaveThrown(HcJmxException.class);
    } catch (HcJmxException actual) {
      // then
      assertThat(actual)
          .hasMessage("HttpClient has no ConnectionManager")
          .hasNoCause();
    }
  }

  @Test
  void throwsExceptionWhenNotPool() {
    // given
    HttpClient client = new HasManager(mock(HttpClientConnectionManager.class));

    // when
    try {
      sut.register(client);
      shouldHaveThrown(HcJmxException.class);
    } catch (HcJmxException actual) {
      // then
      assertThat(actual)
          .hasMessage("HttpClient is using an unsupported HttpClientConnectionManager")
          .hasCauseInstanceOf(ClassCastException.class);
    }
  }

  @Test
  void registersMBeanWhenClientContainsPool() throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
    // given
    HttpClient client = new HasManager(mockConnectionManager);

    // when
    sut.register(client);
    ArgumentCaptor<PoolingHttpClientConnectionManagerMXBean> captor = ArgumentCaptor.forClass(PoolingHttpClientConnectionManagerMXBean.class);
    verify(mockServer).registerMBean(captor.capture(), any(ObjectName.class));
    PoolingHttpClientConnectionManagerMXBean actual = captor.getValue();

    // then
    assertThat(actual)
        .isNotNull()
        .hasFieldOrPropertyWithValue("connectionManager", mockConnectionManager);
  }

  @Test
  void registersWithNameWhenClientContainsPool() {
    // given
    HttpClient client = new HasManager(mockConnectionManager);

    // when
    ObjectName actual = sut.register(client, "my-client");

    // then
    assertThat(actual.toString())
        .isEqualTo("org.apache.httpcomponents.httpclient:name=my-client,type=PoolingHttpClientConnectionManager");
  }

  private static final class HasManager extends TestClient {

    private HttpClientConnectionManager connManager;

    HasManager(HttpClientConnectionManager manager) {
      this.connManager = manager;
    }

  }

  private static final class NoManager extends TestClient { }

}
