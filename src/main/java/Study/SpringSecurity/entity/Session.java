package Study.SpringSecurity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Session {

    @Id @GeneratedValue
    private Long id;

    private String session;
    private String username;

    public Session(String session, String username) {
        this.session = session;
        this.username = username;
    }
}
