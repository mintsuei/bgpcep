/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.bgpcep.pcep.tunnel.provider;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.binding.test.AbstractDataBrokerTest;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.network.concepts.rev131125.Bandwidth;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Path1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Path1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.LspIdentifiersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.LspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.bandwidth.object.BandwidthBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.Ipv4ExtendedTunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.LspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.Node1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.Node1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.PccSyncState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.PathComputationClientBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.ReportedLsp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.ReportedLspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.ReportedLspKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.PathBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.PathKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.node.TerminationPoint;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.node.attributes.SupportingNode;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class NodeChangedListenerTest extends AbstractDataBrokerTest {

    private static final TopologyId PCEP_TOPOLOGY_ID = new TopologyId("pcep-topology");
    private static final TopologyId TUNNEL_TOPOLOGY_ID = new TopologyId("tunnel-topology");

    private static final String NODE1_IPV4 = "39.39.39.39";
    private static final NodeId NODE1_ID = new NodeId("pcc://" + NODE1_IPV4);
    private static final String LSP1_NAME = "lsp1";
    private static final long LSP1_ID = 1;

    private static final String NODE2_IPV4 = "40.40.40.40";
    private static final NodeId NODE2_ID = new NodeId("pcc://" + NODE2_IPV4);
    private static final String LSP2_NAME = "lsp2";
    private static final long LSP2_ID = 2;

    private static final InstanceIdentifier<Topology> PCEP_TOPO_IID = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(PCEP_TOPOLOGY_ID)).toInstance();
    private static final InstanceIdentifier<Topology> TUNNEL_TOPO_IID = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(TUNNEL_TOPOLOGY_ID)).toInstance();

    private ListenerRegistration<DataChangeListener> listenerRegistration;

    @Before
    public void setUp() throws TransactionCommitFailedException {
        final WriteTransaction wTx = getDataBroker().newWriteOnlyTransaction();
        wTx.put(LogicalDatastoreType.OPERATIONAL, PCEP_TOPO_IID, new TopologyBuilder().setKey(new TopologyKey(PCEP_TOPOLOGY_ID)).setNode(Lists.<Node>newArrayList()).setTopologyId(PCEP_TOPOLOGY_ID).build(), true);
        wTx.put(LogicalDatastoreType.OPERATIONAL, TUNNEL_TOPO_IID, new TopologyBuilder().setKey(new TopologyKey(TUNNEL_TOPOLOGY_ID)).setTopologyId(TUNNEL_TOPOLOGY_ID).build(), true);
        wTx.submit().checkedGet();
        final NodeChangedListener nodeListener = new NodeChangedListener(getDataBroker(), TUNNEL_TOPO_IID);
        this.listenerRegistration = getDataBroker().registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, PCEP_TOPO_IID.child(Node.class), nodeListener, DataChangeScope.SUBTREE);
    }

    @Test
    public void testNodeChangedListener() throws ReadFailedException, TransactionCommitFailedException {
        // add node -> create two nodes with TPs and link
        createNode(NODE1_ID, NODE1_IPV4, LSP1_NAME, LSP1_ID, NODE2_IPV4);
        final Topology tunnelTopo = readTunnelTopology().get();
        Assert.assertEquals(2, tunnelTopo.getNode().size());
        final Node dst = tunnelTopo.getNode().get(0);
        final Node src = tunnelTopo.getNode().get(1);
        final NodeId srcId = new NodeId("ip://" + new IpAddress(new Ipv4Address(NODE1_IPV4)));
        final NodeId dstId = new NodeId("ip://" + new IpAddress(new Ipv4Address(NODE2_IPV4)));
        Assert.assertEquals(srcId, src.getNodeId());
        Assert.assertEquals(dstId, dst.getNodeId());

        Assert.assertEquals(1, dst.getTerminationPoint().size());
        Assert.assertEquals(1, src.getTerminationPoint().size());
        final TerminationPoint dstTp = dst.getTerminationPoint().get(0);
        final TerminationPoint srcTp = src.getTerminationPoint().get(0);
        final TpId dstNodeTpId = new TpId(dstId.getValue());
        final TpId srcNodeTpId = new TpId(srcId.getValue());
        Assert.assertEquals(dstNodeTpId, dstTp.getTpId());
        Assert.assertEquals(srcNodeTpId, srcTp.getTpId());

        Assert.assertEquals(1, src.getSupportingNode().size());
        Assert.assertNull(dst.getSupportingNode());
        final SupportingNode sNode = src.getSupportingNode().get(0);
        Assert.assertEquals(NODE1_ID, sNode.getKey().getNodeRef());

        Assert.assertEquals(1, tunnelTopo.getLink().size());
        final Link link = tunnelTopo.getLink().get(0);
        Assert.assertEquals(srcId, link.getSource().getSourceNode());
        Assert.assertEquals(srcNodeTpId, link.getSource().getSourceTp());
        Assert.assertEquals(dstId, link.getDestination().getDestNode());
        Assert.assertEquals(dstNodeTpId, link.getDestination().getDestTp());

        // update second node -> adds supporting node and second link
        createNode(NODE2_ID, NODE2_IPV4, LSP2_NAME, LSP2_ID, NODE1_IPV4);
        final Topology updatedNodeTopo = readTunnelTopology().get();
        final Node updatedNode = updatedNodeTopo.getNode().get(0);
        Assert.assertEquals(1, updatedNode.getSupportingNode().size());
        final SupportingNode sNode2 = updatedNode.getSupportingNode().get(0);
        Assert.assertEquals(NODE2_ID, sNode2.getNodeRef());

        Assert.assertEquals(2, updatedNodeTopo.getLink().size());
        final Link link2 = updatedNodeTopo.getLink().get(0);
        Assert.assertEquals(dstId, link2.getSource().getSourceNode());
        Assert.assertEquals(dstNodeTpId, link2.getSource().getSourceTp());
        Assert.assertEquals(srcId, link2.getDestination().getDestNode());
        Assert.assertEquals(srcNodeTpId, link2.getDestination().getDestTp());

        // remove nodes -> remove link
        removeNode(NODE1_ID);
        removeNode(NODE2_ID);
        final Topology removedNodeTopo = readTunnelTopology().get();
        Assert.assertEquals(0, removedNodeTopo.getNode().size());
        Assert.assertEquals(0, removedNodeTopo.getLink().size());
    }

    @After
    public void tearDown() {
        this.listenerRegistration.close();
    }

    private void createNode(final NodeId nodeId, final String ipv4Address, final String lspName, final long lspId, final String dstIpv4Address) throws TransactionCommitFailedException {
        final NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setKey(new NodeKey(nodeId));
        nodeBuilder.setNodeId(nodeId);
        final PathBuilder pathBuilder = new PathBuilder();
        pathBuilder.setKey(new PathKey(new LspId(lspId)));
        pathBuilder.setBandwidth(new BandwidthBuilder().setBandwidth(new Bandwidth(new byte[] {0x00, 0x00, (byte) 0xff, (byte) 0xff})).build());
        pathBuilder.addAugmentation(Path1.class, new Path1Builder().setLsp(new LspBuilder().setTlvs(new TlvsBuilder().setLspIdentifiers(new LspIdentifiersBuilder().setAddressFamily(new Ipv4CaseBuilder().setIpv4(new Ipv4Builder().setIpv4TunnelSenderAddress(new Ipv4Address(ipv4Address)).setIpv4ExtendedTunnelId(new Ipv4ExtendedTunnelId(ipv4Address)).setIpv4TunnelEndpointAddress(new Ipv4Address(dstIpv4Address)).build()).build()).build()).build()).setAdministrative(true).setDelegate(true).build()).build());
        final ReportedLsp reportedLps = new ReportedLspBuilder().setKey(new ReportedLspKey(lspName)).setPath(Lists.newArrayList(pathBuilder.build())).build();
        final Node1Builder node1Builder = new Node1Builder();
        node1Builder.setPathComputationClient(new PathComputationClientBuilder().setStateSync(PccSyncState.Synchronized).setReportedLsp(Lists.newArrayList(reportedLps)).setIpAddress(new IpAddress(new Ipv4Address(ipv4Address))).build());
        nodeBuilder.addAugmentation(Node1.class, node1Builder.build());
        final WriteTransaction wTx = getDataBroker().newWriteOnlyTransaction();
        wTx.put(LogicalDatastoreType.OPERATIONAL, PCEP_TOPO_IID.builder().child(Node.class, new NodeKey(nodeId)).toInstance(), nodeBuilder.build());
        wTx.submit().checkedGet();
    }

    private void removeNode(final NodeId nodeId) throws TransactionCommitFailedException {
        final WriteTransaction wTx = getDataBroker().newWriteOnlyTransaction();
        wTx.delete(LogicalDatastoreType.OPERATIONAL, PCEP_TOPO_IID.builder().child(Node.class, new NodeKey(nodeId)).toInstance());
        wTx.submit().checkedGet();
    }

    private Optional<Topology> readTunnelTopology() throws ReadFailedException {
        final ReadTransaction rTx = getDataBroker().newReadOnlyTransaction();
        return rTx.read(LogicalDatastoreType.OPERATIONAL, TUNNEL_TOPO_IID).checkedGet();
    }
}
