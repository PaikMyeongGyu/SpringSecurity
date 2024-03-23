package Study.SpringSecurity.filter;

import Study.SpringSecurity.constants.SecurityConstants;
import Study.SpringSecurity.controller.login.TokenManager;
import Study.SpringSecurity.controller.login.dto.login.TokenDto;
import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.repository.SessionRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null != authentication) {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            String accessToken = Jwts.builder().issuer("Recfli").subject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + 1800*1000))
                    .signWith(key).compact();

            String newUUID = UUID.randomUUID().toString();

            String refreshToken = Jwts.builder()
                    .claim("UUID", newUUID)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + 10000))
                    .compact();

            Session findSession = sessionRepository.findByUsername(authentication.getName());

            // Optional을 사용해보는 걸 생각해보자.
            if(sessionRepository.findByUsername(authentication.getName()) != null){
                sessionRepository.delete(findSession);
            }

            Session session = new Session(newUUID, authentication.getName());
            sessionRepository.save(session);

            // 3600*1000*24*7
            tokenManager.setToken(accessToken, refreshToken);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user");
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

}