package pl.put.photo360.shared.exception;

import java.util.List;
import java.util.Objects;

import springfox.documentation.builders.ValidationResult;

/**
 * A simple Timeseries validation exception.
 */
public class ValidationException extends RuntimeException
{
    private final List< ValidationResult > validationResult;

    public ValidationException( String aMessage, List< ValidationResult > aValidationResults )
    {
        super( aMessage );
        Objects.requireNonNull( aValidationResults );
        validationResult = aValidationResults;
    }

    public List< ValidationResult > getValidationResult()
    {
        return this.validationResult;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
