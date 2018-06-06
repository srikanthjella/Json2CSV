package com.agilone.impl.tools;

import com.agilone.impl.util.Constants;
import com.agilone.impl.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AutoAddAttributes {
    private static JSONObject udmEntities;
    private static final Logger log = LoggerFactory.getLogger( AutoAddAttributes.class);

    public static void main( String[] args) throws Exception {
        Map<String,String> optsMap = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                    if (args[i].length() < 2) {
                        exitWithErrorMsg("Illegal argument: " + args[i], true);
                    }
                    if (args.length-1 == i) {
                        exitWithErrorMsg(  "No Matching argument found for: " + args[i], true);
                    }
                    // -opt
                    optsMap.put( args[i].toLowerCase().substring(1), args[i+1]);
                    i++;
                    break;
                default:
                    break;
            }
        }

        if( optsMap.size() == 0) {
            exitWithErrorMsg("None of the required parameters not found (env/location/tenantId/operation)", true);
        }
        else if (optsMap.get( Constants.OPERATION).equalsIgnoreCase( Constants.ADDATTRIBUTES) && (!optsMap.keySet().contains(Constants.ENV)
                || !optsMap.keySet().contains(Constants.LOCATION) || !optsMap.keySet().contains(Constants.TENANT_ID)
                || !optsMap.keySet().contains(Constants.USERNAME) || !optsMap.keySet().contains(Constants.INPUTFILE))) {
            exitWithErrorMsg("Following required parameter(s) not found:"
                    + (optsMap.keySet().contains(Constants.ENV) ? "" : " " + Constants.ENV)
                    + (optsMap.keySet().contains(Constants.LOCATION) ? "" : " " + Constants.LOCATION)
                    + (optsMap.keySet().contains(Constants.OPERATION) ? "" : " " + Constants.OPERATION)
                    + (optsMap.keySet().contains(Constants.TENANT_ID) ? "" : " " + Constants.TENANT_ID)
                    + (optsMap.keySet().contains(Constants.INPUTFILE) ? "" : " " + Constants.INPUTFILE)
                    + (optsMap.keySet().contains(Constants.USERNAME) ? "" : " " + Constants.USERNAME), true);
        }
        else if( optsMap.get( Constants.OPERATION).equalsIgnoreCase( Constants.MAPPING) && (!optsMap.keySet().contains(Constants.ENV)
                || !optsMap.keySet().contains(Constants.LOCATION) || !optsMap.keySet().contains(Constants.TENANT_ID)
                || !optsMap.keySet().contains(Constants.USERNAME) || !optsMap.keySet().contains(Constants.INPUTFILE)
                || !optsMap.keySet().contains(Constants.CONNECTORID))) {
            exitWithErrorMsg("Following required parameter(s) not found:"
                    + (optsMap.keySet().contains(Constants.ENV) ? "" : " " + Constants.ENV)
                    + (optsMap.keySet().contains(Constants.LOCATION) ? "" : " " + Constants.LOCATION)
                    + (optsMap.keySet().contains(Constants.OPERATION) ? "" : " " + Constants.OPERATION)
                    + (optsMap.keySet().contains(Constants.TENANT_ID) ? "" : " " + Constants.TENANT_ID)
                    + (optsMap.keySet().contains(Constants.INPUTFILE) ? "" : " " + Constants.INPUTFILE)
                    + (optsMap.keySet().contains(Constants.CONNECTORID) ? "" : " " + Constants.CONNECTORID)
                    + (optsMap.keySet().contains(Constants.USERNAME) ? "" : " " + Constants.USERNAME), true);
        }

        String encoding;
        if( optsMap.get( Constants.PASSWORD) == null) {
            String userName = optsMap.get( Constants.USERNAME);
            char[] password = System.console().readPassword( "Enter Password");

            encoding = Base64.getEncoder().encodeToString ( (userName + ":" + new String(password)).getBytes());
            System.out.println( password);
            Arrays.fill(password, ' ');
        }
        else
            encoding = Base64.getEncoder().encodeToString ( (
                    optsMap.get( Constants.USERNAME) + ":" + optsMap.get( Constants.PASSWORD)).getBytes());

        Util util = new Util();
        String token = util.getToken( encoding);
        optsMap.put( Constants.TOKEN, token);
        optsMap.remove( Constants.USERNAME);
        optsMap.remove( Constants.PASSWORD);
        optsMap.remove( Constants.PASSWORD);

        AutoAddAttributes att = new AutoAddAttributes();
        if( optsMap.get( Constants.OPERATION).equalsIgnoreCase( Constants.ADDATTRIBUTES)) {
            att.addAttributesOperation( optsMap);
        }
        else if( optsMap.get( Constants.OPERATION).equalsIgnoreCase( Constants.MAPPING)) {
            att.addMapping( optsMap);
        }

