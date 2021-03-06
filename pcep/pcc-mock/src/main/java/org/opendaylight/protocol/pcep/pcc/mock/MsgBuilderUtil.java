/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcc.mock;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import org.opendaylight.protocol.pcep.spi.PCEPErrors;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.OperationalStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Pcrpt;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PcrptBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PlspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.SrpIdNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.SymbolicPathName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.LspIdentifiersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.Lsp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.LspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcerr.pcerr.message.error.type.StatefulCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcerr.pcerr.message.error.type.stateful._case.StatefulBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcerr.pcerr.message.error.type.stateful._case.stateful.SrpsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.PcrptMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.pcrpt.message.ReportsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.pcrpt.message.reports.Path;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.pcrpt.message.reports.PathBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.Srp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.SrpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.symbolic.path.name.tlv.SymbolicPathNameBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.message.rev131007.PcerrBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.EroBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.ero.Subobject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcep.error.object.ErrorObjectBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcerr.message.PcerrMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcerr.message.pcerr.message.ErrorsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.Ipv4ExtendedTunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.LspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.TunnelId;

public final class MsgBuilderUtil {

    private MsgBuilderUtil() {
        throw new UnsupportedOperationException();
    }

    public static Pcrpt createPcRtpMessage(final Lsp lsp, final Optional<Srp> srp, final Path path) {
        final PcrptBuilder rptBuilder = new PcrptBuilder();
        final PcrptMessageBuilder msgBuilder = new PcrptMessageBuilder();
        final ReportsBuilder reportBuilder = new ReportsBuilder();
        reportBuilder.setLsp(lsp);
        reportBuilder.setSrp(srp.orNull());
        reportBuilder.setPath(path);
        msgBuilder.setReports(Lists.newArrayList(reportBuilder.build()));
        rptBuilder.setPcrptMessage(msgBuilder.build());
        return rptBuilder.build();
    }

    public static Lsp createLsp(final long plspId, final boolean sync, final Optional<Tlvs> tlvs) {
        final LspBuilder lspBuilder = new LspBuilder();
        lspBuilder.setAdministrative(true);
        lspBuilder.setDelegate(true);
        lspBuilder.setIgnore(false);
        lspBuilder.setOperational(OperationalStatus.Up);
        lspBuilder.setPlspId(new PlspId(plspId));
        lspBuilder.setProcessingRule(false);
        lspBuilder.setRemove(false);
        lspBuilder.setSync(sync);
        lspBuilder.setTlvs(tlvs.orNull());
        return lspBuilder.build();
    }

    public static Path createPath(final List<Subobject> subobjects) {
        final PathBuilder pathBuilder = new PathBuilder();
        pathBuilder.setEro(new EroBuilder().setSubobject(subobjects).build());
        return pathBuilder.build();
    }

    public static Srp createSrp(final long srpId) {
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setProcessingRule(false);
        srpBuilder.setIgnore(false);
        srpBuilder.setOperationId(new SrpIdNumber(srpId));
        return srpBuilder.build();
    }

    public static Path updToRptPath(
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcupd.message.pcupd.message.updates.Path path) {
        final PathBuilder pathBuilder = new PathBuilder();
        pathBuilder.setBandwidth(path.getBandwidth());
        pathBuilder.setClassType(path.getClassType());
        pathBuilder.setEro(path.getEro());
        pathBuilder.setIro(path.getIro());
        pathBuilder.setLspa(path.getLspa());
        pathBuilder.setMetrics(path.getMetrics());
        pathBuilder.setOf(path.getOf());
        pathBuilder.setRro(path.getRro());
        pathBuilder.setXro(path.getXro());
        return pathBuilder.build();
    }

    public static Tlvs createLspTlvs(final long lspId, final boolean symbolicPathName, InetAddress tunnelEndpoint,
            InetAddress tunnelSender, InetAddress extendedTunnelAddress) {
        final TlvsBuilder tlvs = new TlvsBuilder().setLspIdentifiers(new LspIdentifiersBuilder()
                .setLspId(new LspId(lspId))
                .setAddressFamily(
                        new Ipv4CaseBuilder().setIpv4(
                                new Ipv4Builder()
                                        .setIpv4TunnelEndpointAddress(new Ipv4Address(tunnelEndpoint.getHostAddress()))
                                        .setIpv4TunnelSenderAddress(new Ipv4Address(tunnelSender.getHostAddress()))
                                        .setIpv4ExtendedTunnelId(
                                                new Ipv4ExtendedTunnelId(extendedTunnelAddress.getHostAddress()))
                                        .build()).build()).setTunnelId(new TunnelId((int) lspId)).build());
        if (symbolicPathName) {
            final String pathName = "pcc_" + tunnelSender.getHostAddress() + "_tunnel_" + lspId;
            tlvs.setSymbolicPathName(new SymbolicPathNameBuilder().setPathName(
                    new SymbolicPathName(pathName.getBytes(Charsets.UTF_8))).build());
        }
        return tlvs.build();
    }

    public static Message createErrorMsg(final PCEPErrors e, final long srpId) {
        final PcerrMessageBuilder msgBuilder = new PcerrMessageBuilder();
        return new PcerrBuilder().setPcerrMessage(
                msgBuilder
                        .setErrorType(
                                new StatefulCaseBuilder().setStateful(
                                        new StatefulBuilder().setSrps(
                                                Lists.newArrayList(new SrpsBuilder().setSrp(
                                                        new SrpBuilder().setProcessingRule(false).setIgnore(false)
                                                                .setOperationId(new SrpIdNumber(srpId)).build())
                                                        .build())).build()).build())
                        .setErrors(
                                Arrays.asList(new ErrorsBuilder().setErrorObject(
                                        new ErrorObjectBuilder().setType(e.getErrorType()).setValue(e.getErrorValue())
                                                .build()).build())).build()).build();
    }

}
