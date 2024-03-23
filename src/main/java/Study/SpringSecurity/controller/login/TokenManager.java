package Study.SpringSecurity.controller.login;

import Study.SpringSecurity.controller.login.dto.login.TokenDto;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    private static ThreadLocal<TokenDto> tokenDtoThreadLocal = new ThreadLocal<>();

    public void setToken(String accessToken, String refreshToken){
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        tokenDtoThreadLocal.set(tokenDto);
    }

    public TokenDto getToken(){
        return tokenDtoThreadLocal.get();
    }

    public void removeToken(){
        tokenDtoThreadLocal.remove();
    }


}
