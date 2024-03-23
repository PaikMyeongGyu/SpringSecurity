package Study.SpringSecurity.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String userEmail;
    private String password;

    private String username;

    @OneToMany(mappedBy = "member")
    private List<Authority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Book> books = new ArrayList<>();

    public Member(String userEmail, String password, String username) {
        this.userEmail = userEmail;
        this.password = password;
        this.username = username;
    }

    public Member(String userEmail){
        this.userEmail = userEmail;
    }

    public void addAuthorities(List<Authority> authorities){
        authorities.stream().forEach(authority ->{
            authority.setMember(this);
            this.authorities.add(authority);
        });
    }

    public void setEncodedPassword(String password){
        this.password = password;
    }

}
