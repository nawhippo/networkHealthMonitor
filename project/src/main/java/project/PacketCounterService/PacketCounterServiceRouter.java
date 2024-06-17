package project.PacketCounterService;



import java.io.IOException;

public class PacketCounterServiceRouter {

    PacketCounterServiceRouter() {
        // Replace with your router's IP address and SNMP community string
        String routerIpAddress = "";
        String communityString = "public";

        // SNMP OIDs for interface statistics
        OID ifInOctetsOID = new OID(".1.3.6.1.2.1.2.2.1.10.1");  // Example: Incoming octets
        OID ifOutOctetsOID = new OID(".1.3.6.1.2.1.2.2.1.16.1"); // Example: Outgoing octets

        // Create Transport Mapping
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create SNMP object
        Snmp snmp = new Snmp(transport);

        // Create target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(communityString));
        target.setAddress(new UdpAddress(routerIpAddress + "/161")); // SNMP port 161
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        // Prepare and send PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(ifInOctetsOID));
        pdu.add(new VariableBinding(ifOutOctetsOID));
        pdu.setType(PDU.GET);

        // Send the PDU
        ResponseEvent response = snmp.send(pdu, target);

        // Process the response
        if (response != null && response.getResponse() != null) {
            PDU responsePDU = response.getResponse();
            System.out.println("Packets In: " + responsePDU.getVariable(ifInOctetsOID));
            System.out.println("Packets Out: " + responsePDU.getVariable(ifOutOctetsOID));
        } else {
            System.out.println("No response received.");
        }

        // Clean up
        snmp.close();
    }
}