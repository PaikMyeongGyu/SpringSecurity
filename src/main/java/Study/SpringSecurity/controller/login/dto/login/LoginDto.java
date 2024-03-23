package Study.SpringSecurity.controller.login.dto.login;

import Study.SpringSecurity.entity.Member;
import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
    private String username;

    public Member createMemberByLoginDto(){
        if(email == null || password == null || username == null){
            throw new RuntimeException("Login is failed");
        }
        return new Member(email, password, username);
    }
}
