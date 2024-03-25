package Study.SpringSecurity.filter;

import Study.SpringSecurity.constants.SecurityConstants;
import Study.SpringSecurity.controller.login.TokenManager;
import Study.SpringSecurity.controller.login.dto.login.TokenDto;
import Study.SpringSecurity.entity.Authority;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.repository.MemberRepository;
import Study.SpringSecurity.repository.SessionRepository;
import Study.SpringSecurity.service.LoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final MemberRepository memberRepository;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        SecretKey key = Keys.hmacShaKeyFor(
                SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

        if (null != jwt && !request.getRequestURI().equals("/reissue")) {
            try {
                Claims claims = getClaims(key, jwt);

                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("authorities");
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!");
            }

        } else if(null != jwt && request.getRequestURI().equals("/reissue")){
            try {

                /**
                 * 만료 시간 지났을 시 BadCredentialException
                 * -> 프엔에게 해당 에러코드시 login 화면으로 가라 혹은 리다이렉션 헤더 채워주면 됨
                  */
                Claims claims = getClaims(key, jwt);

                String username = String.valueOf(claims.get("username"));
                Session findSession = sessionRepository.findByUsername(username);

                // 블랙리스트 등록의 경우는 토큰 재발급 요청
                if(findSession.getBlackStatus() == Boolean.TRUE){
                    throw new BadCredentialsException("Please Login Again");
                }

                // 최신 발급 내용과 다른 경우도 토큰 재발급 요청
                if(!findSession.getSession().equals(jwt)){
                    throw new BadCredentialsException("Please Login Again");
                }

                // 로그인 상태인데 리프레시 토큰 만료 안되고 엑세스 토큰만 만료됐을시 재발급
                // 바로 get해도 되는 이유는 없으면 BadCredential이고 있으면 원래 로직대로 돼서 상관없음.
                Member findMember = memberRepository.findByUserEmailWithAuthorities(username).get();
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(populateAuthorities(findMember.getAuthorities())));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }  catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private static Claims getClaims(SecretKey key, String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/user");
    }

    private String populateAuthorities(List<Authority> authorities) {
        Set<String> authoritiesSet = new HashSet<>();
        for (Authority authority : authorities) {
            authoritiesSet.add(authority.getRole());
        }
        return String.join(",", authoritiesSet);
    }

}