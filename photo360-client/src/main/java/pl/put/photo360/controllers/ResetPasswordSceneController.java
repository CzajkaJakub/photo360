package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.service.AuthHandler;
import pl.put.photo360.service.RequestService;

@Component
public class ResetPasswordSceneController extends SwitchSceneController {
    @Autowired
    public ResetPasswordSceneController(RequestService requestService, AuthHandler authHandler) {
        super(requestService, authHandler);
    }
}
