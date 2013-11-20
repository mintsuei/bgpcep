/**
* Generated file

* Generated from: yang module name: config-pcep-spi  yang module local name: pcep-extensions-impl
* Generated by: org.opendaylight.controller.config.yangjmxgenerator.plugin.JMXGenerator
* Generated at: Mon Nov 18 14:32:53 CET 2013
*
* Do not modify this file unless it is present under src/main directory
*/
package org.opendaylight.controller.config.yang.pcep.spi;

import org.opendaylight.protocol.pcep.spi.pojo.SimplePCEPExtensionProviderContext;

/**
*
*/
public final class SimplePCEPExtensionProviderContextModule extends org.opendaylight.controller.config.yang.pcep.spi.AbstractSimplePCEPExtensionProviderContextModule
{

    public SimplePCEPExtensionProviderContextModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public SimplePCEPExtensionProviderContextModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, SimplePCEPExtensionProviderContextModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void validate(){
        super.validate();
        // Add custom validation for module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
		final class PCEPExtensionProviderContextImplCloseable extends SimplePCEPExtensionProviderContext implements AutoCloseable {
			@Override
			public void close() {
				// Nothing to do
			}
		}

		return new PCEPExtensionProviderContextImplCloseable();
    }
}
