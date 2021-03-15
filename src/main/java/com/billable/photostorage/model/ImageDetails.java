package com.billable.photostorage.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageDetails extends Image {

    private String author;

    private String camera;

    private String tags;

    @JsonProperty("full_picture")
    private String fullPicture;


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

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    @Override
    public String toString() {
        return "ImageDetails{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", camera='" + camera + '\'' +
                ", tags='" + tags + '\'' +
                ", croppedPicture='" + croppedPicture + '\'' +
                ", fullPicture='" + fullPicture + '\'' +
                '}';
    }
}
