package pl.put.photo360;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360JavaFxApplication
{
    public static void main( String[] args )
    {
        Application.launch( Photo360client.class, args );
    }
}
