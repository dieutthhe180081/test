package com.sep490.g28.hvh.be.integration.storage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StoragePathGenerator {

    public String cidFront(UUID userId, String mimeType){
        return  "/user/" + userId + "/cid-front" + extFromMime(mimeType);
    }

    public String cidBack(UUID userId, String mimeType){
        return  "/user/" + userId + "/cid-back" + extFromMime(mimeType);
    }

    public String cidHolding(UUID userId, String mimeType){
        return  "/user/" + userId + "/cid-holding" + extFromMime(mimeType);
    }

    private static String extFromMime(String mimeType) {
        MediaType mediaType = MediaType.parseMediaType(mimeType);

        return switch (mediaType.getSubtype()) {
            case "jpeg", "jpg" -> "jpg";
            case "png" -> "png";
            case "webp" -> "webp";
            //todo bat exception cho cho nay
            default -> throw new IllegalArgumentException("UNSUPPORTED_MIME");
        };
    }
}
