package com.sep490.g28.hvh.be.mapper;

import com.sep490.g28.hvh.be.dto.supabase.SupabaseErrorResponse;
import com.sep490.g28.hvh.be.exception.AppCommonErrorCode;
import com.sep490.g28.hvh.be.exception.ErrorCode;
import com.sep490.g28.hvh.be.exception.SupabaseErrorCode;
import com.sep490.g28.hvh.be.exception.ValidationErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SupabaseErrorMapper {
    //map supabase error code to our error code
    private static final Map<String, ErrorCode> MAP = Map.ofEntries(
            // validation
            Map.entry("validation_failed", SupabaseErrorCode.VALIDATION_FAIL),
            Map.entry("unauthorized", SupabaseErrorCode.UNAUTHORIZED),
            Map.entry("rate_limit_exceeded", SupabaseErrorCode.RATE_LIMIT_EXCEEDED),
            Map.entry("internal_server_error", SupabaseErrorCode.INTERNAL_SERVER_ERROR)


//            Map.entry("bad_json", AppErrorCode.INVALID_INPUT),
//
//            // conflict
//            Map.entry("email_exists", AppErrorCode.USER_ALREADY_EXISTS),
//            Map.entry("phone_exists", AppErrorCode.USER_ALREADY_EXISTS),
//            Map.entry("identity_already_exists", AppErrorCode.USER_ALREADY_EXISTS),
//
//            // permission
//            Map.entry("not_admin", AppErrorCode.UNAUTHORIZED),
//            Map.entry("no_authorization", AppErrorCode.UNAUTHORIZED),
//            Map.entry("user_banned", AppErrorCode.ACCOUNT_BANNED),
//
//            // auth
//            Map.entry("session_expired", AppErrorCode.AUTH_EXPIRED),
//            Map.entry("refresh_token_not_found", AppErrorCode.AUTH_EXPIRED),
//
//            // not found
//            Map.entry("user_not_found", AppErrorCode.NOT_FOUND),
//
//            // system
//            Map.entry("unexpected_failure", AppErrorCode.SYSTEM_ERROR),
//            Map.entry("unknown", AppErrorCode.SYSTEM_ERROR)
    );

    private SupabaseErrorMapper() {}

    public static ErrorCode map(SupabaseErrorResponse errorResponse) {
        //if there is no case existed, keep the origin message
        log.error("Unknow exception happen when sent request to supabase");
        return MAP.getOrDefault(errorResponse.getErrorCode(), AppCommonErrorCode.UNKNOWN_EXCEPTION);
    }
}
