package Study.SpringSecurity.filter;

import Study.SpringSecurity.constants.SecurityConstants;
import Study.SpringSecurity.controller.login.TokenManager;
import Study.SpringSecurity.controller.login.dto.login.TokenDto;
import Study.SpringSecurity.entity.Authority;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.repository.MemberRepository;
import Study.SpringSecurity.repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

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
                //처리법이 생각이 안나....
                Claims claims = Jwts.parser().build()
                        .parseClaims.getPayload();

                String uuid = String.valueOf(claims.get("UUID"));
                String username = String.valueOf(claims.get("username"));
                Session session = sessionRepository.findBySession(uuid);

                Date date = new Date();
                // 만료 시 재로그인 요청
                if(claims.getExpiration().after(date)){

                    if(session != null){
                        sessionRepository.delete(session);
                    }

                    throw new BadCredentialsException("Please Login Again");
                }

                // 내부 세션이 없는 경우도 재로그인 요청
                if(session == null){
                    throw new BadCredentialsException("Please Login Again");
                }

                // 세션이 있는 경우 토큰 재발급
                Member findMember = memberRepository.findByUserEmail(username);

                String accessToken = Jwts.builder().issuer("Recfli").subject("JWT Token")
                        .claim("username", findMember.getUsername())
                        .claim("authorities", findMember.getAuthorities())
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 1800*1000))
                        .signWith(key).compact();

                UUID newUUID = UUID.randomUUID();
                String refreshToken = Jwts.builder()
                        .claim("UUID", newUUID)
                        .claim("username", findMember.getUsername())
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 10000))
                        .compact();
                // 3600*1000*24*7
                tokenManager.setToken(accessToken, refreshToken);

            }  catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!", e);
            }
        }
        filterChain.doFilter(request, response);
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