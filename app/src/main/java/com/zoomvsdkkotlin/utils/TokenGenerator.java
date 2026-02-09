package com.zoomvsdkkotlin.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

record JWTOptions(
        String sessionName,
        Integer role,
        String userIdentity,
        String sessionKey,
        String geo_regions,
        Integer cloud_recording_option,
        Integer cloud_recording_election,
        String telemetry_tracking_id,
        Integer video_webrtc_mode,
        Integer audio_webrtc_mode
){}

public class TokenGenerator {
    // This util is to generate JWTs.
// THIS IS NOT A SAFE OPERATION TO DO IN YOUR APP IN PRODUCTION.
// JWTs should be provided by a backend server as they require a secret
// WHICH IS NOT SAFE TO STORE ON DEVICE!
    public static String generateToken(JWTOptions jwtOptions, String sdkKey, String sdkSecret) {

        Key signingKey = Keys.hmacShaKeyFor(sdkSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        Map<String, Object> claims = new HashMap<>();
        claims.put("app_key", sdkKey);
        claims.put("role_type", jwtOptions.role());
        claims.put("tpc", jwtOptions.sessionName());
        claims.put("version", 1);
        claims.put("iat", Date.from(Instant.now()));
        claims.put("exp", Date.from(now.plus(5, ChronoUnit.MINUTES)));
        claims.put("user_key", jwtOptions.userIdentity());
//        claims.put("session_key", jwtOptions.sessionKey());
        claims.put("geo_regions", jwtOptions.geo_regions());
        claims.put("cloud_recording_option", jwtOptions.cloud_recording_option());
        claims.put("cloud_recording_election", jwtOptions.cloud_recording_election());
        claims.put("telemetry_tracking_id", jwtOptions.telemetry_tracking_id());
        claims.put("video_webrtc_mode", jwtOptions.video_webrtc_mode());
        claims.put("audio_webrtc_mode", jwtOptions.audio_webrtc_mode());

        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Must provide all 4 arguments in this order: [topic] [role(0 or 1)] [sdk key] [sdk secret]");
            return;
        }

        JWTOptions body = new JWTOptions(
                args[0],
                Integer.parseInt(args[1]),
                "",
                "",
                "",
                0,
                0,
                "internal-dev5",
                0,
                0
        );

        String signature = TokenGenerator.generateToken(body, args[2], args[3]);
        System.out.println("Your JWT Token: " + signature);
    }
}