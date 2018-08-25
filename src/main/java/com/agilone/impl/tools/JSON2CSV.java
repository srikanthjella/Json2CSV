package com.agilone.impl.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON2CSV {
    public static void main(String args[]) {
        if( args.length > 0 && args[0].toLowerCase().equals("mapping")) {
            if( args.length != 3) {
                System.out.println( "Either input file or output file or both missing. Usage is: " +
                        "java -jar JSON2CSV-1.0-SNAPSHOT.jar mapping <InputFileName> <OutputFileName>");
            }
            else {
                mapping( args[1], args[2]);
            }
        }
        else if( args.length > 0 && args[0].toLowerCase().equals("udm")) {
            if( args.length != 3) {
                System.out.println( "Either input file or output file or both missing. Usage is: " +
                        "java -jar JSON2CSV-1.0-SNAPSHOT.jar udm <InputFileName> <OutputFileName>");
            }
            else {
                udm( args[1], args[2]);
            }
        }
        else {
            List<String> csList = new ArrayList<>();
            List<String> prodList = new ArrayList<>();

            csList.add("account,Batch,STRING,Batch");
            csList.add("account,DateCreated,LONG,DateCreated");
            csList.add("account,DateModified,LONG,DateModified");
            csList.add("account,DeleteFlag,BOOLEAN,Delete Flag");
            csList.add("account,ID,STRING,ID");
            csList.add("account,Name,STRING,Name");

            prodList.add("account,Batch,STRING,Batch");
            prodList.add("account,DateCreated,LONG,DateCreated");
            prodList.add("account,DateModified,LONG,DateModified");
            prodList.add("account,DeleteFlag,BOOLEAN,DeleteFlag");
            prodList.add("account,ID,STRING,ID");

            compare( csList, prodList, "udm");
        }
    }

    private static void udm( String inputFile, String outputFile) {
        if( ! new File(inputFile).isFile()) {
            System.out.println( "Input file does not exist. " + inputFile);
            return;
        }

        if( new File(outputFile).isFile()) {
            System.out.println( "Output file already exists. Overwriting...");
        }
        JSONObject output;

        try {
//            jsonString = FileUtils.readFileToString(new File(args[0]), StandardCharsets.UTF_8);
            {
                String jsonString;
                Path path = Paths.get(inputFile);
                jsonString = Files.lines(path).collect(Collectors.joining());

                output = new JSONObject(jsonString);
            }

            List<String> list = udm2( output);

            Path outputFilePath = Paths.get(outputFile);
            list.sort( Comparator.naturalOrder());
            list.add( 0, "udmTable,ColumnName,ColumnType,ColumnDisplayName");

            Files.write( outputFilePath, list);

        } catch (JSONException | IOException e) {
            System.err.println("Unable to process JSON -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    public static List<String>  udm2( JSONObject output) {

        JSONArray mapArray = output.getJSONArray("content");

        JSONObject udmTableObj;
        JSONArray udmColumnsArr;

        String tableName, columnName, columnType, displayName;

        List<String> list = new ArrayList<>();

        for( int i=0 ; i<mapArray.length() ; ++i) {
            udmTableObj = mapArray.getJSONObject( i);
            udmColumnsArr = udmTableObj.getJSONObject( "columns").getJSONArray("content");
            tableName = udmTableObj.getString("name");

            for( int j=0 ; j<udmColumnsArr.length() ; ++j) {
                columnName = udmColumnsArr.getJSONObject( j).getString("name");
                columnType = udmColumnsArr.getJSONObject( j).getString("type");
                displayName = udmColumnsArr.getJSONObject( j).getString("displayName");

                list.add( tableName+","+columnName+","+columnType+","+displayName);
            }
        }
        return list;
    }

    private static void mapping( String inputFile, String outputFile) {
        if( ! new File(inputFile).isFile()) {
            System.out.println( "Input file does not exist. " + inputFile);
            return;
        }

        if( new File(outputFile).isFile()) {
            System.out.println( "Output file already exists. Overwriting...");
        }

        JSONObject output;

        try {
//            jsonString = FileUtils.readFileToString(new File(args[0]), StandardCharsets.UTF_8);
            {
                String jsonString;
                Path path = Paths.get(inputFile);
                jsonString = Files.lines(path).collect(Collectors.joining());

                output = new JSONObject(jsonString);
            }

            List<String> list = mapping2( output);

            Path outputFilePath = Paths.get(outputFile);
            list.sort( Comparator.naturalOrder());
            list.add( 0, "AIFTable,AIFColumn,UDMTable,UDMColumn");

            Files.write( outputFilePath, list);

        } catch (JSONException | IOException e) {
            System.err.println("Unable to process JSON -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    public static List<String> mapping2( JSONObject output) {
        JSONArray mapArray = output.getJSONArray("mapping");
        JSONArray innerArr;
        JSONObject aIFMappingObj;
        JSONObject udfMappingObj;
        JSONArray udfMappingArr;

        List<String> list = new ArrayList<>();
//            list.add("AIFTable,AIFColumn,UDMTable,UDMColumn");

        for( int i=0 ; i<mapArray.length() ; ++i) {
            innerArr = mapArray.getJSONArray( i);

            aIFMappingObj = innerArr.getJSONObject(0);
            udfMappingArr = innerArr.getJSONArray(1);

            String aifMappingStr = aIFMappingObj.getString("table") + "," + aIFMappingObj.getString("column");
//                System.out.println(aifTable + ", " + aifColumn) ;

            for( int j=0 ; j<udfMappingArr.length() ; ++j) {
                udfMappingObj = udfMappingArr.getJSONObject(j);
                String udfTable = udfMappingObj.getString("table");
                String udfColumn = udfMappingObj.getString("column");
//                    System.out.println(udfTable + ", " + udfColumn) ;
                list.add( aifMappingStr + "," + udfTable + "," + udfColumn);
            }
        }
        return list;

    }

    public static void compare(List<String>  csCSV, List<String>  prodCSV, String udmOrMapping) {
        List<String> newItems = new ArrayList<>();
        List<String> updateItems = new ArrayList<>();

        Map<String,String> csMap = new HashMap<>();
        Map<String,String> prodMap = new HashMap<>();

        for( String item : csCSV) {
            String[] arr = item.split(",");
            csMap.put( (arr[0]+arr[1]).toLowerCase(), item);
        }

        for( String item : prodCSV) {
            String[] arr = item.split(",");
            prodMap.put( (arr[0]+arr[1]).toLowerCase(), item);
        }

        for( Map.Entry<String,String> entry : csMap.entrySet()) {
            if( prodMap.get( entry.getKey()) == null) {
                newItems.add( entry.getValue());
            }
            else if( prodMap.get( entry.getKey()).equals(entry.getValue()) == false) {
                updateItems.add( entry.getValue());
            }
            else continue;
        }

        if( newItems.size() != 0 || updateItems.size() != 0) {
            System.out.println( "New Items:");
            for (String item : newItems)
                System.out.println( item);

            System.out.println();
            System.out.println( "Updated/Modified Items:");
            for (String item : updateItems)
                System.out.println( item);
        }
        else System.out.println( "Both environments are identical.");

    }
}
