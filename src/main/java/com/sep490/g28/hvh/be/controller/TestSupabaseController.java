package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.auth.SupabaseAuthService;
import com.sep490.g28.hvh.be.storage.SupabaseStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth/test-spb")
//@RequiredArgsConstructor
public class TestSupabaseController {

    private final SupabaseAuthService authService;
    private final SupabaseStorageService storageService;

    public TestSupabaseController(SupabaseAuthService authService, SupabaseStorageService storageService) {
        this.authService = authService;
        this.storageService = storageService;
    }

    @PostMapping
    public void create(@RequestParam String email,
                       @RequestParam String password) {
        authService.createUser(email, password);
    }

//    @PostMapping("/upload")
//    public String upload(@RequestParam MultipartFile file) throws IOException {
//        String path = UUID.randomUUID() + "-" + file.getOriginalFilename();
//        storageService.upload(file, path);
//        return path;
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
                Map.of("signedUrl", signedUrl)
        );
    }

    // test: lấy signed upload url
    @PostMapping("/signed-upload-url")
    public ResponseEntity<Map<String, String>> getSignedUploadUrl( ) {

        String signedUrl = storageService.createSignedUploadUrl("test/signeduploadurl", 300);

        return ResponseEntity.ok(
                Map.of("signedUploadUrl", signedUrl)
        );
    }
}
