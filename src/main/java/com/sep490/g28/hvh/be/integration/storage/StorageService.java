package com.sep490.g28.hvh.be.integration.storage;

import java.util.concurrent.CompletableFuture;

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
     * @return signed upload URL
     */
    String getUploadUrl(String path);

    /**
     * Generate a signed upload URL for client-side file upload in async manner.
     *
     * @param path             file path inside storage bucket
     * @return signed upload URL
     */
    CompletableFuture<String> getUploadUrlAsync(String path);
}
