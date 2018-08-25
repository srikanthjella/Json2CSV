package com.agilone.impl.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class TestJSONDiff {
    public static void main(String[] args) {
        String json1 = "{\"name\":\"ABC\", \"city\":\"XYZ\", \"state\":\"CA\"}";
        String json2 = "{\"city\":\"XYZ\", \"street\":\"123 anyplace\", \"name\":\"ABC\"}";

        String str = "CategoryNumber\tCategoryName\tParentCategoryNumber\tHierarchyName\tDateCreated\tDateModified\tDelete_Flag";
        String arr[] = str.split("\t");

        System.out.println( arr[0]);
        System.out.println( arr[1]);
        System.exit(0);
        Gson g = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> firstMap = g.fromJson(json1, mapType);
        Map<String, Object> secondMap = g.fromJson(json2, mapType);
        System.out.println(Maps.difference(firstMap, secondMap));
    }

}
