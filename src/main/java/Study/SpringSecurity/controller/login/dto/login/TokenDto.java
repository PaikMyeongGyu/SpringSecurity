package Study.SpringSecurity.controller.login.dto.login;

import lombok.Data;

@Data
public class TokenDto {
    String AccessToken;
    String RefreshToken;

    public TokenDto(String accessToken, String refreshToken) {
        AccessToken = accessToken;
        RefreshToken = refreshToken;
    }
}
