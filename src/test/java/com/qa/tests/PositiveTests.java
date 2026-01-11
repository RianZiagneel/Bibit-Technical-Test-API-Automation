package com.qa.tests;

import com.qa.base.BaseTest;
import com.qa.models.AddressResponse;
import com.qa.utils.APIValidator;
import io.restassured.RestAssured;  // <-- ADD THIS
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class PositiveTests extends BaseTest {
    
    @Test(priority = 1, description = "TC01: Verify GET request with valid parameters returns correct data")
    public void testValidAddressRequest() {
        System.out.println("\n=== TEST CASE 1: Happy Path - Valid Address Request ===");
        
        // Make request
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "id_ID")
                .queryParam("_quantity", "2")
                .queryParam("_seed", "24157")
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        // Parse response
        AddressResponse addressResponse = response.as(AddressResponse.class);
        
        // Validate
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        validator.validateResponseTime(response, 5000);
        validator.validateTopLevelFields(addressResponse, "id_ID", 2);
        validator.validateDataArraySize(addressResponse);
        validator.validateAllAddresses(addressResponse);
        
        // Assert all passed
        validator.assertAllPassed();
        System.out.println("✓ All validations passed!");
    }
    
    @Test(priority = 2, description = "TC02: Verify different endpoint (users) works correctly")
    public void testValidUsersRequest() {
        System.out.println("\n=== TEST CASE 2: Happy Path - Valid Users Request ===");
        
        // Make request
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "en_US")
                .queryParam("_quantity", "5")
                .queryParam("_seed", "12345")
            .when()
                .get("/users")
            .then()
                .log().all()
                .extract().response();
        
        // Basic validations
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        validator.validateResponseTime(response, 3000);
        
        // Validate response structure
        String status = response.jsonPath().getString("status");
        int total = response.jsonPath().getInt("total");
        int dataSize = response.jsonPath().getList("data").size();
        
        if (!"OK".equals(status)) {
            validator.getValidationErrors().add("Status should be OK");
        }
        if (total != 5) {
            validator.getValidationErrors().add("Total should be 5");
        }
        if (dataSize != 5) {
            validator.getValidationErrors().add("Data size should be 5");
        }
        
        validator.assertAllPassed();
        System.out.println("✓ All validations passed!");
    }
}
