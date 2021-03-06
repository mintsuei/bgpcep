/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.spi;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.iana.rev130816.EnterpriseNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.message.rev131007.Pcerr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.message.rev131007.Pcrep;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.message.rev131007.PcrepBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcep.error.object.ErrorObject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcep.error.object.ErrorObjectBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcrep.message.PcrepMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcrep.message.pcrep.message.RepliesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.rp.object.Rp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.vendor.information.objects.VendorInformationObject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.vendor.information.objects.VendorInformationObjectBuilder;

public class AbstractMessageParserTest {

    private static final EnterpriseNumber EN = new EnterpriseNumber(0L);

    private Object object;

    private VendorInformationObject viObject;

    @Mock
    private ObjectRegistry registry;

    @Mock
    private VendorInformationObjectRegistry viRegistry;

    private class Abs extends AbstractMessageParser {

        protected Abs(ObjectRegistry registry) {
            super(registry);
        }

        protected Abs(ObjectRegistry registry, VendorInformationObjectRegistry viRegistry) {
            super(registry, viRegistry);
        }

        @Override
        public void serializeMessage(Message message, ByteBuf buffer) {
        }

        @Override
        protected Message validate(List<Object> objects, List<Message> errors) throws PCEPDeserializerException {
            if (objects.get(0) instanceof VendorInformationObject) {
                final RepliesBuilder repsBuilder = new RepliesBuilder();
                repsBuilder.setVendorInformationObject(addVendorInformationObjects(objects));
                final PcrepBuilder builder = new PcrepBuilder();
                builder.setPcrepMessage(new PcrepMessageBuilder().setReplies(Lists.newArrayList(repsBuilder.build())).build());
                return builder.build();
            } else if (objects.get(0) instanceof ErrorObject) {
                final short errorType = ((ErrorObject) objects.get(0)).getType();
                final short errorValue = ((ErrorObject) objects.get(0)).getValue();
                return createErrorMsg(PCEPErrors.forValue(errorType, errorValue), Optional.<Rp>absent());
            }
            return null;
        }
    };

    @Before
    public void setUp() throws PCEPDeserializerException {
        MockitoAnnotations.initMocks(this);
        this.object = new ErrorObjectBuilder().setType((short) 1).setValue((short) 1).build();
        this.viObject = new VendorInformationObjectBuilder().setEnterpriseNumber(EN).build();
        Mockito.doNothing().when(this.viRegistry).serializeVendorInformationObject(Mockito.any(VendorInformationObject.class), Mockito.any(ByteBuf.class));
        Mockito.doReturn(Optional.of(this.viObject)).when(this.viRegistry).parseVendorInformationObject(EN, new ObjectHeaderImpl(true, true), Unpooled.wrappedBuffer(new byte[] { 0, 0, 0, 0 }));
        Mockito.doNothing().when(this.registry).serializeObject(Mockito.any(Object.class), Mockito.any(ByteBuf.class));
        Mockito.doReturn(this.object).when(this.registry).parseObject(13, 1, new ObjectHeaderImpl(true, true), Unpooled.wrappedBuffer(new byte[] { 0, 0, 1, 1 }));
    }

    @Test
    public void testParseObjects() throws PCEPDeserializerException {
        Abs a = new Abs(this.registry);
        ByteBuf buffer = Unpooled.buffer();
        a.serializeObject(this.object, buffer);

        Mockito.verify(this.registry, Mockito.only()).serializeObject(Mockito.any(Object.class), Mockito.any(ByteBuf.class));

        Message b = a.parseMessage(Unpooled.wrappedBuffer(new byte[] {0x0D, 0x13, 0, 0x08, 0, 0, 1, 1 }), Collections.<Message> emptyList());

        assertEquals(this.object, ((Pcerr) b).getPcerrMessage().getErrors().get(0).getErrorObject());
    }

    @Test
    public void testParseVendorInformationObject() throws PCEPDeserializerException {
        final Abs parser = new Abs(this.registry, this.viRegistry);
        final ByteBuf buffer = Unpooled.buffer();

        parser.serializeVendorInformationObjects(Lists.newArrayList(viObject), buffer);
        Mockito.verify(this.viRegistry, Mockito.only()).serializeVendorInformationObject(Mockito.any(VendorInformationObject.class), Mockito.any(ByteBuf.class));

        final Message msg = parser.parseMessage(Unpooled.wrappedBuffer(new byte[] {0x22, 0x13, 0x00, 0x08, 0, 0, 0, 0 }), Collections.<Message> emptyList());

        assertEquals(this.viObject, ((Pcrep)msg).getPcrepMessage().getReplies().get(0).getVendorInformationObject().get(0));
    }
}
