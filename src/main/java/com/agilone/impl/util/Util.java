package com.agilone.impl.util;

import com.agilone.impl.configapp.ConfigApp;
import org.json.JSONObject;

import java.util.Map;

public class Util {
    public JSONObject retrieveAllEntities( String token, Map<String, String> optsMap) {
        JSONObject udmEntities;

        try {
//            Path path = Paths.get("/Users/sreekanth/IdeaProjects/ImplTools/src/main/resources/beforecreate.json");
//            String jsonString = Files.lines(path).collect(Collectors.joining());
            ConfigApp app = new ConfigApp();
            String jsonString = app.getAllUdmPTables(  ConfigApp.CS_US_URL, token, optsMap.get( Constants.TENANT_ID));
            udmEntities = new JSONObject( jsonString);
        }
        catch( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return udmEntities;
    }

    public boolean putUdmPTable(Map<String, String> optsMap, String tableID, String body) {
        ConfigApp app = new ConfigApp();
        try {
            app.putUdmPTable( optsMap.get(Constants.TOKEN),
                    optsMap.get( Constants.TENANT_ID), tableID, body);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return true;
    }

    public JSONObject getConnector( Map<String, String> optsMap) {
        ConfigApp app = new ConfigApp();
        String str;
        try {
             str = app.getConnector( optsMap.get(Constants.TOKEN),
                     Integer.parseInt( optsMap.get(Constants.CONNECTORID)),
                     optsMap.get( Constants.TENANT_ID));
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return new JSONObject( str);
    }

    public boolean putConnector( String body, Map<String, String> optsMap) {
        ConfigApp app = new ConfigApp();
        try {
            app.putConnector( optsMap.get(Constants.TOKEN),
                    optsMap.get( Constants.TENANT_ID),
                    Integer.parseInt( optsMap.get(Constants.CONNECTORID)), body);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return true;
    }

    public String getToken( String encoding, Map<String, String> optsMap) {
        ConfigApp app = new ConfigApp();
        String token;
        try {
            token = app.getToken( "", encoding);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return token;
    }
}
