package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.eventimage.request.EditEventImageRequest;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.EventImage;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.EventImageRepository;
import com.sep490.g28.hvh.be.service.impl.EventImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventImageServiceTest {

    @Mock
    EventImageRepository eventImageRepository;

    @Mock
    StoragePathGenerator storagePathGenerator;

    @Mock
    StorageService storageService;

    @InjectMocks
    EventImageServiceImpl service;

    Event event;

    @BeforeEach
    void setup() {
        event = new Event();
        event.setId(UUID.randomUUID());
        event.setImages(new ArrayList<>());
    }

    private EditEventImageRequest addReq() {
        EditEventImageRequest r = new EditEventImageRequest();
        r.setUpdateAction(EUpdateAction.ADD);
        r.setFileExtension("jpg");
        return r;
    }

    private EditEventImageRequest removeReq(UUID id) {
        EditEventImageRequest r = new EditEventImageRequest();
        r.setUpdateAction(EUpdateAction.REMOVE);
        r.setImageId(id);
        return r;
    }

    private EventImage image(UUID id) {
        EventImage img = new EventImage();
        img.setId(id);
        img.setEvent(event);
        img.setImagePath("event/" + UUID.randomUUID() + "/image/" + id +".jpg");
        return img;
    }

    // ==== addEventImages ===================================
    // ===== TC01 add 1 image
    @Test
    void addEventImages_addOne_shouldReturnUrl() {

        EditEventImageRequest r = addReq();

        when(storagePathGenerator.eventImage(any(), any(), any()))
                .thenReturn("path/img.jpg");

        when(storageService.getUploadUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        List<String> urls = service.addEventImages(event, List.of(r));

        verify(eventImageRepository).saveAll(anyList());
        assertEquals(1, urls.size());
    }

    // ===== TC02 add multiple images within limit
    @Test
    void addEventImages_multiple_shouldReturnUrls() {

        List<EditEventImageRequest> req = List.of(
                addReq(), addReq(), addReq()
        );

        when(storagePathGenerator.eventImage(any(), any(), any()))
                .thenReturn("path/img.jpg");

        when(storageService.getUploadUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        List<String> urls = service.addEventImages(event, req);

        assertEquals(3, urls.size());
    }
    // ===== TC03 add > MAX_IMAGES
    @Test
    void addEventImages_exceedLimit_shouldOnlyAddFive() {

        List<EditEventImageRequest> req = Arrays.asList(
                addReq(), addReq(), addReq(),
                addReq(), addReq(), addReq(), addReq()
        );

        when(storagePathGenerator.eventImage(any(), any(), any()))
                .thenReturn("path/img.jpg");

        when(storageService.getUploadUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        List<String> urls = service.addEventImages(event, req);

        assertEquals(5, urls.size());
    }

    // ==== updateEventImages ===================================
    // ===== TC1    request null
    void updateEventImages_nullRequest_shouldReturnEmpty() {

        List<String> result = service.updateEventImages(event, null);

        assertTrue(result.isEmpty());
    }

    // ===== TC2    request empty
    @Test
    void updateEventImages_emptyRequest_shouldReturnEmpty() {

        List<String> result = service.updateEventImages(event, List.of());

        assertTrue(result.isEmpty());
    }

    // ===== TC3    remove existing image
    @Test
    void updateEventImages_removeExisting_shouldDelete() {

        UUID id = UUID.randomUUID();
        EventImage img = image(id);

        event.getImages().add(img);

        when(storageService.deleteFileAsync(any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        service.updateEventImages(event, List.of(removeReq(id)));

        verify(eventImageRepository).deleteAllById(Set.of(id));
        assertTrue(event.getImages().isEmpty());
    }

    // ===== TC3    remove imageId null
    @Test
    void updateEventImages_removeNullId_shouldIgnore() {

        EditEventImageRequest r = new EditEventImageRequest();
        r.setUpdateAction(EUpdateAction.REMOVE);

        List<String> result = service.updateEventImages(event, List.of(r));

        assertTrue(result.isEmpty());
    }

    // ===== TC4    remove id not in event
    @Test
    void updateEventImages_removeNotExisting_shouldNotCrash() {

        UUID id = UUID.randomUUID();

        service.updateEventImages(event, List.of(removeReq(id)));

        verify(eventImageRepository).deleteAllById(Set.of(id));
    }

    // ===== TC5    add images via update
    @Test
    void updateEventImages_add_shouldReturnUrl() {

        EditEventImageRequest r = addReq();

        when(storagePathGenerator.eventImage(any(), any(), any()))
                .thenReturn("path/img.jpg");

        when(storageService.getUploadUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        List<String> urls = service.updateEventImages(event, List.of(r));

        assertEquals(1, urls.size());
    }

    // ===== TC6    remove then add
    @Test
    void updateEventImages_removeThenAdd_shouldWork() {

        UUID id = UUID.randomUUID();
        event.getImages().add(image(id));

        when(storageService.deleteFileAsync(any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        when(storagePathGenerator.eventImage(any(), any(), any()))
                .thenReturn("path/img.jpg");

        when(storageService.getUploadUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        List<EditEventImageRequest> req = List.of(
                removeReq(id),
                addReq()
        );

        List<String> urls = service.updateEventImages(event, req);

        assertEquals(1, urls.size());
    }

    // ===== TC07 exceed max images
    @Test
    void updateEventImages_exceedMax_shouldThrow() {

        for (int i = 0; i < 5; i++) {
            event.getImages().add(image(UUID.randomUUID()));
        }

        assertThrows(AppException.class, () ->
                service.updateEventImages(event, List.of(addReq()))
        );
    }

    // TC13  reqImages == null
    @Test
    void updateEventImages_reqImagesNull_shouldReturnEmpty() {

        List<String> result = service.updateEventImages(event, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(eventImageRepository);
        verifyNoInteractions(storageService);
    }
}
