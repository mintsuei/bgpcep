/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.impl.subobject;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeBitSet;
import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeIpv6Prefix;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.RROSubobjectParser;
import org.opendaylight.protocol.pcep.spi.RROSubobjectSerializer;
import org.opendaylight.protocol.pcep.spi.RROSubobjectUtil;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.protocol.util.Ipv6Util;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpPrefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.reported.route.object.rro.Subobject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.reported.route.object.rro.SubobjectBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.IpPrefixSubobject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.record.route.subobjects.subobject.type.IpPrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.record.route.subobjects.subobject.type.IpPrefixCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.record.route.subobjects.subobject.type.ip.prefix._case.IpPrefixBuilder;

/**
 * Parser for {@link IpPrefixCase}
 */
public class RROIpv6PrefixSubobjectParser implements RROSubobjectParser, RROSubobjectSerializer {

    public static final int TYPE = 2;

    private static final int PREFIX_F_LENGTH = 1;
    private static final int FLAGS_F_LENGTH = 1;

    private static final int PREFIX_F_OFFSET = Ipv6Util.IPV6_LENGTH;

    private static final int CONTENT_LENGTH = Ipv6Util.IPV6_LENGTH + PREFIX_F_LENGTH + FLAGS_F_LENGTH;

    private static final int LPA_F_OFFSET = 7;
    private static final int LPIU_F_OFFSET = 6;

    @Override
    public Subobject parseSubobject(final ByteBuf buffer) throws PCEPDeserializerException {
        Preconditions.checkArgument(buffer != null && buffer.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        final SubobjectBuilder builder = new SubobjectBuilder();
        if (buffer.readableBytes() != CONTENT_LENGTH) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + buffer.readableBytes() + ";");
        }
        final int length = UnsignedBytes.toInt(buffer.getByte(PREFIX_F_OFFSET));
        IpPrefixBuilder prefix = new IpPrefixBuilder().setIpPrefix(new IpPrefix(Ipv6Util.prefixForBytes(ByteArray.readBytes(buffer,
                Ipv6Util.IPV6_LENGTH), length)));
        buffer.readerIndex(buffer.readerIndex() + PREFIX_F_LENGTH);
        final BitSet flags = ByteArray.bytesToBitSet(ByteArray.readBytes(buffer, FLAGS_F_LENGTH));
        builder.setProtectionAvailable(flags.get(LPA_F_OFFSET));
        builder.setProtectionInUse(flags.get(LPIU_F_OFFSET));
        builder.setSubobjectType(new IpPrefixCaseBuilder().setIpPrefix(prefix.build()).build());
        return builder.build();
    }

    @Override
    public void serializeSubobject(final Subobject subobject, final ByteBuf buffer) {
        Preconditions.checkArgument(subobject.getSubobjectType() instanceof IpPrefixCase, "Unknown subobject instance. Passed %s. Needed IpPrefixCase.", subobject.getSubobjectType().getClass());
        final IpPrefixSubobject specObj = ((IpPrefixCase) subobject.getSubobjectType()).getIpPrefix();
        final IpPrefix prefix = specObj.getIpPrefix();
        final BitSet flags = new BitSet(FLAGS_F_LENGTH * Byte.SIZE);
        if (subobject.isProtectionAvailable() != null) {
            flags.set(LPA_F_OFFSET, subobject.isProtectionAvailable());
        }
        if (subobject.isProtectionInUse() != null) {
            flags.set(LPIU_F_OFFSET, subobject.isProtectionInUse());
        }
        final ByteBuf body = Unpooled.buffer(CONTENT_LENGTH);
        Preconditions.checkArgument(prefix.getIpv6Prefix() != null, "Ipv6Prefix is mandatory.");
        writeIpv6Prefix(prefix.getIpv6Prefix(), body);
        writeBitSet(flags, FLAGS_F_LENGTH, body);
        RROSubobjectUtil.formatSubobject(TYPE, body, buffer);
    }
}
