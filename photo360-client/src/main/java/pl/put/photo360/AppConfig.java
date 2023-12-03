package pl.put.photo360;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.put.photo360.controllers.ControllerConfig;
import pl.put.photo360.service.ServiceConfig;


@Configuration
@Import({
        ControllerConfig.class,
        ServiceConfig.class
})
public class AppConfig {}