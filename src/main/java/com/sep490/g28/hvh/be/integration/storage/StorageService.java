package com.sep490.g28.hvh.be.integration.storage;


public interface StorageService {
    String getUploadUrl(String path, int expiresInSeconds);
}
