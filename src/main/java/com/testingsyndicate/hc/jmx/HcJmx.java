package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.UUID;

public final class HcJmx {

  private static final String JMX_DOMAIN = "org.apache.httpcomponents.httpclient";
  private static final String DEFAULT_NAME = "default-%s";
  private static final HcJmx INSTANCE = new HcJmx(ManagementFactory.getPlatformMBeanServer());

  private final MBeanServer server;

  HcJmx(MBeanServer server) {
    this.server = server;
  }

  /**
   * Get an instance of {@link HcJmx} that interfaces with the default PlatformMBeanServer
   *
   * @return the instance of {@link HcJmx}
   */
  public static HcJmx getInstance() {
    return INSTANCE;
  }

  /**
   * Registers a {@link PoolingHttpClientConnectionManager} with JMX, using a default generated name
   *
   * @param connectionManager {@link PoolingHttpClientConnectionManager} to be registered with JMX
   * @return {@link ObjectName} pointer to the registered instance
   * @throws JMException thrown if the {@link PoolingHttpClientConnectionManager} cannot be registered
   */
  public ObjectName register(PoolingHttpClientConnectionManager connectionManager) throws JMException {
    String name = String.format(DEFAULT_NAME, UUID.randomUUID());
    return register(connectionManager, name);
  }

  /**
   * Registers a {@link PoolingHttpClientConnectionManager} with JMX, using a supplied name
   *
   * @param connectionManager {@link PoolingHttpClientConnectionManager} to be registered with JMX
   * @param name name to be used when registering the MXBean with JMX
   * @return {@link ObjectName} pointer to the registered instance
   * @throws JMException thrown if the {@link PoolingHttpClientConnectionManager} cannot be registered
   */
  public ObjectName register(PoolingHttpClientConnectionManager connectionManager, String name) throws JMException {
    PoolingHttpClientConnectionManagerMXBean bean = new PoolingHttpClientConnectionManagerMXBean(connectionManager);

    ObjectName jmxName = getObjectName(name);
    server.registerMBean(bean, jmxName);
    return jmxName;
  }

  /**
   * Unregisters an MXBean from JMX
   *
   * @param name {@link ObjectName} to unregister
   * @throws JMException if the MXBean cannot be unregistered
   */
  public synchronized void unregister(ObjectName name) throws JMException {
    if (server.isRegistered(name)) {
      server.unregisterMBean(name);
    }
  }

  private static ObjectName getObjectName(String name) throws MalformedObjectNameException {
    Hashtable<String, String> properties = new Hashtable<>();
    properties.put("type", PoolingHttpClientConnectionManager.class.getSimpleName());
    properties.put("name", name);

    return ObjectName.getInstance(JMX_DOMAIN, properties);
  }

}
