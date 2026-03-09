package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.checkinplace.request.EditCheckInPlaceRequest;
import com.sep490.g28.hvh.be.entity.CheckInPlace;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.Host;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.CheckInPlaceRepository;
import com.sep490.g28.hvh.be.repository.EventRepository;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import com.sep490.g28.hvh.be.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceCreateEventTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    VolunteerRepository volunteerRepository;


    @Mock
    StorageService storageService;

    @Mock
    CurrentUserProvider currentUserProvider;

    @Mock
    CheckInPlaceRepository checkInPlaceRepository;

    @InjectMocks
    EventServiceImpl eventService;

    Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setCreateBy(new Host());
    }

    private EditCheckInPlaceRequest addRequest(double lat, double lng) {
        EditCheckInPlaceRequest r = new EditCheckInPlaceRequest();
        r.setLat(lat);
        r.setLng(lng);
        r.setAccuracyMeters(10);
        r.setUpdateAction(EUpdateAction.ADD);
        return r;
    }

    private CheckInPlace place() {
        CheckInPlace p = new CheckInPlace();
        p.setId(UUID.randomUUID());
        return p;
    }

    private EditCheckInPlaceRequest removeReq(UUID id) {
        EditCheckInPlaceRequest r = new EditCheckInPlaceRequest();
        r.setUpdateAction(EUpdateAction.REMOVE);
        r.setCheckInPlaceId(id);
        return r;
    }

    private EditCheckInPlaceRequest editReq(UUID id) {
        EditCheckInPlaceRequest r = new EditCheckInPlaceRequest();
        r.setUpdateAction(EUpdateAction.EDIT);
        r.setCheckInPlaceId(id);
        r.setLat(21.1);
        r.setLng(105.8);
        r.setAccuracyMeters(10);
        return r;
    }

    // ==== addCheckInPlaces ===================================
    // ===== TC1 =====
    @Test
    void addCheckInPlaces_shouldSaveCorrectData() throws Exception {

        List<EditCheckInPlaceRequest> req = List.of(
                addRequest(21.1,105.8),
                addRequest(21.2,105.9)
        );

        Method method = EventServiceImpl.class
                .getDeclaredMethod("addCheckInPlaces", Event.class, List.class, int.class);
        method.setAccessible(true);

        method.invoke(eventService, event, req, 10);

        ArgumentCaptor<List<CheckInPlace>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(checkInPlaceRepository).saveAll(captor.capture());

        List<CheckInPlace> saved = captor.getValue();

        assertEquals(2, saved.size());
        assertEquals(event, saved.get(0).getEvent());
        assertEquals(10d, saved.get(0).getAccuracyMeters());
    }

    // ===== TC2 =====
    @Test
    void addCheckInPlaces_shouldRespectLimit() throws Exception {

        List<EditCheckInPlaceRequest> req = List.of(
                addRequest(1,1),
                addRequest(2,2),
                addRequest(3,3)
        );

        Method method = EventServiceImpl.class
                .getDeclaredMethod("addCheckInPlaces", Event.class, List.class, int.class);
        method.setAccessible(true);

        method.invoke(eventService, event, req, 2);

        ArgumentCaptor<List<CheckInPlace>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(checkInPlaceRepository).saveAll(captor.capture());

        assertEquals(2, captor.getValue().size());
    }

    // ===== TC3 =====
    @Test
    void addCheckInPlaces_noAddAction_shouldSaveEmpty() throws Exception {

        EditCheckInPlaceRequest r = new EditCheckInPlaceRequest();
        r.setUpdateAction(EUpdateAction.EDIT);

        List<EditCheckInPlaceRequest> req = List.of(r);

        Method method = EventServiceImpl.class
                .getDeclaredMethod("addCheckInPlaces", Event.class, List.class, int.class);
        method.setAccessible(true);

        method.invoke(eventService, event, req, 5);

        verify(checkInPlaceRepository).saveAll(Collections.emptyList());
    }

    // ==== updateCheckInPlaces ===================================
    // ===== TC1 =====
    @Test
    void updateCheckInPlaces_nullRequest_shouldDoNothing() throws Exception {

        Method m = EventServiceImpl.class.getDeclaredMethod(
                "updateCheckInPlaces",
                Event.class,
                List.class
        );
        m.setAccessible(true);
        m.invoke(eventService, event, null);

        verifyNoInteractions(checkInPlaceRepository);
    }

    // ===== TC2 =====
    @Test
    void updateCheckInPlaces_invalidAmount_shouldThrow() {

        when(checkInPlaceRepository.findByEventId(any()))
                .thenReturn(List.of());

        List<EditCheckInPlaceRequest> req = List.of(removeReq(UUID.randomUUID()));

        assertThrows(
                InvocationTargetException.class,
                () -> {
                    Method m = EventServiceImpl.class.getDeclaredMethod(
                            "updateCheckInPlaces",
                            Event.class,
                            List.class
                    );
                    m.setAccessible(true);
                    m.invoke(eventService, event, req);
                }
        );
    }

    // ===== TC4 REMOVE =====
    @Test
    void updateCheckInPlaces_remove_shouldDelete() throws Exception {

        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setCheckInPlaces(new ArrayList<>());

        CheckInPlace p1 = place();
        CheckInPlace p2 = place();

        event.getCheckInPlaces().add(p1);
        event.getCheckInPlaces().add(p2);

        when(checkInPlaceRepository.findByEventId(event.getId()))
                .thenReturn(List.of(p1, p2));

        List<EditCheckInPlaceRequest> req = List.of(removeReq(p1.getId()));

        Method m = EventServiceImpl.class.getDeclaredMethod(
                "updateCheckInPlaces",
                Event.class,
                List.class
        );
        m.setAccessible(true);
        m.invoke(eventService, event, req);

        verify(checkInPlaceRepository)
                .deleteAllById(Set.of(p1.getId()));

        assertEquals(1, event.getCheckInPlaces().size());
    }

    // ===== TC5 EDIT =====
    @Test
    void updateCheckInPlaces_edit_shouldUpdate() throws Exception {

        CheckInPlace p = place();

        when(checkInPlaceRepository.findByEventId(event.getId()))
                .thenReturn(List.of(p));

        List<EditCheckInPlaceRequest> req = List.of(editReq(p.getId()));

        Method m = EventServiceImpl.class.getDeclaredMethod(
                "updateCheckInPlaces",
                Event.class,
                List.class
        );
        m.setAccessible(true);
        m.invoke(eventService, event, req);

        verify(checkInPlaceRepository).saveAll(any());
    }

    // ===== TC6 ADD =====
    @Test
    void updateCheckInPlaces_add_shouldCallAddMethod() throws Exception {

        when(checkInPlaceRepository.findByEventId(event.getId()))
                .thenReturn(List.of());

        EditCheckInPlaceRequest r = new EditCheckInPlaceRequest();
        r.setUpdateAction(EUpdateAction.ADD);
        r.setLat(21.0);
        r.setLng(105.0);
        r.setAccuracyMeters(10);

        List<EditCheckInPlaceRequest> req = List.of(r);

        Method m = EventServiceImpl.class.getDeclaredMethod(
                "updateCheckInPlaces",
                Event.class,
                List.class
        );
        m.setAccessible(true);
        m.invoke(eventService, event, req);

        verify(checkInPlaceRepository).saveAll(any());
    }

    // ===== TC7 REMOVE but violation in the amount of place =====

    @Test
    void updateCheckInPlaces_removeAll_shouldThrow() {

        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setCheckInPlaces(new ArrayList<>());

        CheckInPlace p = place();
        event.getCheckInPlaces().add(p);

        when(checkInPlaceRepository.findByEventId(event.getId()))
                .thenReturn(List.of(p));

        List<EditCheckInPlaceRequest> req = List.of(removeReq(p.getId()));

        assertThrows(AppException.class, () -> {
            ReflectionTestUtils.invokeMethod(
                    eventService,
                    "updateCheckInPlaces",
                    event,
                    req
            );
        });
    }

}
