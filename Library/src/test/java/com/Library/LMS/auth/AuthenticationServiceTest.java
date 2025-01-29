package com.Library.LMS.auth;

import com.Library.LMS.Mapping.UserMapper;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.Security.JwtService;
import com.Library.LMS.auth.model.AuthenticationRequest;
import com.Library.LMS.auth.model.AuthenticationResponse;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.UserDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testAuthenticateSuccess() {

        String email = "john.doe@example.com";
        String password = "password";
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        UserEntity user = new UserEntity(1L, "John Doe", email, password, Role.USER, "John1Doe", null);
        String jwtToken = "jwtToken";


        Map<String, Object> claim = new HashMap<>();
        claim.put("Role", "ROLE_USER");


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        when(jwtService.generateToken(claim, user)).thenReturn(jwtToken);


        AuthenticationResponse response = authenticationService.authenticate(request);


        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtService, times(1)).generateToken(claim, user);
    }



    @Test
    void testAuthenticateUserNotFound() {
        
        String email = "john.doe@example.com";
        String password = "password";
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("User with Email john.doe@example.com not found.", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testRegisterUserSuccess() {
        
        UserRegister request = new UserRegister("John Doe", "john.doe@example.com", "John1Doe", "password");
        UserDTO userDTO = new UserDTO(1L, "John Doe", "john.doe@example.com", "John1Doe", Role.USER, null);
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password", Role.USER, "John1Doe", null);


        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.findByLibraryId(request.getLibraryId())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);


        UserDTO result = authenticationService.register(request);

        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testRegisterUserConflict() {

        UserRegister request = new UserRegister("John Doe", "john.doe@example.com", "John1Doe", "password");


        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);


        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
            authenticationService.register(request);
        });


        assertEquals("User with Email john.doe@example.com Already exist.", exception.getMessage());
        verify(userRepository, never()).save(any());  
    }
}
