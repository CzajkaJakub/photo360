package pl.put.photo360.auth;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_ACCOUNT_LOCKED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_LOGIN_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_EMAIL;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_LOGIN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_PASSWORD;
import static pl.put.photo360.shared.dto.UserRole.USER_ROLE;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dao.UserDataDao;
import pl.put.photo360.dao.UserRoleDao;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.entity.UserRoleEntity;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.exception.AccountLockedException;
import pl.put.photo360.shared.exception.EmailExistsInDbException;
import pl.put.photo360.shared.exception.LoginExistsInDbException;
import pl.put.photo360.shared.exception.UserNotFoundException;
import pl.put.photo360.shared.exception.WrongPasswordException;
import pl.put.photo360.shared.fieldValidator.FieldValidator;

/**
 * Authorization service, which is also connected to userData database.
 */
@Service
public class AuthService
{
    private final UserDataDao userDataDao;
    private final UserRoleDao userRoleDao;
    private final AuthTokenService authTokenService;
    private final Configuration configuration;

    @Autowired
    public AuthService( UserDataDao aUserDataDao, UserRoleDao aUserRoleDao,
        AuthTokenService aAuthTokenService, Configuration aConfiguration )
    {
        userDataDao = aUserDataDao;
        authTokenService = aAuthTokenService;
        userRoleDao = aUserRoleDao;
        configuration = aConfiguration;
    }

    /**
     * Find user by given login.
     * 
     * @param aLogin
     *                   User's request login.
     */
    public Optional< UserDataEntity > findByLogin( String aLogin )
    {
        var foundUser = userDataDao.findByLogin( aLogin );
        if( foundUser == null )
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of( foundUser );
        }
    }

    /**
     * Find user by given email.
     * 
     * @param aEmail
     *                   User's request email.
     */
    public Optional< UserDataEntity > findByEmail( String aEmail )
    {
        var foundUser = userDataDao.findByEmail( aEmail );
        if( foundUser == null )
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of( foundUser );
        }
    }

    /**
     * Function creates a new user account based on given request, there is a validation which requires email
     * and login which are not currently assigned to any account.
     * 
     * @param aRegisterRequestDto
     *                                User's request.
     */
    public void saveNewUser( RegisterRequestDto aRegisterRequestDto )
    {
        if( findByLogin( aRegisterRequestDto.getLogin() ).isPresent() )
        {
            throw new LoginExistsInDbException( STATUS_LOGIN_ALREADY_EXISTS );
        }
        else if( findByEmail( aRegisterRequestDto.getEmail() ).isPresent() )
        {
            throw new EmailExistsInDbException( STATUS_EMAIL_ALREADY_EXISTS );
        }
        else
        {
            // By default, we set basic role to the new user.
            UserRoleEntity userRole = userRoleDao.findByRoleName( USER_ROLE.getName() );
            UserDataEntity newUser = new UserDataEntity( aRegisterRequestDto, userRole );
            userDataDao.save( newUser );
        }
    }

    /**
     * Function changes users password if given old password was correct.
     */
    @Transactional
    public void changeUserPassword( PasswordChangeRequestDto aPasswordChangeRequestDto )
        throws NoSuchAlgorithmException, IOException
    {
        Optional< UserDataEntity > userToCheck = findByEmail( aPasswordChangeRequestDto.getEmail() );
        if( userToCheck.isPresent() )
        {
            UserDataEntity foundUser = userToCheck.get();
            if( foundUser.isLocked() )
            {
                throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
            }
            else if( checkPassword( foundUser, aPasswordChangeRequestDto.getOldPassword() ) )
            {
                FieldValidator.validatePassword( aPasswordChangeRequestDto.getNewPassword() );
                String hashedNewPassword =
                    BCrypt.hashpw( aPasswordChangeRequestDto.getNewPassword(), foundUser.getSalt() );
                foundUser.setPassword( hashedNewPassword );
                userDataDao.save( foundUser );
            }
            else
            {
                throw new WrongPasswordException( STATUS_WRONG_PASSWORD );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_EMAIL );
        }
    }

    public LoginResponseDto logIntoSystemAttempt( LoginRequestDto aLoginRequestDto )
    {
        Optional< UserDataEntity > userToCheck = findByLogin( aLoginRequestDto.getLogin() );
        if( userToCheck.isPresent() )
        {
            UserDataEntity foundUser = userToCheck.get();
            unlockIfLockTimeExpired( foundUser );
            boolean checkPass = checkPassword( foundUser, aLoginRequestDto.getPassword() );

            if( checkPass && !foundUser.isLocked() )
            {
                String authToken = authTokenService.generateJwt( null, foundUser );
                resetFailedAttempts( foundUser );
                Instant lastLoggedUserDateTime = foundUser.getLastLoggedTime();
                foundUser.reloadLastLogTime( Instant.now() );
                userDataDao.save( foundUser );

                return new LoginResponseDto( foundUser.getEmail(), authToken,
                    Instant.ofEpochSecond( configuration.getTOKEN_EXPIRATION_TIME() ),
                    lastLoggedUserDateTime );
            }
            else if( !checkPass && foundUser.getFailedAttempt() < configuration.getMAX_LOGIN_ATTEMPT() )
            {
                increaseFailedAttempts( foundUser );
                throw new WrongPasswordException( STATUS_WRONG_PASSWORD );
            }
            else
            {
                if( Objects.equals( foundUser.getFailedAttempt(), configuration.getMAX_LOGIN_ATTEMPT() ) )
                    lock( foundUser );
                throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_LOGIN );
        }
    }

    /**
     * Function increases number of failed log in attempts in database
     *
     * @param user
     */
    @Transactional
    public void increaseFailedAttempts( UserDataEntity user )
    {
        user.increaseFailAttempts();
        userDataDao.save( user );
    }

    /**
     * Function resets the number of failed log in attmepts in database
     * 
     * @param user
     */
    @Transactional
    public void resetFailedAttempts( UserDataEntity user )
    {
        user.setFailedAttempt( 0 );
        userDataDao.save( user );
    }

    /**
     * Function locks user
     * 
     * @param user
     */
    @Transactional
    public void lock( UserDataEntity user )
    {
        user.setLocked( true );
        user.setLockTime( Instant.now() );

        userDataDao.save( user );
    }

    /**
     * Function checks whether time of lock have passed
     *
     * @param user
     */
    @Transactional
    public void unlockIfLockTimeExpired( UserDataEntity user )
    {
        Instant currentTimeInMillis = Instant.now();
        if( user.isLocked() )
        {
            Instant lockTimeInMillis = user.getLockTime();

            if( lockTimeInMillis.plus( configuration.getACCOUNT_LOCK_TIME(), ChronoUnit.MILLIS )
                .isBefore( currentTimeInMillis ) )
            {
                user.setLocked( false );
                user.setLockTime( null );
                user.setFailedAttempt( 0 );
                userDataDao.save( user );
            }
        }
    }

    /**
     * Function checks if given password matches this in database
     *
     * @param user
     * @param password
     * @return
     */
    public boolean checkPassword( UserDataEntity user, String password )
    {
        return BCrypt.checkpw( password, user.getPassword() );
    }
}
