package Study.SpringSecurity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Session {

    @Id @GeneratedValue
    private Long id;

    private String session;
    private String username;
    private Boolean blackStatus;

    public Session(String session, String username) {
        this.session = session;
        this.username = username;
        blackStatus = Boolean.FALSE;
    }

    public void reissueSession(String session, String username){
        this.session = session;
        this.username = username;
        blackStatus = Boolean.FALSE;
    }

    public void blackSession(){
        blackStatus = Boolean.TRUE;
    }
}
