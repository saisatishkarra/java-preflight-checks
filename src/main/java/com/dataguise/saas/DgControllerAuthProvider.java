package com.dataguise.saas;

import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.ErrorConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DgControllerAuthProvider implements AuthenticationProvider {
    @Autowired
    private DgSecureRestDriver restDriver;
    private Logger logger = Logger.getLogger(DgControllerAuthProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        Object password = authentication.getCredentials();

        // Null check on object type to avoid null pointer exception on toString()
        if (password == null && "".equals(name)) {
            throw new AuthenticationCredentialsNotFoundException(ErrorConstants.ERROR_EMPTY_CREDENTIAL.getErrorMessage());
        }

        logger.info("Login request for name: " + name);
        if (password != "" && name != "" && attemptDgSecureLogin(name, password.toString())) {
            logger.info("Login successful");
            return new UsernamePasswordAuthenticationToken(
                name, password, new ArrayList<>());
        }
        //For loginWithAuth0Token from gateway we will pass only token as username but not password
        else if (password == "" && name!= "" && attemptDgSecureSSOLogin(name)) {
            logger.info("Login successful");
            return new UsernamePasswordAuthenticationToken(
                name, password, new ArrayList<>());
        } else {
            logger.info("Login failed");
            throw new BadCredentialsException(ErrorConstants.LOGIN_FAIL.getErrorMessage());
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }

    private boolean attemptDgSecureLogin(String username, String password) {
        boolean loginSuccess = false;
        try {
            restDriver.login(username, password);
            loginSuccess = true;
        } catch (DgDrvException e) {
            e.printStackTrace();
        }
        return loginSuccess;
    }

    private boolean attemptDgSecureSSOLogin(String token) {
        boolean loginSuccess = false;
        try {
            restDriver.loginWithAuth0AccessToken(token);
            loginSuccess = true;
        } catch (DgDrvException e) {
            e.printStackTrace();
        }
        return loginSuccess;
    }

    @Override
    public String toString() {
        return "DgControllerAuthProvider{" +
                "restDriver=" + restDriver +
                '}';
    }
}
