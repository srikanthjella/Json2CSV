package com.agilone.impl.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

            JSONArray mapArray = output.getJSONArray("content");

            JSONObject udmTableObj;
            JSONArray udmColumnsArr;

            String tableName, columnName, columnType, displayName;

            List<String> list = new ArrayList<>();

            for( int i=0 ; i<mapArray.length() ; ++i) {
                udmTableObj = mapArray.getJSONObject( i);
                udmColumnsArr = udmTableObj.getJSONObject( "columns").getJSONArray("content");
                tableName = udmTableObj.getString("name");
//                System.out.println( "tableName : " + tableName);

                for( int j=0 ; j<udmColumnsArr.length() ; ++j) {
                    columnName = udmColumnsArr.getJSONObject( j).getString("name");
                    columnType = udmColumnsArr.getJSONObject( j).getString("type");
                    displayName = udmColumnsArr.getJSONObject( j).getString("displayName");

                    list.add( tableName+","+columnName+","+columnType+","+displayName);
                }

/*
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
*/
            }

            Path outputFilePath = Paths.get(outputFile);
            list.sort( Comparator.naturalOrder());
            list.add( 0, "udmTable,ColumnName,ColumnType,ColumnDisplayName");

            Files.write( outputFilePath, list);

        } catch (JSONException | IOException e) {
            System.err.println("Unable to process JSON -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }

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

            Path outputFilePath = Paths.get(outputFile);
            list.sort( Comparator.naturalOrder());
            list.add( 0, "AIFTable,AIFColumn,UDMTable,UDMColumn");

            Files.write( outputFilePath, list);

        } catch (JSONException | IOException e) {
            System.err.println("Unable to process JSON -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }
}
