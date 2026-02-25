package com.sep490.g28.hvh.be.notification.controller;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
//import com.sep490.g28.hvh.be.notification.dto.RegisterTokenRequest;
import com.sep490.g28.hvh.be.notification.dto.SendPushRequest;
//import com.sep490.g28.hvh.be.notification.entity.NotificationToken;
//import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for notification
 */
@RestController
@RequestMapping("/test/notification")
@RequiredArgsConstructor
public class NotificationController {

//    private final NotificationTokenRepository repo;
    private final CurrentUserProvider currentUser;
    private final NotificationService notificationService;

//    public NotificationController(
//            NotificationTokenRepository repo,
//            CurrentUserProvider currentUser
//    ) {
//        this.repo = repo;
//        this.currentUser = currentUser;
//    }

//    @PostMapping("/token")
//    public void register(@RequestBody RegisterTokenRequest req) {
//
//        repo.findByToken(req.getToken())
//                .ifPresentOrElse(
//                        //exist in db
//                        t -> {
//                            t.setActive(true);
//                            t.setDeviceId(req.getDeviceId());
//                        },
//                        //not exist in db
//                        () -> {
//                            NotificationToken t = new NotificationToken();
//                            t.setUserId(currentUser.getId());
//                            t.setToken(req.getToken());
//                            t.setPlatform(req.getPlatform());
//                            t.setDeviceId(req.getDeviceId());
//                            repo.save(t);
//                        }
//                );
//    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody SendPushRequest request) {
        notificationService.sendToToken(
                request.getToken(),
                request.getTitle(),
                request.getBody()
        );
        return ResponseEntity.ok().build();
    }
}