//        System.out.println( udmEntities.toString());
    }

    private void addMapping(Map<String,String> optsMap) throws IOException {
        String inputFile = optsMap.get(Constants.INPUTFILE);
        Util util = new Util();
        List<String> csvList;

        Path path = Paths.get( inputFile);
        csvList = Files.lines( path).collect( Collectors.toList());

        String[] attrs;
        Map<String,List<String>> mappingMap = new HashMap<>();

        for( String line : csvList) {
            attrs = line.split(",");
            if( !mappingMap.containsKey(attrs[0])) {
                mappingMap.put( attrs[0] + "," + attrs[1], new ArrayList<>());
            }
            mappingMap.get( attrs[0] + "," + attrs[1]).add( attrs[2] + "," + attrs[3]);
        }

        buildAddMappingJSON( mappingMap, optsMap);
    }

    private void buildAddMappingJSON( Map<String, List<String>> mappingMap, Map<String, String> optsMap) {
        Util util = new Util();
        udmEntities = util.getConnector( optsMap.get( Constants.TOKEN)
                , Integer.parseInt( optsMap.get( Constants.CONNECTORID))
                , optsMap.get(Constants.TENANT_ID));

        List<Object> mappingListArray;
        List<Object> mappingListOuterArray = new ArrayList<>();
        JSONObject feedInput;
        List<JSONObject> mappedEntitiesArray;
        String [] array;

        for ( String str : mappingMap.keySet()) {
            mappingListArray = new ArrayList<>();
            array = str.split(",");
            feedInput = new JSONObject();
            feedInput.put( "table", array[0]);
            feedInput.put( "column", array[1]);

            mappingListArray.add( feedInput);

            mappedEntitiesArray = new ArrayList<>();

            for( String str2 : mappingMap.get(str)) {
                array = str2.split(",");
                feedInput = new JSONObject();
                feedInput.put( "table", array[0]);
                feedInput.put( "column", array[1]);

                mappedEntitiesArray.add( feedInput);
            }
            mappingListArray.add( mappedEntitiesArray);
            mappingListOuterArray.add( mappingListArray);
        }

        udmEntities.put( "mapping", mappingListOuterArray);

        int connectorDefId = udmEntities.getJSONObject( Constants.CONNECTORDEF).getInt("id");
        JSONObject connectorDef = new JSONObject();
        connectorDef.put("id", connectorDefId);
        udmEntities.put( Constants.CONNECTORDEF, connectorDef);
        int connectorIncrement = udmEntities.getInt( Constants.CONNECTORINCREMENT);
//        udmEntities.put( Constants.CONNECTORINCREMENT, connectorIncrement);
        udmEntities.remove( Constants.CONNECTORINCREMENT);
        String jsonString = udmEntities.toString();
        jsonString = "{\"connectorIncrement\": " + connectorIncrement  + "," + jsonString.substring(1);

        util.putConnector( optsMap.get( Constants.TOKEN), optsMap.get( Constants.TENANT_ID)
                , jsonString, Integer.parseInt( optsMap.get( Constants.CONNECTORID)));
//        System.out.println( udmEntities.toString());
    }

    private void addAttributesOperation(Map<String, String> optsMap) throws IOException {
        String inputFile = optsMap.get(Constants.INPUTFILE);
        List<String> csvList;

        Util util = new Util();
        udmEntities = util.retrieveAllEntities( optsMap.get( Constants.TOKEN)
                , optsMap.get( Constants.TENANT_ID));

        Path path = Paths.get( inputFile);
        csvList = Files.lines( path).collect( Collectors.toList());

        String[] attrs;
        Map<String,List<String>> entitiesMap = new HashMap<>();

        for( String line : csvList) {
            attrs = line.split(",");
            if( !entitiesMap.containsKey(attrs[0])) {
                entitiesMap.put( attrs[0], new ArrayList<>());
            }
            entitiesMap.get(attrs[0]).add( line);
        }
//            removeOtherEntities( entitiesMap);
        buildAddAttributesJSON( entitiesMap, optsMap);
    }
