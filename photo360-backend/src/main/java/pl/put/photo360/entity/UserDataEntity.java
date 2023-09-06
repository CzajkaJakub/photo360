package pl.put.photo360.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCrypt;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
public class UserDataEntity implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( name = "login", unique = true, nullable = false )
    @NotEmpty( message = "Login can't be empty!" )
    @NotNull( message = "Login can't be empty!" )
    private String login;

    @Column( name = "email", unique = true, nullable = false )
    @NotEmpty( message = "Email can't be empty!" )
    @NotNull( message = "Email can't be empty!" )
    private String email;

    @Column( name = "password", nullable = false )
    @NotEmpty( message = "Password can't be empty!" )
    @NotNull( message = "Password can't be empty!" )
    private String password;

    @Column( name = "creation_date_time", nullable = false )
    private Instant creationDate;

    @Column( name = "last_logged_date_time" )
    private Instant lastLoggedTime;

    @Column( name = "is_locked", nullable = false )
    private boolean isLocked;

    @Column( name = "failed_attempt", nullable = false )
    private Integer failedAttempt;

    @Column( name = "lock_time" )
    private Instant lockTime;

    @Column( name = "salt", nullable = false )
    private String salt;

    @ManyToMany( cascade =
    { CascadeType.ALL } )
    @JoinTable( name = "user_roles", joinColumns = @JoinColumn( name = "user_id" ), inverseJoinColumns = @JoinColumn( name = "role_id" ) )
    private Set< RoleEntity > roles = new HashSet<>();

    @OneToMany( mappedBy = "userId", cascade = CascadeType.ALL )
    private Set< PhotoDataEntity > photosData = new HashSet<>();

    public UserDataEntity( RegisterRequestDto aRegisterRequestDto, Set< RoleEntity > userRoles )
    {
        salt = BCrypt.gensalt( 10 );
        login = aRegisterRequestDto.getLogin();
        email = aRegisterRequestDto.getEmail();
        password = BCrypt.hashpw( aRegisterRequestDto.getPassword(), salt );
        creationDate = Instant.now();
        roles = userRoles;
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
