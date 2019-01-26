package ru.afpf;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Snmp_pdu {
    static private Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream(new File("Bot.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String ipAddress = (properties.getProperty("SNMP_ADDRESS"));

    private static String port = (properties.getProperty("SNMP_PORT"));;

    // OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
    //private static String oidValue = ".1.3.6.1.4.1.318.1.1.10.3.13.1.1.3.2";  // ends with 0 for scalar object

    private static int snmpVersion = SnmpConstants.version1;

    private static String community = (properties.getProperty("SMNP_COMMUNITY"));


    public static int getSnmp(String oid) {
        try {

            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();


            // Create Target Address object
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(1000);

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);
            pdu.setRequestID(new Integer32(1));

            // Create Snmp object for sending data to Agent
            Snmp snmp = new Snmp(transport);

            //   System.out.println("Sending Request to Agent...");
            ResponseEvent response = snmp.get(pdu, comtarget);


            // Process Agent Response
            if (response != null) {
                PDU responsePDU = response.getResponse();

                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    int errorIndex = responsePDU.getErrorIndex();
                    String errorStatusText = responsePDU.getErrorStatusText();

                    if (errorStatus == PDU.noError) {
                        System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
                        String str = responsePDU.getVariableBindings().toString();
                        return Integer.parseInt(str.substring(str.indexOf("=") + 2, str.indexOf("]")));
                    } else {
                        System.out.println("Error: Request Failed");
                        System.out.println("Error Status = " + errorStatus);
                        System.out.println("Error Index = " + errorIndex);
                        System.out.println("Error Status Text = " + errorStatusText);
                    }
                } else {
                    System.out.println("Error: Response PDU is null");
                }
            } else {
                System.out.println("Error: Agent Timeout... ");
            }
            snmp.close();
        } catch (IOException E) {
        }
        return -273;
    }
}
