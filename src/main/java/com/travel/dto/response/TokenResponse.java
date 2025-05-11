package com.travel.dto.response;

public record TokenResponse(
        String accessToken
) {
    public static TokenResponse ofAccessToken(String accessToken) {
        return new TokenResponse(accessToken);
    }
}
