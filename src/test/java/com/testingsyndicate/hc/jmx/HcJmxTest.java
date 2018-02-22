package com.testingsyndicate.hc.jmx;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class HcJmxTest {

    private static final MBeanServer SERVER = ManagementFactory.getPlatformMBeanServer();
    private PoolingHttpClientConnectionManager mockConnectionManager;

    @Before
    public void setup() {
        mockConnectionManager = mock(PoolingHttpClientConnectionManager.class);
    }

    @After
    public void tearDown() throws JMException {
        ObjectName query = new ObjectName("org.apache.httpcomponents.httpclient:name=*,type=PoolingHttpClientConnectionManager");
        Set<ObjectName> names = SERVER.queryNames(query, null);

        for (ObjectName name : names) {
            SERVER.unregisterMBean(name);
        }
    }

    @Test
    public void registersMBeanWithName() throws JMException {
        // given
        ObjectName name = HcJmx.register(mockConnectionManager, "wibble");

        // when
        MBeanInfo actual = SERVER.getMBeanInfo(name);

        // then
        assertThat(name.toString())
                .isEqualTo("org.apache.httpcomponents.httpclient:name=wibble,type=PoolingHttpClientConnectionManager");
        assertThat(actual).isNotNull();
    }

    @Test
    public void providesDefaultNameOnRegistration() throws JMException {
        // given

        // when
        ObjectName actual = HcJmx.register(mockConnectionManager);

        // then
        assertThat(actual.toString())
                .matches("org\\.apache\\.httpcomponents\\.httpclient:name=default-([a-f0-9-]{36}),type=PoolingHttpClientConnectionManager");
    }

    @Test
    public void unregistersMBean() throws JMException {
        // given
        ObjectName name = HcJmx.register(mockConnectionManager);

        // when
        HcJmx.unregister(name);
        boolean actual = SERVER.isRegistered(name);

        // then
        assertThat(actual).isFalse();
    }

}
