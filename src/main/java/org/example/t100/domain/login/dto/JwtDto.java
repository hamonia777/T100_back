package org.example.t100.domain.login.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
