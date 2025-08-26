package com.boycottpro.companies;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.boycottpro.models.Companies;
import com.boycottpro.utilities.CompanyUtility;
import com.boycottpro.utilities.JwtUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class GetCompanyDetailsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String TABLE_NAME = "";
    private final DynamoDbClient dynamoDb;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetCompanyDetailsHandler() {
        this.dynamoDb = DynamoDbClient.create();
    }

    public GetCompanyDetailsHandler(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String sub = JwtUtility.getSubFromRestEvent(event);
            if (sub == null) return response(401, "Unauthorized");
            Map<String, String> pathParams = event.getPathParameters();
            String companyId = (pathParams != null) ? pathParams.get("company_id") : null;
            if (companyId == null || companyId.isEmpty()) {
                return response(400,"error : Missing company_id in path");
            }
            Companies company = getCompanyById(companyId);
            if (company == null) {
                return response(500,"error : no company found!");
            }
            String responseBody = objectMapper.writeValueAsString(company);
            return response(200,responseBody);
        } catch (Exception e) {
            return response(500,"error : Unexpected server error: " +
                    e.getMessage());
        }
    }
    private APIGatewayProxyResponseEvent response(int status, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(body);
    }
    private Companies getCompanyById(String companyId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName("companies")
                .key(Map.of("company_id", AttributeValue.fromS(companyId)))
                .build();

        GetItemResponse response = dynamoDb.getItem(request);

        if (response.hasItem()) {
            return CompanyUtility.mapToCompany(response.item());
        } else {
            return null; // or throw an exception if preferred
        }
    }
}