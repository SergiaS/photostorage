package com.billable.photostorage.rest;

import com.billable.photostorage.model.Image;
import com.billable.photostorage.model.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
            "pageNumber", "http://interview.agileengine.com/images?page=",    // GET  /images?page=2
            "details", "http://interview.agileengine.com/images/",  // GET  /images/${id}
            "search", "http://interview.agileengine.com/search/"    // GET  /search/${searchTerm}
    );

    private final RestTemplate restTemplate;

    public ImageRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/images", params = "page")
    public Page getPageNumber(@RequestParam("page") int pageId) {
        log.info(" >>> GET /images?page={}", pageId);

        String stringResponseEntity = null;
        try {
            // if token has expired, getForObject returns UNAUTHORIZED and null
            // than interceptor of Spring restTemplate will refresh token and try again...
            while (stringResponseEntity == null) {
                stringResponseEntity = restTemplate.getForObject(ENDPOINTS.get("pageNumber") + pageId, String.class);
            }
        } catch (Exception e) {
            // if HttpClientErrorException$Unauthorized - 401 Unauthorized: [{"status":"Unauthorized"}]
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server");
        }

        Page page = null;
        try {
            page = new ObjectMapper().readValue(stringResponseEntity, Page.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info(" > Page object {}", page);
        return page;
    }

    @GetMapping("/images/{id}")
    public Image getImageDetails(@PathVariable String id) {
        log.info(" >>> GET /images/{}", id);

        String detailsResponse = null;
        try{
            while (detailsResponse == null) {
                detailsResponse = restTemplate.getForObject(ENDPOINTS.get("details") + id, String.class);
            }
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server");
        }

        Image responseImage = null;
        try {
            responseImage = new ObjectMapper().readValue(detailsResponse, Image.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info(" > Image object {}", responseImage);
        return responseImage;
    }

    @GetMapping("/search/{tag}")
    public String search(@PathVariable String tag) {
        log.info(" >>> GET /search/{}", tag);
        return "searching for " + tag;
    }

    public <T> void printResponse(Map<T, Object> responseMap) {
        responseMap.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}
