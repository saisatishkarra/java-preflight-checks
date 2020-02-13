package com.dataguise.saas.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static java.util.Collections.emptyList;

public class JWTService {

    static final Logger logger = Logger.getLogger(JWTService.class);
    static final long EXPIRATIONTIME = 300000; // 5minutes, can be reduced after initial tests
    static final String SECRET = "k5v2nEkDNROaTYK7k96g6ZKofmLsFMOX"; // This should be externalized, but depends on other encryption infrastructure and would most probably need DgController support
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    public static void addAuthentication(HttpServletResponse res, String username) throws IOException {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader("Access-Control-Allow-Headers", HEADER_STRING);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
//        try {
//            res.getWriter().write(username);
//        } catch (IOException e) {
//            logger.error("Authentication successful. Error writing response." + e);
//            throw e;
//        }
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}