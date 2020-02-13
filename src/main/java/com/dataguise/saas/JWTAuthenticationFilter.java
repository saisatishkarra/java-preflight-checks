package com.dataguise.saas;

import com.dataguise.saas.service.JWTService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = JWTService.getAuthentication((HttpServletRequest)request);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        if (authentication != null) {
            JWTService.addAuthentication((HttpServletResponse)response, authentication.getName());
        }

        filterChain.doFilter(request,response);
    }
}

