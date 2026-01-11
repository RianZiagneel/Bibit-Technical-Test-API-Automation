package com.qa.utils;

import com.qa.models.Address;
import com.qa.models.AddressResponse;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class APIValidator {
    private List<String> validationErrors = new ArrayList<>();
    
    // Validate status code
    public void validateStatusCode(Response response, int expectedCode) {
        int actualCode = response.getStatusCode();
        if (actualCode != expectedCode) {
            validationErrors.add("Expected status " + expectedCode + " but got " + actualCode);
        }
    }
    
    // Validate response time
    public void validateResponseTime(Response response, long maxTime) {
        long actualTime = response.getTime();
        if (actualTime > maxTime) {
            validationErrors.add("Response time " + actualTime + "ms exceeded max " + maxTime + "ms");
        }
    }
    
    // Validate top-level response fields
    public void validateTopLevelFields(AddressResponse addressResponse, String expectedLocale, Integer expectedTotal) {
        if (!"OK".equals(addressResponse.getStatus())) {
            validationErrors.add("Status should be 'OK' but got '" + addressResponse.getStatus() + "'");
        }
        
        if (addressResponse.getCode() != 200) {
            validationErrors.add("Code should be 200 but got " + addressResponse.getCode());
        }
        
        if (expectedLocale != null && !expectedLocale.equals(addressResponse.getLocale())) {
            validationErrors.add("Locale should be '" + expectedLocale + "' but got '" + addressResponse.getLocale() + "'");
        }
        
        if (expectedTotal != null && addressResponse.getTotal() != expectedTotal) {
            validationErrors.add("Total should be " + expectedTotal + " but got " + addressResponse.getTotal());
        }
    }
    
    // Validate data array size
    public void validateDataArraySize(AddressResponse addressResponse) {
        int dataSize = addressResponse.getData().size();
        int total = addressResponse.getTotal();
        
        if (dataSize != total) {
            validationErrors.add("Data array size (" + dataSize + ") doesn't match total (" + total + ")");
        }
    }
    
    // Validate single address object
    public void validateAddressObject(Address address, int index) {
        String prefix = "data[" + index + "]";
        
        // Validate required fields exist
        if (address.getId() == 0) validationErrors.add(prefix + ".id is missing or 0");
        if (address.getStreet() == null || address.getStreet().isEmpty()) 
            validationErrors.add(prefix + ".street is missing");
        if (address.getStreetName() == null || address.getStreetName().isEmpty()) 
            validationErrors.add(prefix + ".streetName is missing");
        if (address.getBuildingNumber() == null || address.getBuildingNumber().isEmpty()) 
            validationErrors.add(prefix + ".buildingNumber is missing");
        if (address.getCity() == null || address.getCity().isEmpty()) 
            validationErrors.add(prefix + ".city is missing");
        if (address.getZipcode() == null || address.getZipcode().isEmpty()) 
            validationErrors.add(prefix + ".zipcode is missing");
        if (address.getCountry() == null || address.getCountry().isEmpty()) 
            validationErrors.add(prefix + ".country is missing");
        if (address.getCountryCode() == null || address.getCountryCode().isEmpty()) 
            validationErrors.add(prefix + ".country_code is missing");
        
        // Validate data types (implicit through model mapping)
        // Jackson will throw exception if types don't match
    }
    
    // Validate all address objects
    public void validateAllAddresses(AddressResponse addressResponse) {
        List<Address> addresses = addressResponse.getData();
        for (int i = 0; i < addresses.size(); i++) {
            validateAddressObject(addresses.get(i), i);
        }
    }
    
    // Assert all validations passed
    public void assertAllPassed() {
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Validation failed:\n" + String.join("\n", validationErrors);
            Assert.fail(errorMessage);
        }
    }
    
    // Get validation errors
    public List<String> getValidationErrors() {
        return validationErrors;
    }
    
    // Check if all passed
    public boolean allPassed() {
        return validationErrors.isEmpty();
    }
}