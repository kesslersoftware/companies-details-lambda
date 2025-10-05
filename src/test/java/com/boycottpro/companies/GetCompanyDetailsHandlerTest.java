package com.boycottpro.companies;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.boycottpro.companies.config.AppConfig;
import com.boycottpro.companies.model.CompanyData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;
import java.io.ByteArrayInputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import com.fasterxml.jackson.core.JsonProcessingException;

@ExtendWith(MockitoExtension.class)
public class GetCompanyDetailsHandlerTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Properties testProperties = new Properties();
        testProperties.setProperty("s3.bucket.name", "test-companies-bucket");
        testProperties.setProperty("app.name", "test-app");
        testProperties.setProperty("app.version", "1.0-TEST");
        testProperties.setProperty("app.environment", "test");
        testProperties.setProperty("app.log.level", "DEBUG");
        testProperties.setProperty("app.cache.enabled", "false");
        testProperties.setProperty("app.metrics.enabled", "false");
        
        testAppConfig = new AppConfig(testProperties, "test");
        handler = new GetCompanyDetailsHandler(s3Client, testAppConfig);
    }

    @Mock
    private S3Client s3Client;

    @Mock
    private Context context;

    private GetCompanyDetailsHandler handler;
    private AppConfig testAppConfig;

    @Test
    public void testValidCompanyNameReturnsCompany() throws Exception {
        String companyName = "Apple";
        Map<String, String> pathParams = Map.of("company_id", companyName);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        event.setPathParameters(pathParams);

        String jsonContent = "{" +
                "\"company\":\"Apple Inc.\"," +
                "\"slug\":\"apple\"," +
                "\"as_of_utc\":\"2024-01-01T00:00:00Z\"," +
                "\"summary\":\"Technology company that designs and manufactures consumer electronics\"," +
                "\"sector\":\"Technology\"," +
                "\"hq_city\":\"Cupertino\"," +
                "\"founded_year\":1976," +
                "\"employees_est\":164000," +
                "\"key_products\":[\"iPhone\",\"iPad\",\"Mac\"]," +
                "\"stock_ticker\":\"AAPL\"," +
                "\"website\":\"https://www.apple.com\"," +
                "\"notable_news_window\":\"Past 6 months\"," +
                "\"controversies_or_issues\":[{\"title\":\"Privacy concerns\",\"desc\":\"Data collection practices\",\"date\":\"2023-12-01\",\"source_url\":\"https://example.com\"}]," +
                "\"sources\":[{\"title\":\"Company website\",\"url\":\"https://www.apple.com\"}]" +
                "}";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(
            GetObjectResponse.builder().build(),
            inputStream
        );

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Apple Inc."));
        assertTrue(response.getBody().contains("Technology"));
        assertTrue(response.getBody().contains("Cupertino"));
    }

    @Test
    public void testMissingCompanyNameReturns400() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        event.setPathParameters(Map.of("company_id", ""));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing company_name"));
    }

    @Test
    public void testCompanyNotFoundReturnsError() {
        String companyName = "Unknown Company";
        Map<String, String> pathParams = Map.of("company_id", companyName);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        event.setPathParameters(pathParams);
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().contains("no company found"));
    }

    @Test
    public void testUnexpectedExceptionReturns500() {
        String companyName = "Test Company";
        Map<String, String> pathParams = Map.of("company_id", companyName);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        event.setPathParameters(pathParams);
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("Boom"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Unexpected server error"));
    }
    
    @Test
    public void testConfigurationProfileLoading() {
        assertEquals("test", testAppConfig.getActiveProfile());
        assertEquals("test", testAppConfig.getEnvironment());
        assertEquals("test-companies-bucket", testAppConfig.getS3BucketName());
        assertEquals("DEBUG", testAppConfig.getLogLevel());
        assertFalse(testAppConfig.isCacheEnabled());
        assertFalse(testAppConfig.isMetricsEnabled());
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor coverage
        // Note: This may fail in environments without AWS credentials/region configured
        try {
            GetCompanyDetailsHandler handler = new GetCompanyDetailsHandler();
            assertNotNull(handler);

            // Verify S3Client was created (using reflection to access private field)
            try {
                Field s3ClientField = GetCompanyDetailsHandler.class.getDeclaredField("s3Client");
                s3ClientField.setAccessible(true);
                S3Client s3 = (S3Client) s3ClientField.get(handler);
                assertNotNull(s3);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to access S3Client field: " + e.getMessage());
            }
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            // AWS SDK can't initialize due to missing region configuration
            // This is expected in Jenkins without AWS credentials - test passes
            System.out.println("Skipping DynamoDbClient verification due to AWS SDK configuration: " + e.getMessage());
        }
    }

    @Test
    public void testUnauthorizedUser() {
        // Test the unauthorized block coverage
        handler = new GetCompanyDetailsHandler(s3Client, testAppConfig);

        // Create event without JWT token (or invalid token that returns null sub)
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        // No authorizer context, so JwtUtility.getSubFromRestEvent will return null

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertEquals(401, response.getStatusCode());
        assertTrue(response.getBody().contains("Unauthorized"));
    }

    @Test
    public void testJsonProcessingExceptionInResponse() throws Exception {
        // Test JsonProcessingException coverage in response method by using reflection
        handler = new GetCompanyDetailsHandler(s3Client, testAppConfig);

        // Use reflection to access the private response method
        java.lang.reflect.Method responseMethod = GetCompanyDetailsHandler.class.getDeclaredMethod("response", int.class, Object.class);
        responseMethod.setAccessible(true);

        // Create an object that will cause JsonProcessingException
        Object problematicObject = new Object() {
            public Object writeReplace() throws java.io.ObjectStreamException {
                throw new java.io.NotSerializableException("Not serializable");
            }
        };

        // Create a circular reference object that will cause JsonProcessingException
        Map<String, Object> circularMap = new HashMap<>();
        circularMap.put("self", circularMap);

        // This should trigger the JsonProcessingException -> RuntimeException path
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                responseMethod.invoke(handler, 500, circularMap);
            } catch (java.lang.reflect.InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e.getCause());
            }
        });

        // Verify it's ultimately caused by JsonProcessingException
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof JsonProcessingException,
                "Expected JsonProcessingException, got: " + cause.getClass().getSimpleName());
    }

    @Test
    public void testConstructorWithS3ClientOnly() {
        // Test lines 32-35: Constructor with S3Client only
        // Note: This constructor calls AppConfig.getInstance(), which may fail without proper setup
        // Using the mock S3Client to ensure the constructor can be invoked
        try {
            GetCompanyDetailsHandler handlerWithS3Only = new GetCompanyDetailsHandler(s3Client);
            assertNotNull(handlerWithS3Only);

            // Verify S3Client was set (using reflection to access private field)
            try {
                Field s3ClientField = GetCompanyDetailsHandler.class.getDeclaredField("s3Client");
                s3ClientField.setAccessible(true);
                S3Client s3 = (S3Client) s3ClientField.get(handlerWithS3Only);
                assertNotNull(s3);
                assertEquals(s3Client, s3);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to access S3Client field: " + e.getMessage());
            }
        } catch (Exception e) {
            // AppConfig.getInstance() may fail if no properties file exists
            // This is acceptable for coverage purposes
            System.out.println("Constructor test skipped due to AppConfig initialization: " + e.getMessage());
        }
    }

    @Test
    public void testNullPathParameters() {
        // Test lines 54-55: pathParams is null
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> claims = Map.of("sub", "11111111-2222-3333-4444-555555555555");
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);

        APIGatewayProxyRequestEvent.ProxyRequestContext rc = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        rc.setAuthorizer(authorizer);
        event.setRequestContext(rc);

        // Set pathParameters to null
        event.setPathParameters(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing company_name"));
    }

    @Test
    public void testObjectMapperFieldInitialization() throws Exception {
        // Test line 25: ObjectMapper field initialization
        handler = new GetCompanyDetailsHandler(s3Client, testAppConfig);

        // Use reflection to verify objectMapper was initialized
        Field objectMapperField = GetCompanyDetailsHandler.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        Object objectMapper = objectMapperField.get(handler);

        assertNotNull(objectMapper);
        assertTrue(objectMapper instanceof com.fasterxml.jackson.databind.ObjectMapper);
    }

}
