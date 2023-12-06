package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;

@Component
public class SceneriosSceneController extends SwitchSceneController {
    @Autowired
    public SceneriosSceneController(RequestService requestService, AuthHandler authHandler, Configuration configuration) {
        super(requestService, authHandler, configuration);
    }
}
