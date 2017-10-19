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

        if( args.length != 2) {
            System.out.println( "Either input file or output file or both missing. Usage is: " +
                    "java -jar Json2CSV.jar <InputFileName> <OutputFileName>");
            return;
        }

        if( ! new File(args[0]).isFile()) {
            System.out.println( "Input file does not exist. " + args[0]);
            return;
        }

        if( new File(args[1]).isFile()) {
            System.out.println( "Output file already exists. Overwriting...");
        }

        JSONObject output;

        try {
//            jsonString = FileUtils.readFileToString(new File(args[0]), StandardCharsets.UTF_8);
            {
                String jsonString;
                Path path = Paths.get(args[0]);
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

            Path outputFile = Paths.get(args[1]);
            list.sort( Comparator.naturalOrder());
            list.add( 0, "AIFTable,AIFColumn,UDMTable,UDMColumn");

            Files.write( outputFile, list);

        } catch (JSONException | IOException e) {
            System.err.println("Unable to process JSON -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
