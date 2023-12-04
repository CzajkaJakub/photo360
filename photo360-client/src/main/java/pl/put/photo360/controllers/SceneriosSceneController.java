package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.put.photo360.service.RequestService;

@Controller
public class SceneriosSceneController extends SwitchSceneController {
    @Autowired
    public SceneriosSceneController(RequestService requestService) {
        super(requestService);
    }
}
