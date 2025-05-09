package com.travel.dto.response;

public record TokenResponse(String grantType, String accessToken, String refreshToken) {

}
