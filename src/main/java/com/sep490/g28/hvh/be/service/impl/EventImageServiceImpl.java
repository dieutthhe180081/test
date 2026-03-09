package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.eventimage.request.EditEventImageRequest;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.EventImage;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.EventImageRepository;
import com.sep490.g28.hvh.be.service.EventImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventImageServiceImpl implements EventImageService {

    private final EventImageRepository eventImageRepository;

    private final StoragePathGenerator storagePathGenerator;

    private final StorageService storageService;

    private static final int MAX_IMAGES = 5;


    public List<String> addEventImages(Event event, List<EditEventImageRequest> addImages) {
        //check request: valid add image amount?
        int countAddImages = (int) addImages.stream()
                .filter(r -> r.getUpdateAction() == EUpdateAction.ADD)
                .count();
        if (countAddImages > MAX_IMAGES) {
            throw new AppException(EventErrorCode.INVALID_IMAGES_AMOUNT);
        }
        return addEventImages(event, addImages, MAX_IMAGES);
    }

    private List<String> addEventImages(Event event, List<EditEventImageRequest> addImages, int remainingSlot) {
        List<EventImage> toAdd = new ArrayList<>();
        List<CompletableFuture<String>> urlFutures = new ArrayList<>();
        //go through add list to create object EventImage and get upload url
        addImages.stream()
                .filter(r -> r.getUpdateAction() == EUpdateAction.ADD)
                .limit(remainingSlot)
                .forEach(r -> {
                    UUID imageId = UUID.randomUUID();
                    String path = storagePathGenerator.eventImage(event.getId(), imageId, r.getFileExtension());

                    EventImage image = new EventImage();
                    image.setId(imageId);
                    image.setEvent(event);
                    image.setImagePath(path);

                    toAdd.add(image);
                    urlFutures.add(storageService.getUploadUrlAsync(path));
                });

        eventImageRepository.saveAll(toAdd);

        CompletableFuture.allOf(urlFutures.toArray(new CompletableFuture[0])).join();

        return urlFutures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public List<String> updateEventImages(Event event, List<EditEventImageRequest> reqImages) {
        //request image empty, don't need to update
        if (reqImages == null || reqImages.isEmpty()) return Collections.emptyList();

        List<EventImage> existingImages = event.getImages();

        //categorize update image request base on action
        List<EditEventImageRequest> removes = new ArrayList<>();
        List<EditEventImageRequest> adds = new ArrayList<>();

        for (EditEventImageRequest r : reqImages) {
            if (r.getUpdateAction().equals(EUpdateAction.REMOVE)) {
                removes.add(r);
            } else
                adds.add(r);
        }

        //check the amount
        int existingCount = existingImages.size();
        int removeCount = removes.size();
        int addCount = adds.size();

        //the amount of check in place after update
        int finalCount = existingCount - removeCount + addCount;
        if (finalCount > MAX_IMAGES) {
            throw new AppException(EventErrorCode.INVALID_IMAGES_AMOUNT);
        }

        //remove
        if (!removes.isEmpty()) {

            Set<UUID> removeIds = removes.stream()
                    .map(EditEventImageRequest::getImageId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // get path to delete
            List<String> pathsToDelete = existingImages.stream()
                    .filter(img -> removeIds.contains(img.getId()))
                    .map(EventImage::getImagePath)
                    .toList();

            // delete DB
            eventImageRepository.deleteAllById(removeIds);

            // remove from collection in Event
            event.getImages().removeIf(img -> removeIds.contains(img.getId()));

            // delete file async
            List<CompletableFuture<Void>> futures = pathsToDelete.stream()
                    .map(storageService::deleteFileAsync)
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }

        //add
        if (!adds.isEmpty()) {
            int remainingSlots = MAX_IMAGES - (existingCount - removeCount);

            return addEventImages(event, adds, remainingSlots);
        }

        return Collections.emptyList();
    }
}
