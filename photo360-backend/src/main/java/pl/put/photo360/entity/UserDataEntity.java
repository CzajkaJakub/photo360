package pl.put.photo360.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCrypt;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import pl.put.photo360.dto.RegisterRequestDto;

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

    @Column( name = "reset_password_token_expiration_date" )
    private Instant resetPasswordTokenExpirationDate;

    @Column( name = "reset_password_token" )
    private String resetPasswordToken;

    @Column( name = "email_verification_token" )
    private String emailVerificationToken;

    @Column( name = "email_verified" )
    private boolean emailVerified;

    @Column( name = "email_verification_token_expiration_date" )
    private Instant emailVerificationTokenExpirationDate;

    @ManyToMany( cascade =
    { CascadeType.MERGE } )
    @JoinTable( name = "user_roles", joinColumns = @JoinColumn( name = "user_id" ), inverseJoinColumns = @JoinColumn( name = "role_id" ) )
    private Set< RoleEntity > roles = new HashSet<>();

    @OneToMany( mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private Set< PhotoDataEntity > photosData = new HashSet<>();

    @OneToMany( mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private Set< FavouriteGifDataEntity > favouritesGif = new HashSet<>();

    public UserDataEntity( RegisterRequestDto aRegisterRequestDto, Set< RoleEntity > aUserRoles,
        String aEmailVerificationToken )
    {
        login = aRegisterRequestDto.getLogin();
        email = aRegisterRequestDto.getEmail();
        password = BCrypt.hashpw( aRegisterRequestDto.getPassword(), BCrypt.gensalt( 10 ) );
        emailVerificationToken = aEmailVerificationToken;
        creationDate = Instant.now();
        roles = aUserRoles;
        failedAttempt = 0;
        isLocked = false;
        emailVerified = false;
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
