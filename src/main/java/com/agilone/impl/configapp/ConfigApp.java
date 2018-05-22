package com.agilone.impl.configapp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ConfigApp {

    private static final Logger log = LoggerFactory.getLogger(ConfigApp.class);
    public  static final String CS_US_URL = "https://cs-configapi.agilone.com/v2/";
//    private static final String PROD_URL = "https://cs-configapi.agilone.com/v2/";
    private static final String ATHLETA_CS_TENANT_ID = "60";
//    private static final String MC_CDP_TENANT_ID = "142";
    private static final String UDMP_URL = "/config/UDMPTables";
    private static final String INCLUDE_COLUMNS = "?include=columns&limit=500&offset=0";
    private static final String EXCLUDE_COLUMNS = "?limit=500&offset=0";
    private static final String putUdmpTable = "";

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
//            log.info(responseEntity.getBody().toString());
        JSONObject json = new JSONObject( responseEntity.getBody());
//        log.info( json.toString());

        return json.toString();
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
}
