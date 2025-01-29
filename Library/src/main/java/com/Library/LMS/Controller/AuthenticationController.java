package com.Library.LMS.Controller;

import com.Library.LMS.auth.model.AuthenticationRequest;
import com.Library.LMS.auth.model.AuthenticationResponse;
import com.Library.LMS.auth.AuthenticationService;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // These are public endpoints, allowing users to register in the system and log in.

    private final AuthenticationService service;

    // POST /api/v1/auth/register - register a new user in the system.
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegister request){
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }
    // POST /api/v1/auth/authenticate - authenticate the user in the system.
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }
}
