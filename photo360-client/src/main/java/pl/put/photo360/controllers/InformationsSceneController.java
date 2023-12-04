package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.put.photo360.service.RequestService;

@Controller
public class InformationsSceneController extends SwitchSceneController {
    @Autowired
    public InformationsSceneController(RequestService requestService) {
        super(requestService);
    }
}
