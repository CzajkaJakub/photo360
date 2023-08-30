package pl.put.photo360.entity;

import java.time.Instant;

import org.springframework.security.crypto.bcrypt.BCrypt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.put.photo360.shared.dto.RegisterRequestDto;

@Getter
@Setter
@Entity
@Table( name = "user_data", schema = "public" )
@NoArgsConstructor
public class UserDataEntity
{
    @Id
    @Column( name = "login", unique = true, nullable = false )
    @NotEmpty( message = "Login can't be empty!" )
    @NotNull( message = "Login can't be empty!" )
    private String login;

    @Column( name = "email", unique = false, nullable = false )
    @NotEmpty( message = "Email can't be empty!" )
    @NotNull( message = "Email can't be empty!" )
    private String email;

    @Column( name = "password", unique = false, nullable = false )
    @NotEmpty( message = "Password can't be empty!" )
    @NotNull( message = "Password can't be empty!" )
    private String password;

    @Column( name = "creation_date_time" )
    private Instant creationDate;

    @Column( name = "last_logged_date_time" )
    private Instant lastLoggedTime;

    @Column( name = "is_locked", nullable = false )
    private boolean isLocked;

    @Column( name = "failed_attempt", nullable = false )
    private Integer failedAttempt;

    @Column( name = "lock_time" )
    private Instant lockTime;

    @Column( name = "salt" )
    private String salt;

    @ManyToOne( fetch = FetchType.EAGER )
    @JoinColumn( name = "role_id" )
    private UserRoleEntity role;

    public UserDataEntity( RegisterRequestDto aRegisterRequestDto, UserRoleEntity aUserRoleEntity )
    {
        salt = BCrypt.gensalt( 10 );
        login = aRegisterRequestDto.getLogin();
        email = aRegisterRequestDto.getEmail();
        password = BCrypt.hashpw( aRegisterRequestDto.getPassword(), salt );
        role = aUserRoleEntity;
        creationDate = Instant.now();
        failedAttempt = 0;
        isLocked = false;
    }

    public void increaseFailAttempts()
    {
        failedAttempt++;
    }

    public void reloadLastLogTime( Instant lastLoggedTime )
    {
        this.lastLoggedTime = lastLoggedTime;
    }
}
