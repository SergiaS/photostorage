package com.billable.photostorage.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ImageRestController {
    private static final Logger log = LoggerFactory.getLogger(ImageRestController.class);

    private final Map<String, String> ENDPOINTS = Map.of(
            "auth", "http://interview.agileengine.com/auth",        // POST /auth
            "images", "http://interview.agileengine.com/images",    // GET  /images?page=2
            "details", "http://interview.agileengine.com/images/",  // GET  /images/${id}
            "search", "http://interview.agileengine.com/search/"    // GET  /search/${searchTerm}
    );

    private final RestTemplate restTemplate;

    public ImageRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public void index() {
        log.info(" GET / ");
        System.out.println("    @@@     IT WORKS    @@@");
    }

    @GetMapping(value = "/images")
    public void getPage() {
        log.info(" >>> GET /images");

        String stringResponseEntity = null;
        try {
            // if token has expired, getForObject returns UNAUTHORIZED and null
            // than interceptor of Spring restTemplate will refresh token and try again...
            while (stringResponseEntity == null) {
                stringResponseEntity = restTemplate.getForObject(ENDPOINTS.get("images"), String.class);
            }
        } catch (Exception e) {
            // if HttpClientErrorException$Unauthorized - 401 Unauthorized: [{"status":"Unauthorized"}]
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server");
        }

        Map<String, Object> responseMap = null;
        try {
            responseMap = new ObjectMapper().readValue(stringResponseEntity, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        printResponse(responseMap);
    }

    @GetMapping("/images/{id}")
    public void getDetails(@PathVariable String id) {
        log.info(" >>> GET /images/{}", id);
        String detailsResponse = null;
        try{
            while (detailsResponse == null) {
                detailsResponse = restTemplate.getForObject(ENDPOINTS.get("details") + id, String.class);
            }
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server");
        }

        Map<String, Object> responseMap = null;
        try {
            responseMap = new ObjectMapper().readValue(detailsResponse, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        printResponse(responseMap);
    }

    @GetMapping("/search/{tag}")
    public void search(@PathVariable String tag) {
        log.info(" >>> GET /search/{}", tag);

    }

    public <T> void printResponse(Map<T, Object> responseMap) {
        responseMap.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}
