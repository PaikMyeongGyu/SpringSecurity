package Study.SpringSecurity;

import Study.SpringSecurity.entity.Authority;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.repository.AuthorityRepository;
import Study.SpringSecurity.repository.MemberRepository;
import Study.SpringSecurity.type.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringSecurityApplication {

	private final MemberRepository memberRepository;
	private final AuthorityRepository authorityRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

	@PostConstruct
	@Transactional
	public void init(){

		String password = passwordEncoder.encode("12345");
		Member member = new Member("test@example.com", password, "happy");
		memberRepository.save(member);

		Authority admin = new Authority("ROLE_ADMIN");
		Authority user = new Authority("ROLE_USER");

		member.addAuthorities(List.of(admin, user));
		authorityRepository.save(admin);
		authorityRepository.save(user);
	}
}
