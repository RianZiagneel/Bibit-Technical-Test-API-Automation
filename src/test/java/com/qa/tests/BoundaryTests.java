package com.qa.tests;

import com.qa.base.BaseTest;
import com.qa.models.AddressResponse;
import com.qa.utils.APIValidator;
import io.restassured.RestAssured;  // <-- ADD THIS
import io.restassured.response.Response;
import org.testng.Assert;  // <-- ADD THIS
import org.testng.annotations.Test;

public class BoundaryTests extends BaseTest {
    
    @Test(priority = 3, description = "TC03: Verify maximum quantity (999) handles correctly")
    public void testMaximumQuantity() {
        System.out.println("\n=== TEST CASE 3: Boundary - Maximum Quantity ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "id_ID")
                .queryParam("_quantity", "999")
                .queryParam("_seed", "24157")
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        validator.validateResponseTime(response, 10000); // 10 seconds for large dataset
        
        // Check if API enforces limit or returns 999
        AddressResponse addressResponse = response.as(AddressResponse.class);
        System.out.println("Actual total returned: " + addressResponse.getTotal());
        
        // Validate data size matches total
        validator.validateDataArraySize(addressResponse);
        
        validator.assertAllPassed();
        System.out.println("✓ Maximum quantity handled correctly!");
    }
    
    @Test(priority = 4, description = "TC04: Verify zero quantity behavior")
    public void testZeroQuantity() {
        System.out.println("\n=== TEST CASE 4: Boundary - Zero Quantity ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "id_ID")
                .queryParam("_quantity", "0")
                .queryParam("_seed", "24157")
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        int statusCode = response.getStatusCode();
        APIValidator validator = new APIValidator();
        
        if (statusCode == 200) {
            AddressResponse addressResponse = response.as(AddressResponse.class);
            
            // API uses default quantity instead of 0
            System.out.println("✓ API uses default quantity: " + addressResponse.getTotal());
            
            // Just verify data size matches total
            validator.validateDataArraySize(addressResponse);
            validator.assertAllPassed();
        } else if (statusCode == 400 || statusCode == 422) {
            System.out.println("✓ API properly rejects zero quantity with status " + statusCode);
        } else {
            Assert.fail("Unexpected status code: " + statusCode);
        }
    }
}