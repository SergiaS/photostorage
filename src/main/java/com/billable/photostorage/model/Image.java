package com.billable.photostorage.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {

    protected String id;

    @JsonProperty("cropped_picture")
    protected String croppedPicture;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCroppedPicture() {
        return croppedPicture;
    }

    public void setCroppedPicture(String croppedPicture) {
        this.croppedPicture = croppedPicture;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", croppedPicture='" + croppedPicture + '\'' +
                '}';
    }
}
