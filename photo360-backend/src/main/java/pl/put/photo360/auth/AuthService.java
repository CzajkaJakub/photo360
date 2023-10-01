package pl.put.photo360.auth;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_ACCOUNT_LOCKED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_LOGIN_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PASSWORD_CAN_NOT_BE_THE_SAME;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_RESET_TOKEN_EXPIRED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_EMAIL;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_LOGIN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_FROM_RESET_TOKEN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_PASSWORD;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dao.UserDataDao;
import pl.put.photo360.dao.UserRoleDao;
import pl.put.photo360.entity.RoleEntity;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.service.EmailService;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.ResetPasswordRequestDto;
import pl.put.photo360.shared.dto.UserRoles;
import pl.put.photo360.shared.exception.AccountLockedException;
import pl.put.photo360.shared.exception.EmailExistsInDbException;
import pl.put.photo360.shared.exception.LoginExistsInDbException;
import pl.put.photo360.shared.exception.ServiceException;
import pl.put.photo360.shared.exception.UserNotFoundException;
import pl.put.photo360.shared.exception.WrongPasswordException;
import pl.put.photo360.shared.fieldValidator.FieldValidator;
import pl.put.photo360.shared.utils.JwtValidator;

/**
 * Authorization service, which is also connected to userData database.
 */
@Service
public class AuthService
{
    private final UserDataDao userDataDao;
    private final UserRoleDao userRoleDao;
    private final JwtValidator jwtValidator;
    private final Configuration configuration;
    private final FieldValidator fieldValidator;
    private final EmailService emailService;

