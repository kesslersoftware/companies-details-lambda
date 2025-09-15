package com.boycottpro.companies.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("url")
    private String url;
    
    public Source() {
    }
    
    public Source(String title, String url) {
        this.title = title;
        this.url = url;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return "Source{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}