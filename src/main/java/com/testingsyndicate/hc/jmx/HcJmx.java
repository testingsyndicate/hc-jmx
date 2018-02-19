package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

public class HcJmx
    extends StandardMBean implements PoolStatsMXBean {

    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final MBeanServer SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final String JMX_DOMAIN = "org.apache.httpcomponents.httpclient";
    private static final String DEFAULT_NAME = "default-%s";

    private final PoolingHttpClientConnectionManager connectionManager;

    HcJmx(PoolingHttpClientConnectionManager connectionManager) {
        super(PoolStatsMXBean.class, true);
        this.connectionManager = connectionManager;
    }

    public static ObjectName register(PoolingHttpClientConnectionManager connectionManager) throws JMException {
        String name = String.format(DEFAULT_NAME, COUNT.incrementAndGet());
        return register(connectionManager, name);
    }

    public static ObjectName register(PoolingHttpClientConnectionManager connectionManager, String name) throws JMException {
        HcJmx bean = new HcJmx(connectionManager);

        ObjectName jmxName = getObjectName(name);
        SERVER.registerMBean(bean, jmxName);
        return jmxName;
    }

    public static void unregister(ObjectName name) throws JMException {
        if (SERVER.isRegistered(name)) {
            SERVER.unregisterMBean(name);
        }
    }

    @Override
    public int getMaxTotal() {
        return connectionManager.getMaxTotal();
    }

    @Override
    public int getDefaultMaxPerRoute() {
        return connectionManager.getDefaultMaxPerRoute();
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
    public int getMax() {
        return connectionManager.getTotalStats().getMax();
    }

    @Override
    public int getRoutesTotal() {
        return connectionManager.getRoutes().size();
    }

    private static ObjectName getObjectName(String name) throws MalformedObjectNameException {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put("type", PoolingHttpClientConnectionManager.class.getSimpleName());
        properties.put("name", name);

        return ObjectName.getInstance(JMX_DOMAIN, properties);
    }
}
