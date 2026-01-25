package com.sep490.g28.hvh.be.auth;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * convert jwt claim to Granted Authority and get the current user info
 */
@Component
@RequiredArgsConstructor
public class SupabaseJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final VolunteerRepository volunteerRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        String roleStr = jwt.getClaim("app_metadata") != null
                ? ((Map<String, Object>) jwt.getClaim("app_metadata")).get("role").toString()
                : null;

        if (roleStr == null) {
            throw new AccessDeniedException("Role not found in JWT");
        }

        try{
            ERole role = ERole.valueOf(roleStr);

            GrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role.name());

            //set current user info
            CurrentUser currentUser = new CurrentUser(
                    userId,
                    jwt.getClaimAsString("email"),
                    role
            );

            JwtAuthenticationToken authentication =
                    new JwtAuthenticationToken(jwt, List.of(authority));

            // attach current user to authentication
            authentication.setDetails(currentUser);

            return authentication;
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Role is not valid");
        }
    }
}
