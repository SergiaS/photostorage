package com.billable.photostorage.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class AppConfig {

    private String token;

    @Bean
    public RestTemplate getRestTemplateBean() {
        RestTemplate restTemplate = new RestTemplate();
        addHeaderTokenToRequest(restTemplate);
        return restTemplate;
    }

    /**
     * Adds header AUTHORIZATION with for each request
     */
    public void addHeaderTokenToRequest(RestTemplate restTemplate) {
        System.out.println(" === === === ");
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                System.out.println("   1 @@@   " + token );
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);

                ClientHttpResponse response = execution.execute(request, body);
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    token = getNewToken();
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);
                    System.out.println("   2 @@@   " + token);
                    return execution.execute(request, body);
                }
                System.out.println("   3 @@@   " + token);
                return response;
            }
        });
    }

    /**
     * Retrieves new token from server
     */
    private String getNewToken() {
        final String BODY_REQUEST = "{\"apiKey\":\"23567b218376f79d9415\"}";

//        TODO: change client on spring
        HttpClient client = HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(BODY_REQUEST))
                .uri(URI.create("http://interview.agileengine.com/auth"))
                .build();

        HttpResponse<?> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue((String) response.body(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        printResponse(map);
        return "Bearer " + map.get("token");
    }
}
