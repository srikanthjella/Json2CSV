package com.agilone.impl.configapp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ConfigApp {

    private static final Logger log = LoggerFactory.getLogger(ConfigApp.class);
    public static Map<String,String> map;
    private  static final String CS_US_URL_SHORT = "https://cs-configapi.agilone.com/";
    public  static final String CS_US_URL = "https://cs-configapi.agilone.com/v2/";
//    public  static final String CS_EU_URL_SHORT = "https://cs-configapi.eu.agilone.com/";
//    public  static final String CS_EU_URL = "https://cs-configapi.eu.agilone.com/v2/";
//    private static final String PROD_URL = "";
    private static final String UDMP_URL = "/config/UDMPTables";
    private static final String CONNECTOR_URL = "/config/connectors/";
    private static final String INCLUDE_COLUMNS = "?include=columns&limit=500&offset=0";
    private static final String EXCLUDE_COLUMNS = "?limit=500&offset=0";

    static {
        Map<String,String> map2 = new HashMap<>(4);
        map2.put("CSUS","https://cs-configapi.agilone.com/");
        map2.put("CSEU","https://cs-configapi.agilone.com/");
        map2.put("PRODUS","https://cs-configapi.agilone.com/");
        map2.put("PRODEU","https://cs-configapi.agilone.com/");

        map = Collections.unmodifiableMap( map2);
    }

    private String callRestAPI( String url, String token, HttpMethod method, String putBody) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> acceptList = new ArrayList<>();
        acceptList.add( MediaType.APPLICATION_JSON_UTF8);
        acceptList.add( MediaType.TEXT_PLAIN);
        acceptList.add( MediaType.APPLICATION_JSON);
        headers.setAccept( acceptList);
        headers.setContentType( MediaType.APPLICATION_JSON_UTF8);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>( putBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity;

        responseEntity = restTemplate.exchange(url
                    , method, requestEntity
                    , String.class);

        log.info(responseEntity.getStatusCode().toString());

        return responseEntity.getBody();
    }

    public String getAllUdmPTables(String url, String token, String tenantId) {
        return callRestAPI( url + tenantId + UDMP_URL + INCLUDE_COLUMNS, token, HttpMethod.GET, "");
    }

    public String getUdmPTable(String url, String token, String tenantId) {
        return callRestAPI( url + tenantId + UDMP_URL + EXCLUDE_COLUMNS, token, HttpMethod.GET, "");
    }

    public String putUdmPTable( String token, String tenantId, String tableID, String body) {
        return callRestAPI( CS_US_URL + tenantId + UDMP_URL + "/" + tableID, token, HttpMethod.PUT, body);
    }

    public String getConnector(String token, int connectorID, String tenantId) {
        return callRestAPI( CS_US_URL + tenantId + CONNECTOR_URL + connectorID, token, HttpMethod.GET, "");
    }

    public String putConnector(String token, String tenantId, int connectorID, String body) {
        return callRestAPI( CS_US_URL + tenantId + CONNECTOR_URL + connectorID, token, HttpMethod.PUT, body);
    }

    public String getToken( String URL, String encoding) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encoding);

        System.out.println( encoding);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity;
        HttpEntity<String> requestEntity = new HttpEntity<>( "", headers);

        responseEntity = restTemplate.exchange( CS_US_URL_SHORT + "token?action=create&scheme=a1user"
                , HttpMethod.POST, requestEntity
                , String.class);

        log.info(responseEntity.getStatusCode().toString());

        return new JSONObject(responseEntity.getBody()).getString("access_token");
    }
}
