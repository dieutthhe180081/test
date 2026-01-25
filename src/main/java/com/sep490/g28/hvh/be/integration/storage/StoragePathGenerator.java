package com.sep490.g28.hvh.be.integration.storage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StoragePathGenerator {

    public String cidFront(UUID verificationId, String mimeType){
        return  "/volunteer-verification/" + verificationId + "/cid-front" + extFromMime(mimeType);
    }

    public String cidBack(UUID verificationId, String mimeType){
        return  "/volunteer-verification/" + verificationId + "/cid-back" + extFromMime(mimeType);
    }

    public String cidHolding(UUID verificationId, String mimeType){
        return  "/volunteer-verification/" + verificationId + "/cid-holding" + extFromMime(mimeType);
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
