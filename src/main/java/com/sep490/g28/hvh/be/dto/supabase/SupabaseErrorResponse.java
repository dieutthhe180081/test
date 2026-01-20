package com.sep490.g28.hvh.be.dto.supabase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Define the errorResponse structure of Supabase
 */
@Getter
@Setter
@NoArgsConstructor
public class SupabaseErrorResponse {
    private int code; //http status
    @JsonProperty("error_code")
    private String errorCode;
    private String msg;
}
