package com.boycottpro.companies.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Controversy {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("desc")
    private String desc;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("source_url")
    private String sourceUrl;
    
    public Controversy() {
    }
    
    public Controversy(String title, String desc, String date, String sourceUrl) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.sourceUrl = sourceUrl;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    @Override
    public String toString() {
        return "Controversy{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                '}';
    }
}