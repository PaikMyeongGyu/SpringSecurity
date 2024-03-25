package Study.SpringSecurity.service;

import Study.SpringSecurity.controller.login.dto.login.LoginDto;
import Study.SpringSecurity.entity.Authority;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.repository.AuthorityRepository;
import Study.SpringSecurity.repository.MemberRepository;
import Study.SpringSecurity.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;

    @Transactional
    public Member saveMember(LoginDto dto){
        String password = passwordEncoder.encode(dto.getPassword());
        Member member = dto.createMemberByLoginDto();
        member.setEncodedPassword(password);

        List<Authority> authorities = List.of(new Authority("ROLE_USER"));
        member.addAuthorities(authorities);
        authorityRepository.save(authorities.get(0));
        return memberRepository.save(member);
    }

    @Transactional
    public void blackSession(String userEmail){
        Session findSession = sessionRepository.findByUsername(userEmail);
        findSession.blackSession();
    }

    public boolean memberExists(String userEmail){
        if(memberRepository.findByUserEmail(userEmail) != null){
            return true;
        }
        return false;
    }

    public Member orElseGetMember(String userEmail){
        Optional<Member> findMember = memberRepository.findOptionalByUserEmail(userEmail);
        return findMember.orElseGet(()-> new Member("Unknown"));
    }
}
