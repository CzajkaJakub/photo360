package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import pl.put.photo360.service.RequestService;

public class LoginSceneController extends SwitchSceneController {

    public final RequestService requestService;

    @Autowired
    public LoginSceneController(RequestService requestService) {
        this.requestService = requestService;
    }


}
