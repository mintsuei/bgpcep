/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.rib.impl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.opendaylight.protocol.bgp.rib.impl.spi.BGPSessionPreferences;
import org.opendaylight.protocol.bgp.rib.impl.spi.BGPSessionProposal;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.AsNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.open.BgpParametersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.open.bgp.parameters.OptionalCapabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.open.bgp.parameters.OptionalCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.As4BytesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.as4.bytes._case.As4BytesCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.GracefulRestartCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.MultiprotocolCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.graceful.restart._case.GracefulRestartCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130919.open.bgp.parameters.optional.capabilities.c.parameters.multiprotocol._case.MultiprotocolCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.AddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.SubsequentAddressFamily;

/**
 * Basic implementation of BGP Session Proposal. The values are taken from conf-bgp.
 */
public final class BGPSessionProposalImpl implements BGPSessionProposal {

    private final short holdTimer;

    private final AsNumber as;

    private final Ipv4Address bgpId;

    private final BGPSessionPreferences prefs;

    public BGPSessionProposalImpl(final short holdTimer, final AsNumber as, final Ipv4Address bgpId,
            final Map<Class<? extends AddressFamily>, Class<? extends SubsequentAddressFamily>> tables) {
        this.holdTimer = holdTimer;
        this.as = as;
        this.bgpId = bgpId;
        final List<OptionalCapabilities> caps = new ArrayList<>();

        for (final Entry<Class<? extends AddressFamily>, Class<? extends SubsequentAddressFamily>> e : tables.entrySet()) {
            caps.add(new OptionalCapabilitiesBuilder().setCParameters(
                    new MultiprotocolCaseBuilder().setMultiprotocolCapability(
                            new MultiprotocolCapabilityBuilder().setAfi(e.getKey()).setSafi(e.getValue()).build()).build()).build());
        }
        caps.add(new OptionalCapabilitiesBuilder().setCParameters(
                new As4BytesCaseBuilder().setAs4BytesCapability(new As4BytesCapabilityBuilder().setAsNumber(as).build()).build()).build());
        caps.add(new OptionalCapabilitiesBuilder().setCParameters(
                new GracefulRestartCaseBuilder().setGracefulRestartCapability(
                    new GracefulRestartCapabilityBuilder().build()).build()).build());
        this.prefs = new BGPSessionPreferences(as, holdTimer, bgpId, Lists.newArrayList(new BgpParametersBuilder().setOptionalCapabilities(caps).build()));
    }

    @Override
    public BGPSessionPreferences getProposal() {
        return this.prefs;
    }

    /**
     * @return the holdTimer
     */
    public short getHoldTimer() {
        return this.holdTimer;
    }

    /**
     * @return the as
     */
    public AsNumber getAs() {
        return this.as;
    }

    /**
     * @return the bgpId
     */
    public Ipv4Address getBgpId() {
        return this.bgpId;
    }
}
