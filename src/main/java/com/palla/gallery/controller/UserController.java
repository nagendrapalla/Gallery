package com.palla.gallery.controller;

import com.palla.gallery.dto.UserDto;
import com.palla.gallery.entity.User;
import com.palla.gallery.exception.InvalidCredentialsException;
import com.palla.gallery.service.JwtService;
import com.palla.gallery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto response = userService.saveUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) throws InvalidCredentialsException, AuthenticationException {
        UsernamePasswordAuthenticationToken validate = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(validate);
        if (authentication.isAuthenticated()) {
            return new ResponseEntity<>(jwtService.generateToken(user.getUsername()), HttpStatus.OK);
        } else {
            throw new InvalidCredentialsException("Invalid user request");
        }
    }

}
