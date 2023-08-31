package pl.put.photo360.controller;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_OK;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping( "/photo360" )
@RestController( "SystemController" )
public class SystemController
{
    @GetMapping( "/test" )
    public ResponseEntity< Object > getTest()
    {
        return new ResponseEntity<>( "siema", STATUS_OK.getStatus() );
    }
}
