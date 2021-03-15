package com.billable.photostorage.repository;

import com.billable.photostorage.model.Image;
import com.billable.photostorage.model.ImageDetails;
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
    private final Map<String, ImageDetails> inMemoryRepository;
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

    public Map<String, ImageDetails> getInMemoryRepository() {
        return inMemoryRepository;
    }

    @Override
    public ImageDetails add(ImageDetails imageDetails) {
        inMemoryRepository.putIfAbsent(imageDetails.getId(), imageDetails);
        return imageDetails;
    }

    @Override
    public ImageDetails get(String id) {
        return inMemoryRepository.get(id);
    }

    public Page getPageWithImages(int pageId) {
        String stringResponseEntity = null;
        try {
            // if token has expired, getForObject returns UNAUTHORIZED and null
            // than interceptor of Spring restTemplate will refresh token and try again...
            while (stringResponseEntity == null) {
                stringResponseEntity = restTemplate.getForObject(ENDPOINTS.get("pageNumber") + pageId, String.class);
            }
        } catch (Exception e) {
            // if HttpClientErrorException$Unauthorized - 401 Unauthorized: [{"status":"Unauthorized"}]
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server or your request!");
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
    public ImageDetails getImageDetails(String id) {
        String detailsResponse = null;
        try {
            while (detailsResponse == null) {
                detailsResponse = restTemplate.getForObject(ENDPOINTS.get("details") + id, String.class);
            }
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "There is some problem with server or your request!");
        }

        ImageDetails responseImage = null;
        try {
            responseImage = new ObjectMapper().readValue(detailsResponse, ImageDetails.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return responseImage;
    }

    public List<Image> search(String tag) {
        List<Image> taggedImages = new ArrayList<>();
        if (inMemoryRepository.size() == 0) {
            boolean hasMorePage = true;
            int pageNumber = 1;
            while (hasMorePage) {
                Page page = getPageWithImages(pageNumber++);
                for (Image cutImage : page.getPictures()) {
                    String id = cutImage.getId();
                    ImageDetails imageDetailsFromAPI = getImageDetails(id);

                    // put all images to inmemory repository
                    inMemoryRepository.put(id, imageDetailsFromAPI);

                    if (isImageHasTag(imageDetailsFromAPI, tag)) {
                        System.out.println("Added from API: " + imageDetailsFromAPI + "\n");
                        taggedImages.add(imageDetailsFromAPI);
                    }
                }
                hasMorePage = page.isHasMore();
            }
        } else {
            for (Map.Entry<String, ImageDetails> entry : inMemoryRepository.entrySet()) {
                ImageDetails imageDetailsFromRepository = entry.getValue();
                if (isImageHasTag(imageDetailsFromRepository, tag)) {
                    System.out.println("Added from REPO: " + imageDetailsFromRepository + "\n");
                    taggedImages.add(imageDetailsFromRepository);
                }
            }
        }
        System.out.println("Founded " + taggedImages.size() + " images.");
        return taggedImages;
    }

    public boolean isImageHasTag(ImageDetails imageDetails, String tag) {
        // compare needed image properties to searched tag
        String authorField = imageDetails.getAuthor() == null ? "" : imageDetails.getAuthor();
        String cameraField = imageDetails.getCamera() == null ? "" : imageDetails.getCamera();
        String tagsField = imageDetails.getTags() == null ? "" : imageDetails.getTags();
        return authorField.contains(tag) || cameraField.contains(tag) || tagsField.contains("#" + tag);
    }
}
