package pl.put.photo360;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.interceptors.RestTemplateFilter;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360JavaFxApplication
{
    public static void main( String[] args )
    {
        Application.launch( Photo360client.class, args );
    }

    @Bean
    public RestTemplate restTemplate(Configuration configuration) {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateFilter(configuration));
        return restTemplate;
    }
}
