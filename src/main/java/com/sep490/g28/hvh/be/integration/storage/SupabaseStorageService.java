package com.sep490.g28.hvh.be.integration.storage;

import com.sep490.g28.hvh.be.config.SupabaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class SupabaseStorageService implements StorageService {
    private final RestTemplate restTemplate;
    private final SupabaseConfig supabaseConfig;

    public SupabaseStorageService(
            @Qualifier("supabaseRestTemplate") RestTemplate restTemplate,
            SupabaseConfig config
    ) {
        this.restTemplate = restTemplate;
        this.supabaseConfig = config;
    }

    //user
    //user/{user-id}/{tên loại file}

    //event - lưu thông tin của event
    //{event/{event-id}/

    //organization - lưu thông tin của organization

    public String upload(MultipartFile file, String path) throws IOException {
        String url = supabaseConfig.getUrl()
                + "/storage/v1/object/"
                + supabaseConfig.getBucket()
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
     * get the signed url, which the client could use to view the content in the path
     * @param path the path of the file in the bucket
     * @return a String of signed url, expire in 1 hour
     */
    public String createSignedUrl(String path) {
        String url = supabaseConfig.getUrl()
                + "/storage/v1/object/sign/"
                + supabaseConfig.getBucket()
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
     * get upload url, clients could use the sign url to upload file from their device
     * @param path the location of the file in the bucket
     * @param expiresInSeconds expiration time of the upload url (in seconds)
     * @return a String of signed upload url
     */
    @Override
    public String getUploadUrl(String path, int expiresInSeconds) {
        String url = supabaseConfig.getUrl()
                + "/storage/v1/object/upload/sign/"
                + supabaseConfig.getBucket()
                + "/" + path;

        //expired in 1 hour
        Map<String, Object> body = Map.of(
                "expiresIn", expiresInSeconds
        );

        ResponseEntity<Map> res = restTemplate.postForEntity(url, body, Map.class);

        log.info("Get upload url successfully: {}", res.getBody());
        //todo xem lai cho nay, de gay loi
        return (String) res.getBody().get("url");
    }
}
