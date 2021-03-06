/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.segment.routing02;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.pojo.SimplePCEPExtensionProviderContext;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.SidType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.SrEroSubobject.Flags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.add.lsp.input.arguments.ero.subobject.subobject.type.SrEroTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.sr.ero.subobject.nai.IpAdjacencyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.sr.ero.subobject.nai.IpNodeIdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.segment.routing._02.rev140506.sr.ero.subobject.nai.UnnumberedAdjacencyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.ero.SubobjectBuilder;

public class SrEroSubobjectParserTest {

    private static final byte[] srEroSubobjectWithIpv4NodeID  = {
        0x05,0x0c,(byte) 0x10,0x00,
        0x00,0x01,(byte) 0xe2,0x40,
        0x4A,0x7D,0x2b,0x63,
    };

    private static final byte[] srEroSubobjectWithIpv6NodeID  = {
        0x05,0x18,(byte) 0x20,0x00,
        0x00,0x01,(byte) 0xe2,0x40,
        (byte) 0xFE,(byte) 0x80,(byte) 0xCD,0x00,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,
        0x21,0x1E,0x72,(byte) 0x9C,
    };

    private static final byte[] srEroSubobjectWithIpv4Adjacency  = {
        0x05,0x10,(byte) 0x30,0x00,
        0x00,0x01,(byte) 0xe2,0x40,
        0x4A,0x7D,0x2b,0x63,
        0x4A,0x7D,0x2b,0x64,
    };

    private static final byte[] srEroSubobjectWithIpv6Adjacency  = {
        0x05,0x28,(byte) 0x40,0x00,
        0x00,0x01,(byte) 0xe2,0x40,
        (byte) 0xFE,(byte) 0x80,(byte) 0xCD,0x00,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,
        0x21,0x1E,0x72,(byte) 0x9C,
        (byte) 0xFE,(byte) 0x80,(byte) 0xCD,0x00,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,
        0x21,0x1E,0x72,(byte) 0x9D,
    };

    private static final byte[] srEroSubobjectWithUnnumbered  = {
        0x05,0x18,(byte) 0x50,0x00,
        0x00,0x01,(byte) 0xe2,0x40,
        0x00,0x00,0x00,0x01,
        0x00,0x00,0x00,0x02,
        0x00,0x00,0x00,0x03,
        0x00,0x00,0x00,0x04
    };

    private static final byte[] srEroSubobjectWithoutNAI  = {
        0x05,0x08,(byte) 0x10,0x08,
        0x00,0x01,(byte) 0xe2,0x40,
    };

    private static final byte[] srEroSubobjectWithoutSID  = {
        0x05,0x08,(byte) 0x10,0x04,
        0x4A,0x7D,0x2b,0x63,
    };

    private SimplePCEPExtensionProviderContext ctx;
    private SegmentRoutingActivator act;

    @Before
    public void setUp() {
        this.ctx = new SimplePCEPExtensionProviderContext();
        this.act = new SegmentRoutingActivator();
        this.act.start(this.ctx);
    }

    @Test
    public void testSrEroSubobjectIpv4NodeIdNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, false));
        builder.setSidType(SidType.Ipv4NodeId);
        builder.setSid(123456L);
        builder.setNai(new IpNodeIdBuilder().setIpAddress(new IpAddress(new Ipv4Address("74.125.43.99"))).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithIpv4NodeID, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithIpv4NodeID, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectIpv6NodeIdNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, false));
        builder.setSidType(SidType.Ipv6NodeId);
        builder.setSid(123456L);
        builder.setNai(new IpNodeIdBuilder().setIpAddress(new IpAddress(new Ipv6Address("fe80:cd00::211e:729c"))).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithIpv6NodeID, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithIpv6NodeID, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectIpv4AdjacencyNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, false));
        builder.setSidType(SidType.Ipv4Adjacency);
        builder.setSid(123456L);
        builder.setNai(new IpAdjacencyBuilder().setLocalIpAddress(new IpAddress(new Ipv4Address("74.125.43.99")))
                .setRemoteIpAddress(new IpAddress(new Ipv4Address("74.125.43.100"))).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithIpv4Adjacency, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithIpv4Adjacency, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectIpv6AdjacencyNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, false));
        builder.setSidType(SidType.Ipv6Adjacency);
        builder.setSid(123456L);
        builder.setNai(new IpAdjacencyBuilder().setLocalIpAddress(new IpAddress(new Ipv6Address("fe80:cd00::211e:729c")))
                .setRemoteIpAddress(new IpAddress(new Ipv6Address("fe80:cd00::211e:729d"))).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithIpv6Adjacency, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithIpv6Adjacency, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectUnnumberedNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, false));
        builder.setSidType(SidType.Unnumbered);
        builder.setSid(123456L);
        builder.setNai(new UnnumberedAdjacencyBuilder().setLocalNodeId(1L).setLocalInterfaceId(2L).setRemoteNodeId(3L).setRemoteInterfaceId(4L).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithUnnumbered, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithUnnumbered, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectWithoutNAI() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, true, false, false));
        builder.setSidType(SidType.Ipv4NodeId);
        builder.setSid(123456L);
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithoutNAI, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithoutNAI, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testSrEroSubobjectWithoutBody() throws PCEPDeserializerException {
        final SrEroSubobjectParser parser = new SrEroSubobjectParser();
        final SrEroTypeBuilder builder = new SrEroTypeBuilder();
        builder.setFlags(new Flags(false, false, false, true));
        builder.setSidType(SidType.Ipv4NodeId);
        builder.setNai(new IpNodeIdBuilder().setIpAddress(new IpAddress(new Ipv4Address("74.125.43.99"))).build());
        final SubobjectBuilder subobjBuilder = new SubobjectBuilder().setSubobjectType(builder.build()).setLoose(false);

        assertEquals(subobjBuilder.build(), parser.parseSubobject(Unpooled.wrappedBuffer(ByteArray.cutBytes(srEroSubobjectWithoutSID, 2)), false));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeSubobject(subobjBuilder.build(), buffer);
        assertArrayEquals(srEroSubobjectWithoutSID, ByteArray.getAllBytes(buffer));
    }
}
