package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.integration.authServer.AuthService;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/test-sb")
@RequiredArgsConstructor
public class TestSupabaseController {

    private final AuthService authService;
    private final StorageService storageService;

//    public TestSupabaseController(SupabaseAuthService authService, SupabaseStorageService storageService) {
//        this.authService = authService;
//        this.storageService = storageService;
//    }

    // test upload file
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("path") String path
    ){

        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse("");

        String savePath = path + ext;

        storageService.upload(file, savePath);

        return ResponseEntity.ok(
                "upload sucess "
        );

    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestParam("path") String path
    ){

        storageService.deleteFile(path);

        return ResponseEntity.ok(
                "delete sucess "
        );
    }

    @PostMapping("/delete-multiple")
    public ResponseEntity<String> deleteMultiple(
            @RequestParam("path") String path1,
            @RequestParam("path2") String path2
    ){

        CompletableFuture<Void> delete1 = storageService.deleteFileAsync(path1);
        CompletableFuture<Void> delete2 = storageService.deleteFileAsync(path2);

        try {
            CompletableFuture.allOf(delete1, delete2).join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }

        return ResponseEntity.ok(
                "delete multiple sucess "
        );
    }

    // test create signed url
//    @GetMapping("/signed-url")
//    public ResponseEntity<Map<String, String>> getSignedUrl(
//            @RequestParam("path") String path
//    ) {
//        String signedUrl = storageService.createSignedUrl(path);
//
//        return ResponseEntity.ok(
//                Map.of("signedURL", signedUrl)
//        );
//    }

    private final UserRepository userRepository;
    @GetMapping("/create-account")
    public ResponseEntity<String> testCreateVolAccount(@RequestParam String email) {
        if (userRepository.existsByEmail(email)){
        return ResponseEntity.ok("oh oh emddaxd dc su dung");

        }
        authService.createAccount(ERole.VOL, email, "12345678", "0123456789");
        return ResponseEntity.ok("OK");
    }

}
