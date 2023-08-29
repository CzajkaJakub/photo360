package pl.put.photo360.shared.exception;

import java.util.Objects;

/**
 * A basic exception for all "unexpected" behaviors. This is just a wrapper for an actual exception, so always
 * check {@link RepositoryException#getCause()} for more details.
 */
public class RepositoryException extends RuntimeException
{
    public RepositoryException( String aMessage, Throwable aCause )
    {
        super( aMessage, aCause );
        Objects.requireNonNull( aCause );
        Objects.requireNonNull( aMessage );
    }
}
