package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;

public class HCJMX
    extends StandardMBean implements PoolStatsMXBean {

    private static final MBeanServer SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final String JMX_DOMAIN = "com.testingsyndicate.httpclient";

    private final PoolingHttpClientConnectionManager connectionManager;

    HCJMX(PoolingHttpClientConnectionManager connectionManager) {
        super(PoolStatsMXBean.class, true);
        this.connectionManager = connectionManager;
    }

    public static void register(PoolingHttpClientConnectionManager connectionManager, String name) throws JMException {
        HCJMX bean = new HCJMX(connectionManager);

        SERVER.registerMBean(bean, getObjectName(name));
    }

    public static void unregister(String name) throws JMException {
        ObjectName jmxName = getObjectName(name);

        if (SERVER.isRegistered(jmxName)) {
            SERVER.unregisterMBean(jmxName);
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