    @Autowired
    public AuthService( UserDataDao aUserDataDao, UserRoleDao aUserRoleDao, JwtValidator aJwtValidator,
        Configuration aConfiguration, FieldValidator aFieldValidator, EmailService emailService )
    {
        userDataDao = aUserDataDao;
        userRoleDao = aUserRoleDao;
        jwtValidator = aJwtValidator;
        configuration = aConfiguration;
        fieldValidator = aFieldValidator;
        this.emailService = emailService;
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
     * Find user by given reset password token.
     *
     * @param aResetPasswordToken
     *                                User's request token.
     */
    public Optional< UserDataEntity > findByResetPasswordToken( String aResetPasswordToken )
    {
        var foundUser = userDataDao.findByResetPasswordToken( aResetPasswordToken );
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
     * Find user by given jwt token.
     *
     * @param aToken
     *                   User's request jwt token.
     */
    public Optional< UserDataEntity > findByToken( String aToken )
    {
        var extractedLoginFromToken = jwtValidator.extractLoginFromToken( aToken );
        var foundUser = userDataDao.findByLogin( extractedLoginFromToken );
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
    public void saveNewUser( RegisterRequestDto aRegisterRequestDto, List< UserRoles > aUserRolesList )
    {
        fieldValidator.validateRegisterForm( aRegisterRequestDto );

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
            UserDataEntity newUser = new UserDataEntity( aRegisterRequestDto );
            assignRolesToAccount( newUser, aUserRolesList );
            userDataDao.save( newUser );
        }
    }

    public void assignRolesToAccount( UserDataEntity aUserDataEntity, List< UserRoles > aUserRolesList )
    {
        Set< RoleEntity > userRoles = new HashSet<>();
        aUserRolesList.forEach( role -> userRoles.add( userRoleDao.findByRoleName( role.getName() ) ) );
        aUserDataEntity.setRoles( userRoles );
    }

    /**
     * Function changes users password if given old password was correct.
     */
    @Transactional
    public void changeUserPassword( String aAuthorizationToken,
        PasswordChangeRequestDto aPasswordChangeRequestDto )
    {
        fieldValidator.validatePassword( aPasswordChangeRequestDto.getNewPassword() );
        fieldValidator.validatePassword( aPasswordChangeRequestDto.getOldPassword() );
        Optional< UserDataEntity > userToCheck =
            findByLogin( jwtValidator.extractLoginFromToken( aAuthorizationToken ) );
        if( userToCheck.isPresent() )
        {
            UserDataEntity foundUser = userToCheck.get();
            checkLockAndUnlockIfLockTimeExpired( foundUser );
            if( checkPassword( foundUser, aPasswordChangeRequestDto.getOldPassword() ) )
            {
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

    @Transactional
    public LoginResponseDto logIntoSystemAttempt( LoginRequestDto aLoginRequestDto )
    {
        fieldValidator.validateLoginForm( aLoginRequestDto );
        Optional< UserDataEntity > userToCheck = findByLogin( aLoginRequestDto.getLogin() );
        if( userToCheck.isPresent() )
        {
            UserDataEntity foundUser = userToCheck.get();
            checkLockAndUnlockIfLockTimeExpired( foundUser );
            boolean checkPass = checkPassword( foundUser, aLoginRequestDto.getPassword() );

            if( checkPass )
            {
                String authToken = jwtValidator.generateJwt( foundUser );
                resetFailedAttempts( foundUser );
                Instant lastLoggedUserDateTime = foundUser.getLastLoggedTime();
                foundUser.reloadLastLogTime( Instant.now() );
                userDataDao.save( foundUser );

                return new LoginResponseDto( foundUser.getEmail(), authToken,
                    Instant.ofEpochSecond( configuration.getTOKEN_EXPIRATION_TIME() ), lastLoggedUserDateTime,
                    foundUser.getRoles()
                        .stream()
                        .map( RoleEntity::getName )
                        .collect( Collectors.toSet() ) );
            }
            else if( foundUser.getFailedAttempt() < configuration.getMAX_LOGIN_ATTEMPT() )
            {
                increaseFailedAttempts( foundUser );
                throw new WrongPasswordException( STATUS_WRONG_PASSWORD );
            }
            else
            {
                if( Objects.equals( foundUser.getFailedAttempt(), configuration.getMAX_LOGIN_ATTEMPT() ) )
                    lockAccount( foundUser );
                throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_LOGIN );
        }
    }

    @Transactional
    public void requestPasswordReset( ResetPasswordRequestDto request )
    {
        var foundUser = findByEmail( request.getEmail() );
        if( foundUser.isPresent() )
        {
            var userEntity = foundUser.get();
            checkLockAndUnlockIfLockTimeExpired( userEntity );
            if( userEntity.isLocked() )
            {
                throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
            }
            else
            {
                String generatedUuidResetPasswordToken = generateResetPasswordToken();
                userEntity.setResetPasswordToken( generatedUuidResetPasswordToken );
                userEntity.setResetPasswordTokenExpirationDate( Instant.now()
                    .plus( configuration.getRESET_PASSWORD_TOKEN_EXPIRATION(), ChronoUnit.MILLIS ) );
                emailService.sendResetPasswordEmail( request.getEmail(), userEntity );
                userDataDao.save( userEntity );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_EMAIL );
        }
    }

    public void resetPassword( ResetPasswordRequestDto request )
    {
        var foundUser = findByResetPasswordToken( request.getResetPasswordToken() );
        if( foundUser.isPresent() )
        {
            var userEntity = foundUser.get();
            if( userEntity.getResetPasswordTokenExpirationDate()
                .isBefore( Instant.now() ) )
                throw new ServiceException( STATUS_RESET_TOKEN_EXPIRED );
            if( checkPassword( userEntity, request.getNewPassword() ) )
                throw new WrongPasswordException( STATUS_PASSWORD_CAN_NOT_BE_THE_SAME );
            fieldValidator.validatePassword( request.getNewPassword() );
            userEntity
                .setResetPasswordToken( BCrypt.hashpw( request.getNewPassword(), userEntity.getSalt() ) );
            userEntity.setResetPasswordToken( null );
            userEntity.setResetPasswordTokenExpirationDate( null );
            userDataDao.save( userEntity );
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_FROM_RESET_TOKEN );
        }
    }

    @Transactional
    public void checkLockAndUnlockIfLockTimeExpired( UserDataEntity aUserDataEntity )
    {
        unlockIfLockTimeExpired( aUserDataEntity );
        if( aUserDataEntity.isLocked() )
        {
            throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
        }
    }

    /**
     * Function increases number of failed log in attempts in database
     *
     * @param user
     *                 user entity
     */
    @Transactional
    public void increaseFailedAttempts( UserDataEntity user )
    {
        user.increaseFailAttempts();
        userDataDao.save( user );
    }

    /**
     * Function resets the number of failed log in attempts in database
     *
     * @param user
     *                 user entity
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
     *                 user entity
     */
    @Transactional
    public void lockAccount( UserDataEntity user )
    {
        user.setLocked( true );
        user.setLockTime( Instant.now() );

        userDataDao.save( user );
    }

    /**
     * Function checks whether time of lock have passed
     *
     * @param user
     *                 user entity
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
     *                     user entity
     * @param password
     *                     provided password
     */
    public boolean checkPassword( UserDataEntity user, String password )
    {
        return BCrypt.checkpw( password, user.getPassword() );
    }

    private String generateResetPasswordToken()
    {
        return UUID.randomUUID()
            .toString();
    }
}
