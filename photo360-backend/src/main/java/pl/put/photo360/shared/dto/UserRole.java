package pl.put.photo360.shared.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents all the possible user's role.
 */
public enum UserRole
{
    // @formatter:off
    ADMIN_ROLE(1, "admin"),
    USER_ROLE(2, "user");
    // @formatter:on;

    private static final Map< Integer, UserRole > ROLES_MAP = new HashMap<>();
    static
    {
        for( UserRole role : values() )
        {
            ROLES_MAP.put( role.roleId, role );
        }
    }

    private final int roleId;
    private final String name;

    UserRole( int aRoleId, String aName )
    {
        roleId = aRoleId;
        name = aName;
    }

    /**
     * Gets user role.
     *
     * @param aRoleId
     *                    role's id
     * @return role's name
     */
    public static UserRole get( int aRoleId )
    {
        var role = ROLES_MAP.get( aRoleId );
        if( role == null )
        {
            throw new IllegalArgumentException( String.format( "No role with id %s found", aRoleId ) );
        }
        else
        {
            return role;
        }
    }

    public int getId()
    {
        return this.roleId;
    }

    public String getName()
    {
        return this.name;
    }
}
