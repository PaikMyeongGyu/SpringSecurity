package Study.SpringSecurity.service;

import Study.SpringSecurity.entity.Session;
import Study.SpringSecurity.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public void makeSessionAndSave(String refreshToken, String userEmail){
        Session newSession = new Session(refreshToken, userEmail);
        sessionRepository.save(newSession);
    }

    public void deleteSession(Session session){
        sessionRepository.delete(session);
    }

    public Session findSession(String userEmail){
        return sessionRepository.findByUsername(userEmail);
    }

    public void sessionReissue(String refreshToken, String username){
        Session findSession = sessionRepository.findByUsername(username);

        findSession.reissueSession(refreshToken, username);
    }

}
