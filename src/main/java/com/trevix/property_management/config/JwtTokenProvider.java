package com.trevix.property_management.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.trevix.property_management.security.CustomUserDetails;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;
    
    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationInMs;
    
    @Value("${jwt.reset-password-expiration}")
    private long jwtResetPasswordExpirationInMs;
    
    private Key key;
    
    // Initialize signing key
    private Key getSigningKey() {
        if (key == null) {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            key = Keys.hmacShaKeyFor(keyBytes);
        }
        return key;
    }
    
    // Generate access token from Authentication object
    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateAccessToken(userDetails);
    }
    
    // Generate access token from UserDetails
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", getUserIdFromUserDetails(userDetails));
        claims.put("email", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    // Generate refresh token
    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getId());
        claims.put("type", "refresh");
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    // Generate password reset token
    public String generatePasswordResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password_reset");
        claims.put("email", email);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtResetPasswordExpirationInMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    // Get user ID from token
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }
    
    // Get email from token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    // Get roles from token
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.get("roles", List.class);
    }
    
    // Get authentication from token
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        List<String> roles = getRolesFromToken(token);
        UUID userId = getUserIdFromToken(token);
        
        List<GrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        CustomUserDetails userDetails = CustomUserDetails.builder()
            .id(userId)
            .email(email)
            .password("") // Password not needed for authentication
            .authorities(authorities)
            .build();
        
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            userDetails, null, authorities);
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
    
    // Validate token and check if it's a refresh token
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        String type = claims.get("type", String.class);
        return "refresh".equals(type);
    }
    
    // Validate password reset token
    public boolean validatePasswordResetToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        String type = claims.get("type", String.class);
        return "password_reset".equals(type);
    }
    
    // Get token expiration
    public long getAccessTokenExpiration() {
        return jwtExpirationInMs;
    }
    
    public long getRefreshTokenExpiration() {
        return jwtRefreshExpirationInMs;
    }
    
    // Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    // Get remaining time in milliseconds
    public long getRemainingTimeInMs(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            Date expiration = claims.getExpiration();
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    // Extract user ID from UserDetails
    private UUID getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getId();
        }
        return null;
    }
    
    // Blacklist token (for logout)
    private Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();
    
    public void invalidateToken(String token) {
        tokenBlacklist.add(token);
        log.info("Token invalidated: {}", token);
    }
    
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
    
    // Clean expired tokens from blacklist (run this periodically)
    public void cleanBlacklist() {
        tokenBlacklist.removeIf(token -> isTokenExpired(token));
    }
}