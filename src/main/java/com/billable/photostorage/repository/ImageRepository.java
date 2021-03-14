package com.billable.photostorage.repository;

import com.billable.photostorage.model.Image;

public interface ImageRepository {

    Image add(Image image);

    Image get(String id);
}
