package com.Library.LMS.auth;

import com.Library.LMS.Mapping.UserMapper;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.Security.JwtService;
import com.Library.LMS.auth.model.AuthenticationRequest;
import com.Library.LMS.auth.model.AuthenticationResponse;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.UserDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // Service in charge of create new user and authenticate it.


    private final AuthenticationManager authenticationManager; // Use for authentication process

    private final UserRepository userRepository;

    private final JwtService jwtService; // Generates a JWT token during login/authentication, including the user's roles.

    private final UserMapper userMapper;

    // Returns User JWT, Needed to access others endpoints.
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with Email "+request.getEmail()+" not found."));
        Map<String, Object> claim = new HashMap<>();
        claim.put("Role", "ROLE_" + user.getRole());

        String jwtToken = jwtService.generateToken(claim,user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    // Returns the UserDTO with sanitized data to confirm the user's information.
    public UserDTO register(UserRegister request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceConflictException("User with Email " + request.getEmail() + " Already exist.");
        }
        if(userRepository.findByLibraryId(request.getLibraryId()).isPresent()){
            throw new ResourceConflictException("User with LibraryId " + request.getLibraryId() + " Already exist.");
        }
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(request)));
    }
}
