package pl.put.photo360.shared.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents all the possible user's role.
 */
public enum UserRoles
{
    // @formatter:off
    ADMIN_ROLE(1L, "ADMIN"),
    USER_ROLE(2L, "USER");
    // @formatter:on;

    private static final Map< Long, UserRoles > ROLES_MAP = new HashMap<>();
    static
    {
        for( UserRoles role : values() )
        {
            ROLES_MAP.put( role.roleId, role );
        }
    }

    private final Long roleId;
    private final String name;

    UserRoles( Long aRoleId, String aName )
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
    public static UserRoles get( Long aRoleId )
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

    public Long getId()
    {
        return this.roleId;
    }

    public String getName()
    {
        return this.name;
    }
}
