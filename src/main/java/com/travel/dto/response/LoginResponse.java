package com.travel.dto.response;


public record LoginResponse(
        String accessToken,
        String refreshToken
) {
    public static LoginResponse ofPairedTokens(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
