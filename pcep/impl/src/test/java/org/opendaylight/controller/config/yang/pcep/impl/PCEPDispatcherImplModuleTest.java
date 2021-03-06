/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.config.yang.pcep.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectName;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.config.api.ValidationException;
import org.opendaylight.controller.config.api.jmx.CommitStatus;
import org.opendaylight.controller.config.manager.impl.AbstractConfigTest;
import org.opendaylight.controller.config.manager.impl.factoriesresolver.HardcodedModuleFactoriesResolver;
import org.opendaylight.controller.config.util.ConfigTransactionJMXClient;
import org.opendaylight.controller.config.yang.netty.threadgroup.NettyThreadgroupModuleFactory;
import org.opendaylight.controller.config.yang.netty.threadgroup.NettyThreadgroupModuleMXBean;
import org.opendaylight.controller.config.yang.pcep.spi.SimplePCEPExtensionProviderContextModuleFactory;
import org.opendaylight.controller.config.yang.pcep.spi.SimplePCEPExtensionProviderContextModuleMXBean;

public class PCEPDispatcherImplModuleTest extends AbstractConfigTest {

    private static final String INSTANCE_NAME = "pcep-dispatcher-impl";
    private static final String FACTORY_NAME = PCEPDispatcherImplModuleFactory.NAME;

    private static final String THREADGROUP_FACTORY_NAME = NettyThreadgroupModuleFactory.NAME;
    private static final String BOSS_TG_INSTANCE_NAME = "boss-group";
    private static final String WORKER_TG_INSTANCE_NAME = "worker-group";

    private static final String EXTENSION_INSTANCE_NAME = "pcep-extension-privider";
    private static final String EXTENSION_FACTORYNAME = SimplePCEPExtensionProviderContextModuleFactory.NAME;

    @Before
    public void setUp() throws Exception {
        super.initConfigTransactionManagerImpl(new HardcodedModuleFactoriesResolver(this.mockedContext, new PCEPDispatcherImplModuleFactory(), new PCEPSessionProposalFactoryImplModuleFactory(), new NettyThreadgroupModuleFactory(), new SimplePCEPExtensionProviderContextModuleFactory()));
    }

    @Test
    public void testValidationExceptionMaxUnknownMessagesNotSet() throws Exception {
        try {
            createDispatcherInstance(null);
            fail();
        } catch (final ValidationException e) {
            assertTrue(e.getMessage().contains("MaxUnknownMessages value is not set"));
        }
    }

    @Test
    public void testValidationExceptionMaxUnknownMessagesMinValue() throws Exception {
        try {
            createDispatcherInstance(0);
            fail();
        } catch (final ValidationException e) {
            assertTrue(e.getMessage().contains("must be greater than 0"));
        }
    }

    @Test
    public void testCreateBean() throws Exception {
        final CommitStatus status = createDispatcherInstance(5);
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 5, 0, 0);
    }

    @Test
    public void testReusingOldInstance() throws Exception {
        createDispatcherInstance(5);
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        final CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 0, 5);
    }

    @Test
    public void testReconfigure() throws Exception {
        createDispatcherInstance(5);
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        final PCEPDispatcherImplModuleMXBean mxBean = transaction.newMXBeanProxy(transaction.lookupConfigBean(FACTORY_NAME, INSTANCE_NAME),
                PCEPDispatcherImplModuleMXBean.class);
        mxBean.setMaxUnknownMessages(10);
        final CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 1, 4);
    }

    private CommitStatus createDispatcherInstance(final Integer maxUnknownMessages) throws Exception {
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        createDispatcherInstance(transaction, maxUnknownMessages);
        return transaction.commit();
    }

    public static ObjectName createDispatcherInstance(final ConfigTransactionJMXClient transaction, final Integer maxUnknownMessages)
            throws Exception {
        final ObjectName nameCreated = transaction.createModule(FACTORY_NAME, INSTANCE_NAME);
        final PCEPDispatcherImplModuleMXBean mxBean = transaction.newMXBeanProxy(nameCreated, PCEPDispatcherImplModuleMXBean.class);
        mxBean.setPcepSessionProposalFactory(PCEPSessionProposalFactoryImplModuleTest.createSessionInstance(transaction));
        mxBean.setMaxUnknownMessages(maxUnknownMessages);
        mxBean.setBossGroup(createThreadGroupInstance(transaction, 10, BOSS_TG_INSTANCE_NAME));
        mxBean.setWorkerGroup(createThreadGroupInstance(transaction, 10, WORKER_TG_INSTANCE_NAME));
        mxBean.setPcepExtensions(createExtensionsInstance(transaction));
        return nameCreated;
    }

    private static ObjectName createExtensionsInstance(final ConfigTransactionJMXClient transaction) throws InstanceAlreadyExistsException {
        final ObjectName nameCreated = transaction.createModule(EXTENSION_FACTORYNAME, EXTENSION_INSTANCE_NAME);
        transaction.newMXBeanProxy(nameCreated, SimplePCEPExtensionProviderContextModuleMXBean.class);

        return nameCreated;
    }

    private static ObjectName createThreadGroupInstance(final ConfigTransactionJMXClient transaction, final Integer threadCount,
            final String instanceName) throws InstanceAlreadyExistsException {
        final ObjectName nameCreated = transaction.createModule(THREADGROUP_FACTORY_NAME, instanceName);
        final NettyThreadgroupModuleMXBean mxBean = transaction.newMXBeanProxy(nameCreated, NettyThreadgroupModuleMXBean.class);
        mxBean.setThreadCount(threadCount);
        return nameCreated;
    }

}
