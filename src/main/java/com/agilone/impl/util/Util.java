package com.agilone.impl.util;

import com.agilone.impl.configapp.ConfigApp;
import org.json.JSONObject;

public class Util {
    public JSONObject retrieveAllEntities( String token, String tenantID) {
        JSONObject udmEntities;

        try {
//            Path path = Paths.get("/Users/sreekanth/IdeaProjects/ImplTools/src/main/resources/beforecreate.json");
//            String jsonString = Files.lines(path).collect(Collectors.joining());
            ConfigApp app = new ConfigApp();
            String jsonString = app.getAllUdmPTables(  ConfigApp.CS_US_URL, token, tenantID);
            udmEntities = new JSONObject( jsonString);
        }
        catch( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return udmEntities;
    }

    public boolean putUdmPTable(String token, String tenantId, String tableID, String body) {
        ConfigApp app = new ConfigApp();
        try {
            app.putUdmPTable(token, tenantId, tableID, body);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return true;
    }

    public JSONObject getConnector( String token, int connectorID, String tenantId) {
        ConfigApp app = new ConfigApp();
        String str;
        try {
             str = app.getConnector( token, connectorID, tenantId);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return new JSONObject( str);
    }

    public boolean putConnector(String token, String tenantId, String body, int connectorId) {
        ConfigApp app = new ConfigApp();
        try {
            app.putConnector(token, tenantId, connectorId, body);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return true;
    }

    public String getToken( String encoding) {
        ConfigApp app = new ConfigApp();
        String token;
        try {
            token = app.getToken( encoding);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return token;
    }
}