/*
    private static void removeOtherEntities( Map<String,List<String>> entitiesMap) {
        JSONArray entities = udmEntities.getJSONArray("content");
        JSONArray entitiesToModify = new JSONArray();

        Set<String> entitiesToBeModified = entitiesMap.keySet();
        List<Integer> toBeRemoved = new ArrayList<>();

        for( int i=0 ; i < entities.length() ; ++i)
            if( entitiesToBeModified.contains( entities.getJSONObject(i).get("name").toString()))
                entitiesToModify.put( entities.getJSONObject(i));
//                toBeRemoved.add(i);

        udmEntities.remove("content");
        udmEntities.put("content", entitiesToModify);
    }
*/

    private void buildAddAttributesJSON(Map<String, List<String>> inputEntitiesMap, Map<String, String> optsMap) {
        JSONArray udmColumnsArr =  udmEntities.getJSONArray("content");
        String entityName;

        for ( int i=0 ; i < udmColumnsArr.length() ; ++i) {
            JSONObject jsonObject = udmColumnsArr.getJSONObject(i);
            entityName = jsonObject.getString( Constants.NAME);

            if( inputEntitiesMap.containsKey( entityName)) {
                List<Object> list = jsonObject.getJSONObject("columns").getJSONArray("content").toList();
                for( String attrDef : inputEntitiesMap.get( entityName)) {
                    String attrDefArr[] = attrDef.split(",");
                    for( Object obj : list) {
                        HashMap<String,String> map = (HashMap<String, String>) obj;
                        if( map.get( Constants.NAME).equalsIgnoreCase( attrDefArr[1])) {
                            exitWithErrorMsg( attrDefArr[1] + " is already exist in the table " + entityName, false);
                        }
                    }

                    list.add( addNewAttribute( attrDefArr[1], attrDefArr[2]
                                        , attrDefArr[3].toUpperCase(), attrDefArr[4], entityName));
                }

                jsonObject.put( "columns", list);
                jsonObject.put( "unPublished", true);
                jsonObject.put( "modified", true);

                Util util = new Util();
                log.info( "Adding attributes to entity " + entityName);
                util.putUdmPTable( optsMap.get( Constants.TOKEN), optsMap.get( Constants.TENANT_ID)
                            , jsonObject.get(Constants.TABLE_ID).toString(), jsonObject.toString());
            }
        }
    }

    private Map<String,Object> addNewAttribute(String attributeName, String displayName, String attributeType
            , String availability, String entityName) {
        Map<String,Object> attr = new HashMap<>();

        if( !attributeName.startsWith("c_"))
            exitWithErrorMsg( attributeName + " attribute should start with c_ ", false);

        if( !attributeName.matches("([a-z]+([A-Z][a-z])*)+"))
            exitWithErrorMsg( attributeName + " should be in camelCase ", false);

        if( !Constants.VALID_DATA_TYPES.contains( attributeType.toUpperCase()))
            exitWithErrorMsg( attributeType.toUpperCase() + " is not a valid data type. AttributeName: "
                    + attributeName + ", EntityName: " + entityName, false);

        if( !Constants.VALID_DATA_TYPES.contains( attributeType.toUpperCase()))
            exitWithErrorMsg( attributeType.toUpperCase() + " is not a valid data type.", false);

        List<String> list = Arrays.asList( availability.split("\\|"));

        for( String str : list) {
            if( !Constants.VALID_AVAILABILITY.contains( str) )
                exitWithErrorMsg( attributeType.toUpperCase() + " is not a valid availability.", false);
        }

        attr.put( Constants.NAME,attributeName);
        attr.put( Constants.UNPUBLISHED,Boolean.TRUE);
        attr.put( Constants.READONLY,Boolean.FALSE);
        attr.put( Constants.DISPLAYNAME, displayName);
        attr.put( Constants.ATTRIBUTETYPE, attributeType);
        attr.put( Constants.AVAILABILITY, list);
        attr.put( Constants.LOOKUP, null);
        attr.put( Constants.NEW, true);

//        System.out.println(attr.toString());

        return attr;
    }

    private static String usage() {
        return "Invalid option: Usage is \n java -jar ImplTools-X.X.jar " +
                "-operation <AddAttributes/Mapping/Compare> " +
                "-env <CS/PROD> " +
                "-location <US/EU> " +
                "-tenantId <ID> " +
                "-userName <UserName> " +
                "[-password <Password>] " +
                "[-ConnectorId <id>] " +
                "-fileName </path/to/file>";
    }

    private static void exitWithErrorMsg( String msg, boolean usage) {
        log.error( "\n\n" + msg);

        if( usage)
            log.error( usage());
        log.error( "Exiting..");
        System.exit(1);
    }
}
