package com.dataguise.saas.controllers;

import com.dataguise.saas.exception.BadRequestException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dataguise.saas.dto.LoginDTO;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/")
public class LoginController {
    private Logger logger = Logger.getLogger(LoginController.class);
    @Autowired private DgSecureRestDriver restDriver;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginDTO loginDto) throws BadRequestException {
        String sessionId = "";
        if(loginDto.getUsername() == null) {
            throw new BadRequestException("username must be specified in body", HttpStatus.BAD_REQUEST);
        }
        if(loginDto.getPassword() == null) {
            throw new BadRequestException("password must be specified in body", HttpStatus.BAD_REQUEST);
        }
        try {
            sessionId = restDriver.login(loginDto.getUsername(), loginDto.getPassword());
        } catch (DgDrvException e) {
            logger.error("com.dataguise.saas.Exception while trying to log in:", e);
            return new ResponseEntity<String>("Login failure", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<String>(sessionId, HttpStatus.OK);
    }

    @RequestMapping(value = "loginSSO", method = RequestMethod.GET)
    public ResponseEntity loginSSO(@RequestParam("token") String token) throws BadRequestException {
        String sessionId = "";
        if(token == null) {
            throw new BadRequestException("access token must be specified in request param", HttpStatus.BAD_REQUEST);
        }

        try {
            sessionId = restDriver.loginWithAuth0AccessToken(token);
        } catch (DgDrvException e) {
            logger.error("com.dataguise.saas.Exception while trying to log in:", e);
            return new ResponseEntity<String>("Login failure", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<String>(sessionId, HttpStatus.OK);
    }
}
