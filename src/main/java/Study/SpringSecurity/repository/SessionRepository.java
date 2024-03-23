package Study.SpringSecurity.repository;

import Study.SpringSecurity.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Session findBySession(String session);
    Session findByUsername(String username);
}
