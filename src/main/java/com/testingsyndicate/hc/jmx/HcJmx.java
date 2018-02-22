package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

public class HcJmx {

    private static final String JMX_DOMAIN = "org.apache.httpcomponents.httpclient";
    private static final String DEFAULT_NAME = "default-%s";
    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final MBeanServer SERVER = ManagementFactory.getPlatformMBeanServer();

    private HcJmx() {
    }

    public static ObjectName register(PoolingHttpClientConnectionManager connectionManager) throws JMException {
        String name = String.format(DEFAULT_NAME, COUNT.incrementAndGet());
        return register(connectionManager, name);
    }

    public static ObjectName register(PoolingHttpClientConnectionManager connectionManager, String name) throws JMException {
        PoolingHttpClientConnectionManagerMXBean bean = new PoolingHttpClientConnectionManagerMXBean(connectionManager);

        ObjectName jmxName = getObjectName(name);
        SERVER.registerMBean(bean, jmxName);
        return jmxName;
    }

    public static synchronized void unregister(ObjectName name) throws JMException {
        if (SERVER.isRegistered(name)) {
            SERVER.unregisterMBean(name);
        }
    }

    private static ObjectName getObjectName(String name) throws MalformedObjectNameException {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put("type", PoolingHttpClientConnectionManager.class.getSimpleName());
        properties.put("name", name);

        return ObjectName.getInstance(JMX_DOMAIN, properties);
    }

}
