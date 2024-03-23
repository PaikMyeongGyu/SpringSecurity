package Study.SpringSecurity.controller;

import Study.SpringSecurity.controller.login.dto.book.BookRegisterDto;
import Study.SpringSecurity.controller.login.dto.login.LoginDto;
import Study.SpringSecurity.entity.Book;
import Study.SpringSecurity.entity.Member;
import Study.SpringSecurity.repository.BookRepository;
import Study.SpringSecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/register")
    public ResponseEntity<Book> registerBook(@RequestBody BookRegisterDto registerDto){

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member findMember = memberRepository.findByUserEmail(userEmail);
        Book book = registerDto.makeBookWithMember(findMember);
        bookRepository.save(book);

        return ResponseEntity.ok(book);
    }


    @GetMapping("/{userEmail}")
    public ResponseEntity<List<Book>> getBooks(@PathVariable("userEmail") String userEmail){
        Optional<Member> findMember = memberRepository.findOptionalByUserEmail(userEmail);

        if(findMember.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Book> books = bookRepository.findByIdWithMember(findMember.get().getId());
        return ResponseEntity.ok(books);
    }
}
