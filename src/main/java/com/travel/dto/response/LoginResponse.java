package com.travel.dto.response;

/** LoginResponse DTO, Immutable object to transfer to client,
 *  LoginResponse deliver with id(user pk) and nickname to display user's nickname on the top of client */
public record LoginResponse(
        String accessToken,
        Long id,
        String nickname

) {
    public static LoginResponse ofAccessTokenAndUserInfo(String accessToken, Long id, String nickname) {
        return new LoginResponse(
                accessToken,
                id,
                nickname
        );
    }
}
