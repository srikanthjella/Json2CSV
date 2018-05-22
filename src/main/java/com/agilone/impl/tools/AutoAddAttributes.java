package com.agilone.impl.tools;

import com.agilone.impl.util.Constants;
import com.agilone.impl.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AutoAddAttributes {
    private static JSONObject udmEntities;
    private static final Logger log = LoggerFactory.getLogger( AutoAddAttributes.class);

    public static void main(String[] args) throws Exception {
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
        else if( !optsMap.keySet().contains( Constants.ENV) || !optsMap.keySet().contains( Constants.LOCATION)
                || !optsMap.keySet().contains( Constants.OPERATION) || !optsMap.keySet().contains( Constants.TENANT_ID)
                || !optsMap.keySet().contains( Constants.TOKEN) || !optsMap.keySet().contains( Constants.INPUTFILE)) {
            exitWithErrorMsg("Following required parameter(s) not found:"
                    + (optsMap.keySet().contains( Constants.ENV) ? "" : " " + Constants.ENV)
                    + (optsMap.keySet().contains( Constants.LOCATION) ? "" : " " + Constants.LOCATION)
                    + (optsMap.keySet().contains( Constants.OPERATION) ? "" : " "+ Constants.OPERATION)
                    + (optsMap.keySet().contains( Constants.TENANT_ID) ? "" : " " + Constants.TENANT_ID)
                    + (optsMap.keySet().contains( Constants.INPUTFILE) ? "" : " " + Constants.INPUTFILE)
                    + (optsMap.keySet().contains( Constants.TOKEN) ? "" : " " + Constants.TOKEN), true);
        }

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

//        System.out.println( udmEntities.toString());
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

    private static void buildAddAttributesJSON(Map<String, List<String>> inputEntitiesMap, Map<String, String> optsMap) {
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

    private static Map<String,Object> addNewAttribute(String attributeName, String displayName, String attributeType, String availability, String entityName) {
        Map<String,Object> attr = new HashMap<>();

        if( !Constants.VALID_DATA_TYPES.contains( attributeType.toUpperCase())) {
            exitWithErrorMsg( attributeType.toUpperCase() + " is not a valid data type. AttributeName: " + attributeName + ", EntityName: " + entityName, false);
        }

        List<String> list = Arrays.asList( availability.split("\\|"));

        if( !Constants.VALID_DATA_TYPES.contains( attributeType.toUpperCase())) {
            exitWithErrorMsg( attributeType.toUpperCase() + " is not a valid data type.", false);
        }

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
                "-operation <AddAttributes/Compare> " +
                "-env <CS/PROD> " +
                "-location <US/EU> " +
                "-tenantId <ID> " +
                "-token <tokenId> " +
                "-fileName </path/to/file>";
    }

    private static void exitWithErrorMsg( String msg, boolean usage) {
        log.error( msg);

        if( usage)
            log.error( usage());
        log.error( "Exiting..");
        System.exit(1);
    }
}
