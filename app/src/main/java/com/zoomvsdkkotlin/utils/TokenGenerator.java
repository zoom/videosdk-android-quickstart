package com.zoomvsdkkotlin.utils;
import com.zoomvsdkkotlin.activities.JWTOptions;

import org.jetbrains.annotations.NotNull;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenGenerator {

// This util is to generate JWTs.
// THIS IS NOT A SAFE OPERATION TO DO IN YOUR APP IN PRODUCTION.
// JWTs should be provided by a backend server as they require a secret
// WHICH IS NOT SAFE TO STORE ON DEVICE!
    public static String generateToken(@NotNull JWTOptions jwtOptions, @NotNull String sdkKey, @NotNull String sdkSecret) {

        Key signingKey = Keys.hmacShaKeyFor(sdkSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        Map<String, Object> claims = new HashMap<>();
        claims.put("app_key", sdkKey);
        claims.put("role_type", jwtOptions.getRole());
        claims.put("tpc", jwtOptions.getSessionName());
        claims.put("version", 1);
        claims.put("iat", Date.from(Instant.now()));
        claims.put("exp", Date.from(now.plus(5, ChronoUnit.MINUTES)));
        claims.put("user_identity", jwtOptions.getUserIdentity());
//        claims.put("session_key", jwtOptions.getSessionkey());
        claims.put("geo_regions", jwtOptions.getGeo_regions());
        claims.put("cloud_recording_option", jwtOptions.getCloud_recording_option());
        claims.put("cloud_recording_election", jwtOptions.getCloud_recording_election());
        claims.put("telemetry_tracking_id", jwtOptions.getTelemetry_tracking_id());
        claims.put("video_webrtc_mode", jwtOptions.getVideo_webrtc_mode());
        claims.put("audio_webrtc_mode", jwtOptions.getAudio_webrtc_mode());

        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }
}