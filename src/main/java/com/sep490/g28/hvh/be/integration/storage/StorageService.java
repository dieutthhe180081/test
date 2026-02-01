package com.sep490.g28.hvh.be.integration.storage;

/**
 * Abstraction for file storage operations.
 * <p>
 * Hides storage provider implementation (Supabase, S3, etc).
 * Used by application layer to generate upload URLs for clients.
 * </p>
 */
public interface StorageService {
    /**
     * Generate a signed upload URL for client-side file upload.
     *
     * @param path             file path inside storage bucket
     * @param expiresInSeconds expiration time in seconds
     * @return signed upload URL
     */
    String getUploadUrl(String path, int expiresInSeconds);
}
