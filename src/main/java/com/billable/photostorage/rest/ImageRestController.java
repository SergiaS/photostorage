package com.billable.photostorage.rest;

import com.billable.photostorage.model.Image;
import com.billable.photostorage.model.Page;
import com.billable.photostorage.repository.ImageRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImageRestController {
    private static final Logger log = LoggerFactory.getLogger(ImageRestController.class);

    private final ImageRepositoryImpl repository;

    public ImageRestController(ImageRepositoryImpl repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/images", params = "page")
    public Page getPageNumber(@RequestParam("page") int pageId) {
        log.info(" >>> GET /images?page={}", pageId);
        return repository.getPageWithImages(pageId);
    }

    @GetMapping("/images/{id}")
    public Image getImageDetails(@PathVariable String id) {
        log.info(" >>> GET /images/{}", id);
        return repository.getImageDetails(id);
    }

    @GetMapping("/search/{tag}")
    public List<Image> search(@PathVariable String tag) {
        log.info(" >>> GET /search/{}", tag);
        return repository.search(tag);
    }
}
