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

public class Snmp_pdu implements AnsverInterface{
    static private Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream(new File("Bot.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String IP_ADDRESS = (properties.getProperty("SNMP_ADDRESS"));

    private static String PORT = (properties.getProperty("SNMP_PORT"));;

    private static int SNMP_VERSION = SnmpConstants.version1;

    private static String COMMUNITY = (properties.getProperty("SMNP_COMMUNITY"));

    public String getAnsver (String question){

        switch (question){
            case "pogreb":
                return ("Температура "+getSnmp(properties.getProperty("SNMP_PG_T"))+" оС,"+" Влажность "+getSnmp(properties.getProperty("SNMP_PG_H"))+" %");
            case "prihojaya":
                return ("Температура "+getSnmp(properties.getProperty("SNMP_PR_T"))+" оС,"+" Влажность "+getSnmp(properties.getProperty("SNMP_PR_H"))+" %");
        }
        return "Не реализовано";
    }

    public String getSnmp(String oid) {
        try {

            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target Address object
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(COMMUNITY));
            comtarget.setVersion(SNMP_VERSION);
            comtarget.setAddress(new UdpAddress(IP_ADDRESS + "/" + PORT));
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
                        return (str.substring(str.indexOf("=") + 2, str.indexOf("]")));
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
        return "-273";
    }
}
