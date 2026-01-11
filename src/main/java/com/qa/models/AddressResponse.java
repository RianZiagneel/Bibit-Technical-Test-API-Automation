package com.qa.models;

import java.util.List;

public class AddressResponse {
    private String status;
    private int code;
    private String locale;
    private String seed;
    private int total;
    private List<Address> data;
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getSeed() { return seed; }
    public void setSeed(String seed) { this.seed = seed; }
    
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    
    public List<Address> getData() { return data; }
    public void setData(List<Address> data) { this.data = data; }
}