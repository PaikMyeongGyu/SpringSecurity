package Study.SpringSecurity.repository;

import Study.SpringSecurity.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select b from Book b join fetch b.member where b.member.id = :id")
    List<Book> findByIdWithMember(@Param("id") Long id);
}
