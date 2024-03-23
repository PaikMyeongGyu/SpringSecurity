package Study.SpringSecurity.controller.login;

import Study.SpringSecurity.controller.login.dto.login.LoginDto;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.repository.MemberRepository;
import Study.SpringSecurity.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final MemberRepository memberRepository;

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
    public Member getUserDetailsAfterLogin(Authentication authentication) {
        return loginService.orElseGetMember(authentication.getName());
    }
}
