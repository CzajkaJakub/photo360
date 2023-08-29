package pl.put.photo360.shared.fieldValidator;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_WRONG_FORMAT;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FIELD_SIZE;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.exception.FieldValidationException;

public class FieldValidator
{
    private static final int MIN_REGISTER_PASSWORD_LENGTH = 12;
    private static final int MAX_REGISTER_FIELD_LENGTH = 128;
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static void validateRegisterForm( RegisterRequestDto aRegisterRequestDto )
        throws NoSuchAlgorithmException, IOException
    {
        validateLogin( aRegisterRequestDto.getLogin() );
        validatePassword( aRegisterRequestDto.getPassword() );
        validateEmailFormat( aRegisterRequestDto.getEmail() );
    }

    public static void validateLogin( String aFieldToValidate )
    {
        checkWhiteMarks( aFieldToValidate );
    }

    public static void validatePassword( String aFieldToValidate )
        throws NoSuchAlgorithmException, IOException
    {
        checkWhiteMarks( aFieldToValidate );
        checkFieldSize( aFieldToValidate );
    }

    private static void checkFieldSize( String aFieldToValidate )
    {
        aFieldToValidate = aFieldToValidate.replaceAll( "\\s+", " " );
        if( aFieldToValidate.length() < MIN_REGISTER_PASSWORD_LENGTH
            || aFieldToValidate.length() > MAX_REGISTER_FIELD_LENGTH )
        {
            throw new FieldValidationException( STATUS_WRONG_FIELD_SIZE );
        }
    }

    private static void checkWhiteMarks( String aFieldToValidate )
    {
        if( StringUtils.containsWhitespace( aFieldToValidate ) )
        {
            throw new FieldValidationException( STATUS_FIELD_CONTAINS_WHITESPACES );
        }
    }

    public static void validateEmailFormat( String aEmailToValidate )
    {
        if( !patternMatches( aEmailToValidate, EMAIL_REGEX ) )
        {
            throw new FieldValidationException( STATUS_EMAIL_WRONG_FORMAT );
        }
    }

    private static boolean patternMatches( String emailAddress, String aRegexPattern )
    {
        return Pattern.compile( aRegexPattern )
            .matcher( emailAddress )
            .matches();
    }
}
