package com.helloWorld.tech.security;

import com.helloWorld.tech.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtServiceImpl jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring("Bearer ".length()).trim();
        try {
            Claims claims = jwtService.parseAccessTokenClaims(token);
            String userId = claims.getSubject();
            List<SimpleGrantedAuthority> authorities = extractAuthorities(claims.get("roles"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            // Invalid token -> continue without authentication; Security will return 401 for protected endpoints.
        }

        filterChain.doFilter(request, response);
    }

    private static List<SimpleGrantedAuthority> extractAuthorities(Object rolesClaim) {
        List<String> roles = new ArrayList<>();

        if (rolesClaim instanceof List<?> rawList) {
            for (Object o : rawList) {
                if (o == null) continue;
                String r = String.valueOf(o).trim();
                if (!r.isEmpty()) roles.add(r);
            }
        } else if (rolesClaim instanceof String s) {
            // Support either a single role ("ADMIN") or a CSV list ("ADMIN,USER")
            for (String part : s.split(",")) {
                String r = part.trim();
                if (!r.isEmpty()) roles.add(r);
            }
        }

        List<SimpleGrantedAuthority> out = new ArrayList<>(roles.size());
        for (String role : roles) {
            String normalized = role.toUpperCase().startsWith("ROLE_")
                    ? role.toUpperCase()
                    : "ROLE_" + role.toUpperCase();
            out.add(new SimpleGrantedAuthority(normalized));
        }
        return out;
    }
}

