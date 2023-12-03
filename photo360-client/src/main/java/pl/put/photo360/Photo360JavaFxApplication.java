package pl.put.photo360;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javafx.application.Application;
import okhttp3.OkHttpClient;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360JavaFxApplication
{

    public static void main( String[] args )
    {
        Application.launch( Photo360client.class, args );
    }

    @Bean
    public OkHttpClient okHttpClient()
    {
        return new OkHttpClient();
    }
}
