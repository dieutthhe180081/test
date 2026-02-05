package com.sep490.g28.hvh.be.dto.supabase;

import java.util.List;

public record UserListResponse (
        List<UserResponse> users
) {}
