package com.qa.tests;

import com.qa.base.BaseTest;
import com.qa.models.AddressResponse;
import com.qa.utils.APIValidator;
import io.restassured.RestAssured;  // <-- ADD THIS
import io.restassured.response.Response;
import org.testng.Assert;  // <-- ADD THIS
import org.testng.annotations.Test;

public class NegativeTests extends BaseTest {
    
    @Test(priority = 5, description = "TC05: Verify missing locale uses default")
    public void testMissingLocale() {
        System.out.println("\n=== TEST CASE 5: Negative - Missing Locale ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_quantity", "2")
                .queryParam("_seed", "24157")
                // No _locale parameter
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        
        AddressResponse addressResponse = response.as(AddressResponse.class);
        
        // Check default locale is provided
        if (addressResponse.getLocale() == null || addressResponse.getLocale().isEmpty()) {
            validator.getValidationErrors().add("Default locale should be provided");
        }
        
        System.out.println("Default locale used: " + addressResponse.getLocale());
        validator.validateDataArraySize(addressResponse);
        
        validator.assertAllPassed();
        System.out.println("✓ Missing locale handled correctly!");
    }
    
    @Test(priority = 6, description = "TC06: Verify invalid locale returns error or default")
    public void testInvalidLocale() {
        System.out.println("\n=== TEST CASE 6: Negative - Invalid Locale ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "INVALID_XX")
                .queryParam("_quantity", "2")
                .queryParam("_seed", "24157")
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        int statusCode = response.getStatusCode();
        
        if (statusCode == 400 || statusCode == 422) {
            System.out.println("✓ API rejects invalid locale with status " + statusCode);
        } else if (statusCode == 200) {
            AddressResponse addressResponse = response.as(AddressResponse.class);
            System.out.println("✓ API falls back to default locale: " + addressResponse.getLocale());
            
            APIValidator validator = new APIValidator();
            validator.validateDataArraySize(addressResponse);
            validator.assertAllPassed();
        } else {
            Assert.fail("Unexpected status code: " + statusCode);
        }
    }
    
    @Test(priority = 7, description = "TC07: Verify missing quantity uses default")
    public void testMissingQuantity() {
        System.out.println("\n=== TEST CASE 7: Negative - Missing Quantity ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "id_ID")
                .queryParam("_seed", "24157")
                // No _quantity parameter
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        
        AddressResponse addressResponse = response.as(AddressResponse.class);
        
        System.out.println("Default quantity used: " + addressResponse.getTotal());
        
        if (addressResponse.getTotal() <= 0) {
            validator.getValidationErrors().add("Default quantity should be greater than 0");
        }
        
        validator.validateDataArraySize(addressResponse);
        validator.assertAllPassed();
        System.out.println("✓ Missing quantity handled correctly!");
    }
    
    @Test(priority = 8, description = "TC08: Verify missing seed still generates valid data")
    public void testMissingSeed() {
        System.out.println("\n=== TEST CASE 8: Negative - Missing Seed ===");
        
        Response response = RestAssured
            .given()
                .baseUri(BASE_URL)
                .queryParam("_locale", "id_ID")
                .queryParam("_quantity", "2")
                // No _seed parameter
            .when()
                .get("/addresses")
            .then()
                .log().all()
                .extract().response();
        
        APIValidator validator = new APIValidator();
        validator.validateStatusCode(response, 200);
        
        AddressResponse addressResponse = response.as(AddressResponse.class);
        validator.validateDataArraySize(addressResponse);
        validator.validateAllAddresses(addressResponse);
        
        validator.assertAllPassed();
        System.out.println("✓ Missing seed generates random valid data!");
        System.out.println("First address city: " + addressResponse.getData().get(0).getCity());
    }
}