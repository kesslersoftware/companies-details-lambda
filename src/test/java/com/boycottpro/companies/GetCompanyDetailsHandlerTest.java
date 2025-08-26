package com.boycottpro.companies;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.boycottpro.models.Companies;
import com.boycottpro.utilities.CompanyUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCompanyDetailsHandlerTest {

    @Mock
    private DynamoDbClient dynamoDb;

    @Mock
    private Context context;

    @InjectMocks
    private GetCompanyDetailsHandler handler;

    @Test
    public void testValidCompanyIdReturnsCompany() throws Exception {
        String companyId = "c1";
        Map<String, String> pathParams = Map.of("company_id", companyId); // note: still using "user_id" key as in handler
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        // Path param "s" since client calls /users/s
        event.setPathParameters(pathParams);

        Map<String, AttributeValue> item = Map.ofEntries(
                Map.entry("company_id", AttributeValue.fromS("c1")),
                Map.entry("company_name", AttributeValue.fromS("Apple")),
                Map.entry("description", AttributeValue.fromS("computer company")),
                Map.entry("industry", AttributeValue.fromS("software")),
                Map.entry("city", AttributeValue.fromS("New York")),
                Map.entry("state", AttributeValue.fromS("NY")),
                Map.entry("zip", AttributeValue.fromS("11111")),
                Map.entry("employees", AttributeValue.fromN("2222")),
                Map.entry("revenue", AttributeValue.fromN("33333333")),
                Map.entry("valuation", AttributeValue.fromN("4444444444")),
                Map.entry("profits", AttributeValue.fromN("5555")),
                Map.entry("stock_symbol", AttributeValue.fromS("APL")),
                Map.entry("ceo", AttributeValue.fromS("fred")),
                Map.entry("boycott_count", AttributeValue.fromN("10"))
        );

        when(dynamoDb.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Apple"));
        assertTrue(response.getBody().contains("c1"));
    }

    @Test
    public void testMissingCompanyIdReturns400() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        // Path param "s" since client calls /users/s
        event.setPathParameters(Map.of("company_id", ""));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing company_id"));
    }

    @Test
    public void testCompanyNotFoundReturnsError() {
        String companyId = "unknown";
        Map<String, String> pathParams = Map.of("company_id", companyId);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        // Path param "s" since client calls /users/s
        event.setPathParameters(pathParams);
        when(dynamoDb.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().build());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("no company found"));
    }

    @Test
    public void testUnexpectedExceptionReturns500() {
        String companyId = "crash";
        Map<String, String> pathParams = Map.of("company_id", companyId);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        // Path param "s" since client calls /users/s
        event.setPathParameters(pathParams);
        when(dynamoDb.getItem(any(GetItemRequest.class)))
                .thenThrow(new RuntimeException("Boom"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Unexpected server error"));
    }
}
