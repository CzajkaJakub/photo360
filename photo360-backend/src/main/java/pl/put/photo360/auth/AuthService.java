package pl.put.photo360.auth;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_ACCOUNT_LOCKED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_ALREADY_VERIFIED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_NOT_CONFIRMED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_VERIFICATION_TOKEN_NOT_VALID;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_LOGIN_ALREADY_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PASSWORD_CAN_NOT_BE_THE_SAME;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_RESET_TOKEN_EXPIRED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_EMAIL;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_BY_LOGIN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_FROM_TOKEN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_PASSWORD;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        Configuration aConfiguration, FieldValidator aFieldValidator, EmailService aEmailService )
    {
        userDataDao = aUserDataDao;
        userRoleDao = aUserRoleDao;
        jwtValidator = aJwtValidator;
        configuration = aConfiguration;
        fieldValidator = aFieldValidator;
        emailService = aEmailService;
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

        if( userDataDao.findByLogin( aRegisterRequestDto.getLogin() )
            .isPresent() )
        {
            throw new LoginExistsInDbException( STATUS_LOGIN_ALREADY_EXISTS );
        }
        else if( userDataDao.findByEmail( aRegisterRequestDto.getEmail() )
            .isPresent() )
        {
            throw new EmailExistsInDbException( STATUS_EMAIL_ALREADY_EXISTS );
        }
        else
        {
            UserDataEntity newUser = new UserDataEntity( aRegisterRequestDto, aUserRolesList.stream()
                .map( role -> userRoleDao.findByRoleName( role.getName() ) )
                .collect( Collectors.toSet() ), generateUuidToken() );
            newUser.setEmailVerificationTokenExpirationDate( Instant.now()
                .plus( configuration.getEMAIL_VERIFICATION_TOKEN_EXPIRATION(), ChronoUnit.MILLIS ) );
            userDataDao.save( newUser );
            emailService.sendEmailVerification( newUser );
        }
    }

    /**
     * Function changes users password if given old password was correct.
     */
    @Transactional
    public void changeUserPassword( String aAuthorizationToken,
        PasswordChangeRequestDto aPasswordChangeRequestDto )
    {
        fieldValidator.checkRequiredField( aPasswordChangeRequestDto.getNewPassword() );
        fieldValidator.checkRequiredField( aPasswordChangeRequestDto.getOldPassword() );
        var user = findUserByAuthorizationToken( aAuthorizationToken );
        checkLockAndUnlockIfLockTimeExpired( user );
        if( user.isEmailVerified() )
        {
            if( checkPassword( user, aPasswordChangeRequestDto.getOldPassword() ) )
            {
                fieldValidator.validatePassword( aPasswordChangeRequestDto.getNewPassword() );
                String hashedNewPassword =
                    BCrypt.hashpw( aPasswordChangeRequestDto.getNewPassword(), user.getSalt() );
                user.setPassword( hashedNewPassword );
                userDataDao.save( user );
            }
            else
            {
                throw new WrongPasswordException( STATUS_WRONG_PASSWORD );
            }
        }
        else
        {
            throw new ServiceException( STATUS_EMAIL_NOT_CONFIRMED );
        }
    }

    public LoginResponseDto logIntoSystemAttempt( LoginRequestDto aLoginRequestDto )
    {
        var user = findUserByLogin( aLoginRequestDto.getLogin() );
        checkLockAndUnlockIfLockTimeExpired( user );
        boolean checkPass = checkPassword( user, aLoginRequestDto.getPassword() );

        if( checkPass )
        {
            String authToken = jwtValidator.generateJwt( user );
            userDataDao.resetFailedAttempts( user );
            Instant lastLoggedUserDateTime = user.getLastLoggedTime();
            user.reloadLastLogTime( Instant.now() );
            userDataDao.save( user );

            return new LoginResponseDto( user.getEmail(), authToken, user.isEmailVerified(),
                Instant.ofEpochSecond( configuration.getTOKEN_EXPIRATION_TIME() ), lastLoggedUserDateTime,
                user.getRoles()
                    .stream()
                    .map( RoleEntity::getName )
                    .collect( Collectors.toSet() ) );
        }
        else if( user.getFailedAttempt() < configuration.getMAX_LOGIN_ATTEMPT() )
        {
            userDataDao.increaseFailedAttempts( user );
            throw new WrongPasswordException( STATUS_WRONG_PASSWORD );
        }
        else
        {
            if( Objects.equals( user.getFailedAttempt(), configuration.getMAX_LOGIN_ATTEMPT() ) )
                userDataDao.lockAccount( user );
            throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
        }
    }

    @Transactional
    public void requestPasswordReset( ResetPasswordRequestDto request )
    {
        var userEntity = findUserByEmail( request.getEmail() );
        checkLockAndUnlockIfLockTimeExpired( userEntity );
        if( userEntity.isLocked() )
        {
            throw new AccountLockedException( STATUS_ACCOUNT_LOCKED );
        }
        else if( !userEntity.isEmailVerified() )
        {
            throw new ServiceException( STATUS_EMAIL_NOT_CONFIRMED );
        }
        else
        {
            String generatedUuidResetPasswordToken = generateUuidToken();
            userEntity.setResetPasswordToken( generatedUuidResetPasswordToken );
            userEntity.setResetPasswordTokenExpirationDate( Instant.now()
                .plus( configuration.getRESET_PASSWORD_TOKEN_EXPIRATION(), ChronoUnit.MILLIS ) );
            emailService.sendResetPasswordEmail( userEntity );
            userDataDao.save( userEntity );
        }
    }

    public void resetPassword( ResetPasswordRequestDto request )
    {
        var userEntity = findUserByEmail( request.getEmail() );
        if( userEntity.isEmailVerified() )
        {
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
            throw new ServiceException( STATUS_EMAIL_NOT_CONFIRMED );
        }
    }

    public void confirmEmail( String aAuthorizationToken, String aEmailVerificationCode )
    {
        UserDataEntity foundUser = findUserByAuthorizationToken( aAuthorizationToken );
        if( !foundUser.isEmailVerified() )
        {
            if( foundUser.getEmailVerificationToken()
                .equals( aEmailVerificationCode ) )
            {
                if( foundUser.getEmailVerificationTokenExpirationDate()
                    .isAfter( Instant.now() ) )
                {
                    foundUser.setEmailVerificationTokenExpirationDate( null );
                    foundUser.setEmailVerified( true );
                    userDataDao.save( foundUser );
                }
                else
                {
                    throw new ServiceException( STATUS_EMAIL_VERIFICATION_TOKEN_EXPIRED );
                }
            }
            else
            {
                throw new ServiceException( STATUS_EMAIL_VERIFICATION_TOKEN_NOT_VALID );
            }
        }
        else
        {
            throw new ServiceException( STATUS_EMAIL_ALREADY_VERIFIED );
        }
    }

    public void sendConfirmEmailRequest( String aAuthorizationToken )
    {
        UserDataEntity foundUser = findUserByAuthorizationToken( aAuthorizationToken );
        if( !foundUser.isEmailVerified() )
        {
            foundUser.setEmailVerificationTokenExpirationDate( Instant.now()
                .plus( configuration.getEMAIL_VERIFICATION_TOKEN_EXPIRATION(), ChronoUnit.MILLIS ) );
            foundUser.setEmailVerificationToken( generateUuidToken() );
            emailService.sendEmailVerification( foundUser );
            userDataDao.save( foundUser );
        }
        else
        {
            throw new ServiceException( STATUS_EMAIL_ALREADY_VERIFIED );
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

    public UserDataEntity findUserByAuthorizationToken( String aAuthorizationToken )
    {
        fieldValidator.checkRequiredField( aAuthorizationToken );
        Optional< UserDataEntity > user =
            userDataDao.findByLogin( jwtValidator.extractLoginFromToken( aAuthorizationToken ) );
        if( user.isPresent() )
        {
            return user.get();
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_FROM_TOKEN );
        }
    }

    public UserDataEntity findUserByEmail( String aEmail )
    {
        fieldValidator.checkRequiredField( aEmail );
        Optional< UserDataEntity > user = userDataDao.findByEmail( aEmail );
        if( user.isPresent() )
        {
            return user.get();
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_EMAIL );
        }
    }

    public UserDataEntity findUserByLogin( String aLogin )
    {
        fieldValidator.checkRequiredField( aLogin );
        Optional< UserDataEntity > user = userDataDao.findByLogin( aLogin );
        if( user.isPresent() )
        {
            return user.get();
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_BY_LOGIN );
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
        return password != null && BCrypt.checkpw( password, user.getPassword() );
    }

    private String generateUuidToken()
    {
        return UUID.randomUUID()
            .toString();
    }
}
