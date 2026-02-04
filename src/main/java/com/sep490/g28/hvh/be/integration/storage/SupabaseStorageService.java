package com.sep490.g28.hvh.be.integration.storage;

import com.sep490.g28.hvh.be.config.SupabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Supabase-based implementation of {@link StorageService}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Upload files directly to Supabase Storage (server-side)</li>
 *   <li>Generate signed URLs for upload/view</li>
 * </ul>
 *
 * <p>Notes:</p>
 * <ul>
 *   <li>Uses Supabase service role key</li>
 *   <li>All paths are scoped to a single bucket</li>
 * </ul>
 */
@Slf4j
@Service
public class SupabaseStorageService implements StorageService {
    private final RestTemplate restTemplate;
    private final SupabaseProperties supabaseProperties;

    public SupabaseStorageService(
            @Qualifier("supabaseRestTemplate") RestTemplate restTemplate,
            SupabaseProperties config
    ) {
        this.restTemplate = restTemplate;
        this.supabaseProperties = config;
    }

    /**
     * Upload file directly to Supabase Storage.
     * <p>
     * Intended for server-side upload only.
     * </p>
     *
     * @param file multipart file
     * @param path destination path in bucket
     * @return stored path
     * @throws IOException if file stream cannot be read
     */
    public String upload(MultipartFile file, String path) throws IOException {
        String url = supabaseProperties.getUrl()
                + "/storage/v1/object/"
                + supabaseProperties.getBucket()
                + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.getSize());

        InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
            @Override
            public long contentLength() throws IOException {
                return file.getSize();
            }
        };

        HttpEntity<Resource> request =
                new HttpEntity<>(resource, headers);

        ResponseEntity<Void> res = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Upload failed");
        }

        return path;

    }

    /**
     * Create a signed URL for viewing/downloading a file.
     *
     * @param path file path in bucket
     * @return signed URL (default 1 hour expiry)
     */
    public String createSignedUrl(String path) {
        String url = supabaseProperties.getUrl()
                + "/storage/v1/object/sign/"
                + supabaseProperties.getBucket()
                + "/" + path;
        //expired in 1 hour
        Map<String, Object> body = Map.of(
                "expiresIn", 3600
        );

        try {
            ResponseEntity<Map> res =
                    restTemplate.postForEntity(url, body, Map.class);

            return (String) res.getBody().get("signedURL");

        } catch (HttpClientErrorException e) {

            // status code
            int status = e.getStatusCode().value();

            // raw body: {"statusCode":"404","error":"not_found","message":"Object not found"}
            String responseBody = e.getResponseBodyAsString();
            //todo xu li exception
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> err =
//                    mapper.readValue(e.getResponseBodyAsString(), Map.class);
//
//            String message = (String) err.get("message");
            throw new RuntimeException(
                    "Supabase error " + status + ": " + responseBody
            );
        }
    }

    /**
     * Generate signed upload URL for client-side upload.
     * By default, the url would expire in 10 minutes
     * <p>
     * Client uploads file directly to Supabase Storage.
     * Backend does not handle file content.
     * </p>
     *
     * @param path             file path in bucket
     * @return signed upload URL
     */
    @Override
    public String getUploadUrl(String path) {
        String url = supabaseProperties.getUrl()
                + "/storage/v1/object/upload/sign/"
                + supabaseProperties.getBucket()
                + "/" + path;

        //expired in 1 hour
        Map<String, Object> body = Map.of(
                "expiresIn", 600
        );

        ResponseEntity<Map> res = restTemplate.postForEntity(url, body, Map.class);

        log.info("Get upload url successfully: {}", res.getBody());
        //todo xem lai cho nay, de gay loi
        return (String) res.getBody().get("url");
    }

    @Override
    @Async
    public CompletableFuture<String> getUploadUrlAsync(String path) {
        return CompletableFuture.completedFuture(getUploadUrl(path));
    }
}
