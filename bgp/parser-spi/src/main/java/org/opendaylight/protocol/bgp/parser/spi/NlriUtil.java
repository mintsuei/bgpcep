/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.parser.spi;

import org.opendaylight.protocol.bgp.parser.BGPParsingException;
import org.opendaylight.protocol.concepts.Ipv4Util;
import org.opendaylight.protocol.concepts.Ipv6Util;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130918.update.path.attributes.MpReachNlriBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.CNextHop;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.CIpv4NextHopBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.CIpv6NextHopBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.c.ipv4.next.hop.Ipv4NextHopBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.c.ipv6.next.hop.Ipv6NextHopBuilder;

public final class NlriUtil {
	private NlriUtil() {

	}

	public static void parseNextHop(final byte[] bytes, final MpReachNlriBuilder builder) throws BGPParsingException {
		final CNextHop addr;

		switch (bytes.length) {
		case 4:
			addr = new CIpv4NextHopBuilder().setIpv4NextHop(new Ipv4NextHopBuilder().setGlobal(Ipv4Util.addressForBytes(bytes)).build()).build();
			break;
		case 16:
			addr = new CIpv6NextHopBuilder().setIpv6NextHop(new Ipv6NextHopBuilder().setGlobal(Ipv6Util.addressForBytes(bytes)).build()).build();
			break;
		case 32:
			addr = new CIpv6NextHopBuilder().setIpv6NextHop(
					new Ipv6NextHopBuilder().setGlobal(Ipv6Util.addressForBytes(ByteArray.subByte(bytes, 0, 16))).setLinkLocal(
							Ipv6Util.addressForBytes(ByteArray.subByte(bytes, 16, 16))).build()).build();
			break;
		default:
			throw new BGPParsingException("Cannot parse NEXT_HOP attribute. Wrong bytes length: " + bytes.length);
		}

		builder.setCNextHop(addr);
	}
}