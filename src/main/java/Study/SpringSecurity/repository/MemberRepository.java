package Study.SpringSecurity.repository;

import Study.SpringSecurity.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m join fetch m.authorities where m.userEmail = :userEmail")
    Optional<Member> findByUserEmailWithAuthorities(@Param("userEmail") String userEmail);
    Member findByUserEmail(String userEmail);
    Optional<Member> findOptionalByUserEmail(String userEmail);
}
