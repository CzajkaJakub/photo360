package pl.put.photo360.shared.fieldValidator;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_WRONG_FORMAT;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FIELD_SIZE;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.exception.FieldValidationException;

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
        validateEmailFormat( aRegisterRequestDto.getEmail() );
    }

    public void validateLogin( String aFieldToValidate )
    {
        checkWhiteMarks( aFieldToValidate );
    }

    public void validatePassword( String aFieldToValidate )
    {
        checkWhiteMarks( aFieldToValidate );
        checkFieldSize( aFieldToValidate );
    }

    private void checkFieldSize( String aFieldToValidate )
    {
        aFieldToValidate = aFieldToValidate.replaceAll( "\\s+", " " );
        if( aFieldToValidate.length() < configuration.getMIN_REGISTER_PASSWORD_LENGTH()
            || aFieldToValidate.length() > configuration.getMAX_REGISTER_FIELD_LENGTH() )
        {
            throw new FieldValidationException( STATUS_WRONG_FIELD_SIZE );
        }
    }

    private void checkWhiteMarks( String aFieldToValidate )
    {
        if( StringUtils.containsWhitespace( aFieldToValidate ) )
        {
            throw new FieldValidationException( STATUS_FIELD_CONTAINS_WHITESPACES );
        }
    }

    public void validateEmailFormat( String aEmailToValidate )
    {
        if( !patternMatches( aEmailToValidate, configuration.getEMAIL_REGEX() ) )
        {
            throw new FieldValidationException( STATUS_EMAIL_WRONG_FORMAT );
        }
    }

    private boolean patternMatches( String emailAddress, String aRegexPattern )
    {
        return Pattern.compile( aRegexPattern )
            .matcher( emailAddress )
            .matches();
    }
}