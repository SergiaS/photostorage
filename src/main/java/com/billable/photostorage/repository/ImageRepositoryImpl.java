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

import java.util.ArrayList;
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

    // TODO: try to turn on deserializable (off NULL properties) for mapper, and remove @ from model
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

        // get page - iterate pictures and collect all id, if page property hasMore=true, do it again

        boolean hasMorePage = true;
        int pageNumber = 1;
        List<Image> taggedImages = new ArrayList<>();

        while (hasMorePage) {
            Page page = getPageNumber(pageNumber++);
            for (Image picture : page.getPictures()) {
                String id = picture.getId();
                Image imageDetails = getImageDetails(id);

                System.out.println(imageDetails);
                System.out.println(" === ");

                // put all images to inmemory repo
                inMemoryRepository.putIfAbsent(id, imageDetails);
                // compare needed image properties to searched tag
                if (imageDetails.getAuthor() != null && imageDetails.getCamera() != null && imageDetails.getTags() != null) {
                    if (imageDetails.getAuthor().contains(tag) || imageDetails.getCamera().contains(tag) || imageDetails.getTags().contains("#"+tag)) {
                        taggedImages.add(imageDetails);
                    }
                }
            }
            hasMorePage = page.isHasMore();
        }
        System.out.println("Founded " + taggedImages.size() + " images.");
        return taggedImages;
    }
}
