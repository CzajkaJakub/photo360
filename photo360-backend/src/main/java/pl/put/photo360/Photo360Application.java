package pl.put.photo360;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360Application
{
    private static final Logger logger = LoggerFactory.getLogger( Photo360Application.class );

    public static void main( String[] args )
    {
        logger.info( "----------------------------- PROPERTIES -----------------------------" );
        logger.info( "Rest api documentation yaml version : http://localhost:8095/v3/api-docs.yaml" );
        logger.info( "Rest api documentation yaml version reader: https://editor.swagger.io/" );
        logger.info( "Rest api documentation : http://localhost:8095/swagger-ui/index.html#/" );
        logger.info( "Springboot version  : " + SpringBootVersion.getVersion() );
        logger.info( "Spring version  : " + SpringVersion.getVersion() );
        SpringApplication.run( Photo360Application.class, args );
    }
}