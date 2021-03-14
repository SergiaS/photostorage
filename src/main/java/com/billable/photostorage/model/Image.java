package com.billable.photostorage.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {

    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String author;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String camera;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tags;

    @JsonProperty("cropped_picture")
    private String croppedPicture;

    @JsonProperty("full_picture")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullPicture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCroppedPicture() {
        return croppedPicture;
    }

    public void setCroppedPicture(String croppedPicture) {
        this.croppedPicture = croppedPicture;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", camera='" + camera + '\'' +
                ", tags='" + tags + '\'' +
                ", croppedPicture='" + croppedPicture + '\'' +
                ", fullPicture='" + fullPicture + '\'' +
                '}';
    }
}
