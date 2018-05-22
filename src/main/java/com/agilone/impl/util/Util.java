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

    public String putUdmPTable(String token, String tenantId, String tableID, String body) {
        ConfigApp app = new ConfigApp();
        try {
            app.putUdmPTable(token, tenantId, tableID, body);
        }
        catch ( Exception ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
        return "";
    }
}
