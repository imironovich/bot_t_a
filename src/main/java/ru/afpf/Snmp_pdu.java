package ru.afpf;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Snmp_pdu implements AnsverInterface{
     private Properties properties;
   Snmp_pdu(Properties properties) {
        this.properties =  properties;

        try {
            properties.load(new FileInputStream(new File("Bot.properties")));
            IP_ADDRESS = (properties.getProperty("SNMP_ADDRESS"));
            PORT = (properties.getProperty("SNMP_PORT"));
            COMMUNITY = (properties.getProperty("SMNP_COMMUNITY"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String IP_ADDRESS;

    private String PORT;

    private int SNMP_VERSION = SnmpConstants.version1;

    private String COMMUNITY;

    public String getAnsver (String question){

        switch (question){
            case "pogreb":
                return ("Температура "+getSnmp(properties.getProperty("SNMP_PG_T"))+" оС,"+" Влажность "+getSnmp(properties.getProperty("SNMP_PG_H"))+" %");
            case "prihojaya":
                return ("Температура "+getSnmp(properties.getProperty("SNMP_PR_T"))+" оС,"+" Влажность "+getSnmp(properties.getProperty("SNMP_PR_H"))+" %");
            case "pogrebT":
                return (getSnmp(properties.getProperty("SNMP_PG_T")));
        }
        return "Не реализовано";
    }

    private String getSnmp(String oid) {
        try {

            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
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
            System.err.println("Error in snmp_pdu");
        }
        return "-273";
    }
}
