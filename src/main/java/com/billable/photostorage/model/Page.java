package com.billable.photostorage.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Page {

    private List<Image> pictures;
    @JsonProperty("page")
    private int pageNumber;
    private int pageCount;
    private boolean hasMore;


    public List<Image> getPictures() {
        return pictures;
    }

    public void setPictures(List<Image> pictures) {
        this.pictures = pictures;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pictures=" + pictures +
                ", pageNumber=" + pageNumber +
                ", pageCount=" + pageCount +
                ", hasMore=" + hasMore +
                '}';
    }
}
