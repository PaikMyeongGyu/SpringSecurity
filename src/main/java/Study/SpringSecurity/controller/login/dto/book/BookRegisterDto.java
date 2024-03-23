package Study.SpringSecurity.controller.login.dto.book;

import Study.SpringSecurity.entity.Book;
import Study.SpringSecurity.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
public class BookRegisterDto {

    private String title;
    private String content;
    private boolean privateStatus;

    public Book makeBookWithMember(Member member){
        return new Book(title, content, privateStatus, member);
    }
}
