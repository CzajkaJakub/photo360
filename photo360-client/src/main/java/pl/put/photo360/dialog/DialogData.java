package pl.put.photo360.dialog;

public class DialogData
{
    private boolean accessType; // true dla publicznego, false dla prywatnego
    private String title;
    private String description;

    public DialogData( boolean accessType, String title, String description )
    {
        this.accessType = accessType;
        this.title = title;
        this.description = description;
    }

    public boolean isAccessTypePublic()
    {
        return accessType;
    }

    public void setAccessType( boolean accessType )
    {
        this.accessType = accessType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
}
