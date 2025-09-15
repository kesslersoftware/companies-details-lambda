package com.boycottpro.companies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CompanyData {
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("slug")
    private String slug;
    
    @JsonProperty("as_of_utc")
    private String asOfUtc;
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("sector")
    private String sector;
    
    @JsonProperty("hq_city")
    private String hqCity;
    
    @JsonProperty("founded_year")
    private Integer foundedYear;
    
    @JsonProperty("employees_est")
    private Integer employeesEst;
    
    @JsonProperty("key_products")
    private List<String> keyProducts;
    
    @JsonProperty("stock_ticker")
    private String stockTicker;
    
    @JsonProperty("website")
    private String website;
    
    @JsonProperty("notable_news_window")
    private String notableNewsWindow;
    
    @JsonProperty("controversies_or_issues")
    private List<Controversy> controversiesOrIssues;
    
    @JsonProperty("sources")
    private List<Source> sources;
    
    public CompanyData() {
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getAsOfUtc() {
        return asOfUtc;
    }
    
    public void setAsOfUtc(String asOfUtc) {
        this.asOfUtc = asOfUtc;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getSector() {
        return sector;
    }
    
    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public String getHqCity() {
        return hqCity;
    }
    
    public void setHqCity(String hqCity) {
        this.hqCity = hqCity;
    }
    
    public Integer getFoundedYear() {
        return foundedYear;
    }
    
    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }
    
    public Integer getEmployeesEst() {
        return employeesEst;
    }
    
    public void setEmployeesEst(Integer employeesEst) {
        this.employeesEst = employeesEst;
    }
    
    public List<String> getKeyProducts() {
        return keyProducts;
    }
    
    public void setKeyProducts(List<String> keyProducts) {
        this.keyProducts = keyProducts;
    }
    
    public String getStockTicker() {
        return stockTicker;
    }
    
    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getNotableNewsWindow() {
        return notableNewsWindow;
    }
    
    public void setNotableNewsWindow(String notableNewsWindow) {
        this.notableNewsWindow = notableNewsWindow;
    }
    
    public List<Controversy> getControversiesOrIssues() {
        return controversiesOrIssues;
    }
    
    public void setControversiesOrIssues(List<Controversy> controversiesOrIssues) {
        this.controversiesOrIssues = controversiesOrIssues;
    }
    
    public List<Source> getSources() {
        return sources;
    }
    
    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
    
    @Override
    public String toString() {
        return "CompanyData{" +
                "company='" + company + '\'' +
                ", slug='" + slug + '\'' +
                ", asOfUtc='" + asOfUtc + '\'' +
                ", summary='" + summary + '\'' +
                ", sector='" + sector + '\'' +
                ", hqCity='" + hqCity + '\'' +
                ", foundedYear=" + foundedYear +
                ", employeesEst=" + employeesEst +
                ", keyProducts=" + keyProducts +
                ", stockTicker='" + stockTicker + '\'' +
                ", website='" + website + '\'' +
                ", notableNewsWindow='" + notableNewsWindow + '\'' +
                ", controversiesOrIssues=" + controversiesOrIssues +
                ", sources=" + sources +
                '}';
    }
}