package Study.SpringSecurity.service;

import Study.SpringSecurity.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoginServiceTest {
    @Autowired
    LoginService loginService;

//    @Test
//    @DisplayName("orElse 확인하기")
//    void orElseTest(){
//        // 없는 회원
//        Member member = loginService.orElseMember("test@example.com");
//        System.out.println("member = " + member);
//
//        // 있는 회원
//        Member member2 = loginService.orElseMember("test2@example.com");
//        System.out.println("member = " + member2);
//    }

//    @Test
//    @DisplayName("orElseGet 확인하기")
//    void orElseGetTest(){
//        // 없는 회원
//        Member member = loginService.orElseGetMember("test@example.com");
//        System.out.println("member = " + member);
//
//        // 있는 회원
//        Member member2 = loginService.orElseGetMember("test2@example.com");
//        System.out.println("member = " + member2);
//    }

//    @Test
//    @DisplayName("Unknown 확인하기")
//    void unknownTest(){
//        // 없는 회원
//        Member member = loginService.orElseMember("test2@example.com");
//        System.out.println("Before: member.getPassword() = " + member.getPassword());
//        member.setEncodedPassword("12345");
//        System.out.println("After: member.getPassword() = " + member.getPassword());
//    }
}