package com.sep490.g28.hvh.be.exception;

import org.springframework.http.HttpMethod;

import java.net.URI;

public class SupabaseException extends RuntimeException {
    private final int status;
    private final String rawBody;
    private final URI url;
    private final HttpMethod method;

    public SupabaseException(
            int status,
            String rawBody,
            URI url,
            HttpMethod method
    ) {
        super("Supabase API error " + status);
        this.status = status;
        this.rawBody = rawBody;
        this.url = url;
        this.method = method;
    }

    public int getStatus() { return status; }
    public String getRawBody() { return rawBody; }
    public URI getUrl() { return url; }
    public HttpMethod getMethod() { return method; }
}
