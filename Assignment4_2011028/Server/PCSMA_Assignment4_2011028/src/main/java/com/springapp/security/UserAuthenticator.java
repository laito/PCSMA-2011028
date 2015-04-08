package com.springapp.security;


import com.google.gson.Gson;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */

public class UserAuthenticator extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = this.getLoginRequest(request);
        System.out.println("OOOH LOGIGINGINGINGING");
        System.out.println(loginRequest.getPassword());
        System.out.println(loginRequest.getUsername());
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        setDetails(request, authRequest);
        System.out.println(this.getAuthenticationManager().authenticate(authRequest).isAuthenticated());
        
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {
        BufferedReader reader = null;
        LoginRequest loginRequest = null;
        try {
            reader = request.getReader();
            Gson gson = new Gson();
            loginRequest = gson.fromJson(reader, LoginRequest.class);
        } catch (IOException ex) {
            Logger.getLogger(UserAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(UserAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (loginRequest == null) {
            loginRequest = new LoginRequest();
        }

        return loginRequest;
    }

    public static class LoginRequest {
        public String username;
        public String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
