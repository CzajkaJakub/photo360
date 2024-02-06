package pl.put.photo360.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;

@Component
public class InformationsSceneController extends SwitchSceneController
{
    @Autowired
    public InformationsSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow )
    {
        super( requestService, authHandler, configuration, configURL, cameraWindow );
    }
}
