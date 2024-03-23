package Study.SpringSecurity.repository;

import Study.SpringSecurity.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
