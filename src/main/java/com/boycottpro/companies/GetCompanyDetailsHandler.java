package com.boycottpro.companies;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.boycottpro.utilities.JwtUtility;
import com.boycottpro.companies.config.AppConfig;
import com.boycottpro.companies.model.CompanyData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;

import java.util.*;
import java.util.stream.Collectors;

public class GetCompanyDetailsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final S3Client s3Client;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetCompanyDetailsHandler() {
        this.s3Client = S3Client.create();
        this.appConfig = AppConfig.getInstance();
    }

    public GetCompanyDetailsHandler(S3Client s3Client) {
        this.s3Client = s3Client;
        this.appConfig = AppConfig.getInstance();
    }

    public GetCompanyDetailsHandler(S3Client s3Client, AppConfig appConfig) {
        this.s3Client = s3Client;
        this.appConfig = appConfig;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String sub = null;
        try {
            sub = JwtUtility.getSubFromRestEvent(event);
            if (sub == null) return response(401, Map.of("message", "Unauthorized"));
            
            // Log environment info for debugging
            System.out.println("Active profile: " + appConfig.getActiveProfile() + 
                             ", Environment: " + appConfig.getEnvironment() + 
                             ", S3 Bucket: " + appConfig.getS3BucketName());
            
            Map<String, String> pathParams = event.getPathParameters();
            String companyName = (pathParams != null) ? pathParams.get("company_name") : null;
            if (companyName == null || companyName.isEmpty()) {
                return response(400,Map.of("error", "Missing company_name in path"));
            }
            CompanyData company = getCompanyByName(companyName);
            if (company == null) {
                return response(404,Map.of("error", "no company found!"));
            }
            return response(200,company);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " for user " + sub + " in environment " + appConfig.getEnvironment());
            return response(500,Map.of("error", "Unexpected server error: " + e.getMessage()) );
        }
    }
    private APIGatewayProxyResponseEvent response(int status, Object body) {
        String responseBody = null;
        try {
            responseBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(responseBody);
    }
    private CompanyData getCompanyByName(String companyName) {
        try {
            String key = String.format("companies/%s.json", companyName.toLowerCase().replace(" ", "-"));
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(appConfig.getS3BucketName())
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            
            String jsonContent = new String(s3Object.readAllBytes());
            return objectMapper.readValue(jsonContent, CompanyData.class);
            
        } catch (NoSuchKeyException e) {
            return null; // Company file not found
        } catch (Exception e) {
            throw new RuntimeException("Error reading company data from S3", e);
        }
    }
}