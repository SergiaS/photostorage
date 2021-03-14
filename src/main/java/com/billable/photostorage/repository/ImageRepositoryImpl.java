package com.billable.photostorage.repository;

import com.billable.photostorage.model.Image;
import com.billable.photostorage.model.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ImageRepositoryImpl implements ImageRepository {
    private final Map<String, Image> inMemoryRepository;
    private final Map<String, String> ENDPOINTS;

    @Autowired
    private RestTemplate restTemplate;

    public ImageRepositoryImpl() {
//        this.restTemplate = restTemplate;
        inMemoryRepository = new HashMap<>();
        ENDPOINTS = Map.of(
                "auth", "http://interview.agileengine.com/auth",                 // POST /auth
                "images", "http://interview.agileengine.com/images",             // GET  /images?page=2
                "pageNumber", "http://interview.agileengine.com/images?page=",   // GET  /images?page=2
                "details", "http://interview.agileengine.com/images/",           // GET  /images/${id}
                "search", "http://interview.agileengine.com/search/"             // GET  /search/${searchTerm}
        );
    }

    public Map<String, Image> getInMemoryRepository() {
        return inMemoryRepository;
    }

    @Override
    public Image add(Image image) {
        inMemoryRepository.putIfAbsent(image.getId(), image);
        return image;
    }

    @Override
    public Image get(String id) {
        return inMemoryRepository.get(id);
    }

    public Page getPageNumber(int pageId) {
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
        return page;
    }

    public Image getImageDetails(String id) {
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
        return responseImage;
    }

    public List<Image> search(String tag) {
        return null;
    }
}
