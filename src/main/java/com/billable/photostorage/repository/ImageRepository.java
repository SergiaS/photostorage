package com.billable.photostorage.repository;

import com.billable.photostorage.model.ImageDetails;

public interface ImageRepository {

    ImageDetails add(ImageDetails image);

    ImageDetails get(String id);
}
