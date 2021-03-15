package com.billable.photostorage.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class AppConfig {

    private String token;

    @Autowired
    private RestTemplate restTemplate;

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
                System.out.println("   Token @   " + token);
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);

                ClientHttpResponse response = execution.execute(request, body);
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    token = getNewToken();
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);
                    System.out.println("   Token @@   " + token);
                    return execution.execute(request, body);
                }
                System.out.println("   Token @@@   " + token);
                return response;
            }
        });
    }

    private String getNewToken() {
        final String BODY_REQUEST = "{\"apiKey\":\"23567b218376f79d9415\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(BODY_REQUEST, headers);

        String result = restTemplate.postForObject("http://interview.agileengine.com/auth", request, String.class);

        JsonNode root = null;
        try {
            root = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "Bearer " + root.path("token").asText();
    }
}
