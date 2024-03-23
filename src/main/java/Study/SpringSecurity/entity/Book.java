package Study.SpringSecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Book {

    @Id @GeneratedValue
    private Long id;

    private String title;

    // @Column(columnDefinition = "TEXT") 편의상 닫음
    private String content;

    private boolean privateStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") @JsonIgnore
    private Member member;

    public Book(String title, String content, boolean privateStatus, Member member) {
        this.title = title;
        this.content = content;
        this.privateStatus = privateStatus;
        this.member = member;
    }
}
