package pl.put.photo360.fieldValidator;

import java.util.regex.Pattern;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.RegisterRequestDto;
import pl.put.photo360.dto.ServerResponseCode;
import pl.put.photo360.exception.FieldValidationException;
import pl.put.photo360.exception.MissingRequiredFieldsException;

@Component
public class FieldValidator
{
    private final Configuration configuration;

    @Autowired
    public FieldValidator( Configuration aConfiguration )
    {
        configuration = aConfiguration;
    }

    public void validateRegisterForm( RegisterRequestDto aRegisterRequestDto )
    {
        validateLogin( aRegisterRequestDto.getLogin() );
        validatePassword( aRegisterRequestDto.getPassword() );
        validateEmail( aRegisterRequestDto.getEmail() );
    }

    public void validateLoginForm( LoginRequestDto aLoginRequestDto )
    {
        checkRequiredField( aLoginRequestDto.getLogin() );
        checkRequiredField( aLoginRequestDto.getPassword() );
    }

    public void validateEmail( String aFieldToValidate )
    {
        checkRequiredField( aFieldToValidate );
        validateEmailFormat( aFieldToValidate );
    }

    public void validateLogin( String aFieldToValidate )
    {
        checkWhiteMarks( aFieldToValidate );
        checkRequiredField( aFieldToValidate );
    }

    public void validatePassword( String aFieldToValidate )
    {
        checkRequiredField( aFieldToValidate );
        checkWhiteMarks( aFieldToValidate );
        checkFieldSize( aFieldToValidate );
    }

    private void checkFieldSize( String aFieldToValidate )
    {
        aFieldToValidate = aFieldToValidate.replaceAll( "\\s+", " " );
        if( aFieldToValidate.length() < configuration.getMIN_REGISTER_PASSWORD_LENGTH()
            || aFieldToValidate.length() > configuration.getMAX_REGISTER_FIELD_LENGTH() )
        {
            throw new FieldValidationException( ServerResponseCode.STATUS_WRONG_FIELD_SIZE );
        }
    }

    public void checkRequiredField( String aFieldToValidate )
    {
        if( aFieldToValidate == null )
        {
            throw new MissingRequiredFieldsException( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );
        }
    }

    private void checkWhiteMarks( String aFieldToValidate )
    {
        if( StringUtils.containsWhitespace( aFieldToValidate ) )
        {
            throw new FieldValidationException( ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES );
        }
    }

    public void validateEmailFormat( String aEmailToValidate )
    {
        if( !patternMatches( aEmailToValidate, configuration.getEMAIL_REGEX() ) )
        {
            throw new FieldValidationException( ServerResponseCode.STATUS_EMAIL_WRONG_FORMAT );
        }
    }

    private boolean patternMatches( String emailAddress, String aRegexPattern )
    {
        return Pattern.compile( aRegexPattern )
            .matcher( emailAddress )
            .matches();
    }
}
