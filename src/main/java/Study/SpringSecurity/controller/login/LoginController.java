package Study.SpringSecurity.controller.login;

import Study.SpringSecurity.constants.SecurityConstants;
import Study.SpringSecurity.controller.login.dto.login.LoginDto;
import Study.SpringSecurity.controller.login.dto.login.TokenDto;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.service.LoginService;
import Study.SpringSecurity.service.SessionService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final TokenManager tokenManager;
    private final SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody LoginDto dto){
        if(dto.getPassword() == null || !checkPassword(dto.getPassword()) || loginService.memberExists(dto.getEmail())){
            throw new RuntimeException("Password has Problem");
        }

        try{
            Member savedMember = loginService.saveMember(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Member is successfully registered");
        } catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An exception occured due to " + ex.getMessage());
        } catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occured due to " + ex.getMessage());
        }
    }

    public static boolean checkPassword(String password) {
        // 비밀번호 길이가 8자에서 15자 사이인지 확인
        if (password.length() < 8 || password.length() > 15) {
            return false;
        }

        // 대문자, 소문자, 특수문자가 모두 포함되었는지 확인
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasSpecialChar;
    }

    @RequestMapping("/user")
    public TokenDto getUserDetailsAfterLogin(Authentication authentication) {

        TokenDto token = tokenManager.getToken();

        Session findSession = sessionService.findSession(authentication.getName());
        // Optional을 사용해보는 걸 생각해보자.
        // 로그인 시 내용 제거 후 새 내용 추가
        if(findSession != null){
            sessionService.deleteSession(findSession);
        }
        sessionService.makeSessionAndSave(token.getRefreshToken(), authentication.getName());

        tokenManager.removeToken(); // 쓰레드 로컬 값을 반드시 지울 것
        return token;
    }

    @GetMapping("/reissue")
    public TokenDto reIssueToken(){
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String accessToken = Jwts.builder().issuer("Recfli").subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + 1800*1000))
                .signWith(key).compact();

        String uuid = UUID.randomUUID().toString();
        String refreshToken = Jwts.builder()
                .claim("username", authentication.getName())
                .claim("session", uuid)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + 3600*1000*24*7))
                .signWith(key).compact();

        sessionService.sessionReissue(refreshToken, authentication.getName());

        return new TokenDto(accessToken, refreshToken);
    }

    @PostMapping("/login/logout")
    public String userLogout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        loginService.blackSession(userEmail);

        return "유저의 로그아웃이 성공하였습니다.";
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
}
