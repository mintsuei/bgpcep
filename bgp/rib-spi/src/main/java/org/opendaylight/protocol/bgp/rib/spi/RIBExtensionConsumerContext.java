/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.rib.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.AddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.SubsequentAddressFamily;

/**
 * Interface for acquiring AdjRIBsIn factories. In order for a model-driven RIB implementation to work correctly, it
 * has to know how to handle individual NLRI fields, whose encoding is specific to a AFI/SAFI pair. This interface
 * exposes an entry point for locating the AFI/SAFI-specific implementation handler.
 */
public interface RIBExtensionConsumerContext {
    /**
     * Acquire a RIB implementation factory for a AFI/SAFI combination.
     * @param afi Address Family Identifier
     * @param safi Subsequent Address Family identifier
     * @return RIB implementation factory, or null if the AFI/SAFI is
     *         not implemented.
     */
    @Nullable AdjRIBsFactory getAdjRIBsInFactory(@Nonnull Class<? extends AddressFamily> afi, @Nonnull Class<? extends SubsequentAddressFamily> safi);
}