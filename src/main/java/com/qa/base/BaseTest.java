package com.qa.base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected static final String BASE_URL = "https://fakerapi.it/api/v2";
    protected RequestSpecification request;
    
    @BeforeClass
    public void setup() {
        // Set base URI for all tests
        RestAssured.baseURI = BASE_URL;
        
        // Initialize request specification
        request = RestAssured.given()
            .header("Content-Type", "application/json")
            .log().all(); // Log all request details
    }
    
    // Helper method to make GET request
    protected Response getRequest(String endpoint, String queryParams) {
        return request
            .queryParam("_locale", extractParam(queryParams, "_locale"))
            .queryParam("_quantity", extractParam(queryParams, "_quantity"))
            .queryParam("_seed", extractParam(queryParams, "_seed"))
            .when()
            .get(endpoint)
            .then()
            .log().all() // Log all response details
            .extract().response();
    }
    
    // Extract parameter from query string
    private String extractParam(String queryParams, String paramName) {
        if (queryParams == null || !queryParams.contains(paramName)) {
            return null;
        }
        String[] params = queryParams.split("&");
        for (String param : params) {
            if (param.startsWith(paramName + "=")) {
                return param.split("=")[1];
            }
        }
        return null;
    }
}