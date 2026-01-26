package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.integration.authServer.SupabaseAuthService;
import com.sep490.g28.hvh.be.integration.storage.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test-sb")
@RequiredArgsConstructor
public class TestSupabaseController {

    private final SupabaseAuthService authService;
    private final SupabaseStorageService storageService;

//    public TestSupabaseController(SupabaseAuthService authService, SupabaseStorageService storageService) {
//        this.authService = authService;
//        this.storageService = storageService;
//    }

    // test upload file
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestPart("file") MultipartFile file
    ) throws IOException {


        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse("");

        String path = "test/file" + ext;

        String savedPath = storageService.upload(file, path);

        return ResponseEntity.ok(
                Map.of("path", savedPath)
        );

    }

    // test create signed url
    @GetMapping("/signed-url")
    public ResponseEntity<Map<String, String>> getSignedUrl(
            @RequestParam("path") String path
    ) {
        String signedUrl = storageService.createSignedUrl(path);

        return ResponseEntity.ok(
                Map.of("signedURL", signedUrl)
        );
    }

    @GetMapping("/create-account")
    public ResponseEntity<String> createVolAccount() {
        authService.createAccount(ERole.VOL, "abc1@gmail.com", "12345678", "0123456789");
        return ResponseEntity.ok("OK");
    }

}
