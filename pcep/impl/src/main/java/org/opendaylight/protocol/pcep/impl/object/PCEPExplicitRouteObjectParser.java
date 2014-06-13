/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.impl.object;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.opendaylight.protocol.pcep.spi.EROSubobjectRegistry;
import org.opendaylight.protocol.pcep.spi.ObjectUtil;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.Ero;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.EroBuilder;

/**
 * Parser for {@link Ero}
 */
public class PCEPExplicitRouteObjectParser extends AbstractEROWithSubobjectsParser {

    public static final int CLASS = 7;

    public static final int TYPE = 1;

    public PCEPExplicitRouteObjectParser(final EROSubobjectRegistry subobjReg) {
        super(subobjReg);
    }

    @Override
    public Ero parseObject(final ObjectHeader header, final ByteBuf buffer) throws PCEPDeserializerException {
        // Explicit approval of empty ERO
        Preconditions.checkArgument(buffer != null, "Array of bytes is mandatory. Can't be null.");
        final EroBuilder builder = new EroBuilder();
        builder.setIgnore(header.isIgnore());
        builder.setProcessingRule(header.isProcessingRule());
        builder.setSubobject(parseSubobjects(buffer));
        return builder.build();
    }

    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Ero, "Wrong instance of PCEPObject. Passed %s. Needed EroObject.", object.getClass());
        final Ero ero = ((Ero) object);
        final ByteBuf body = Unpooled.buffer();
        // FIXME: switch to ByteBuf
        final byte[] bytes = serializeSubobject(ero.getSubobject());
        body.writeBytes(bytes);
        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
    }
}
