package com.agilone.impl.util;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String CONNECTORID = "connectorid";
    public static final String PASSWORD = "password";
    public static String TENANT_ID = "tenantid";
    public static String ENV = "env";
    public static String OPERATION = "operation";
    public static String LOCATION = "location";
    public static String TOKEN = "token";
    public static String USERNAME = "username";
    public static String INPUTFILE = "inputfile";
    public static String TABLE_ID = "tableId";
    public static String NAME = "name";
    public static String UNPUBLISHED = "unPublished";
    public static String READONLY = "readOnly";
    public static String DISPLAYNAME = "displayName";
    public static String ATTRIBUTETYPE = "type";
    public static String AVAILABILITY = "availability";
    public static String LOOKUP = "lookup";
    public static String NEW = "new";
    public static String ADDATTRIBUTES = "AddAttributes";
    public static String MAPPING = "Mapping";
    public static String CONNECTORDEF = "connectorDef";
    public static String CONNECTORINCREMENT = "connectorIncrement";

    public static List<String> VALID_DATA_TYPES = new ArrayList<>(7);
    public static List<String> VALID_AVAILABILITY = new ArrayList<>(7);
    static {
        VALID_DATA_TYPES.add( "INTEGER");
        VALID_DATA_TYPES.add( "LONG");
        VALID_DATA_TYPES.add( "STRING");
        VALID_DATA_TYPES.add( "DOUBLE");
        VALID_DATA_TYPES.add( "BOOLEAN");
        VALID_DATA_TYPES.add( "DECIMAL");
        VALID_DATA_TYPES.add( "DATE");

        VALID_AVAILABILITY.add( "CAMPAIGN");
        VALID_AVAILABILITY.add( "CONTENT");
        VALID_AVAILABILITY.add( "API");
        VALID_AVAILABILITY.add( "SUMMARY");
        VALID_AVAILABILITY.add( "CUBE");
        VALID_AVAILABILITY.add( "EXPORT");
        VALID_AVAILABILITY.add( "LOOKUP");
        VALID_AVAILABILITY.add( "PII");
        VALID_AVAILABILITY.add( "SUMMARY");
    }
}
