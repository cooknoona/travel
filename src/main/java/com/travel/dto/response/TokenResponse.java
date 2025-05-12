package com.travel.dto.response;

/** TokenResponse Dto, based on immutable object.
 *  For re-issue access token */
public record TokenResponse(
        String accessToken
) {
    public static TokenResponse ofAccessToken(String accessToken) {
        return new TokenResponse(
                accessToken
        );
    }
}
