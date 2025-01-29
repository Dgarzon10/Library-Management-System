package com.Library.LMS.Service.Imp;

import com.Library.LMS.Mapping.UserMapper;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.auth.AuthenticationService;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.UserDTO;
import com.Library.LMS.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;



    @Test
    void testGetById() {
        Long id = 1L;
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", Collections.emptyList());
        UserDTO userDTO = new UserDTO(1L, "John Doe", "john.doe@example.com","John1Doe",Role.USER , Collections.emptyList());
        UserRegister userRegister = new UserRegister( "John Doe", "john.doe@example.com","John1Doe", "password");

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.getById(id);

        assertNotNull(result);
        assertEquals(userRegister.getName(), result.getName());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteUser() {
        Long id = 1L;
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", Collections.emptyList());

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        userService.delete(id);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteUserWithBorrowings_ShouldThrowException() {
        Long id = 1L;
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", List.of(mock(BorrowingEntity.class)));

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        assertThrows(ResourceConflictException.class, () -> userService.delete(id));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void testUpdateUser() {
        Long id = 1L;
        UserRegister userRegister = new UserRegister( "John Doe", "john.doe@example.com","John1Doe", "password");
        UserDTO userDTO = new UserDTO(1L, "Updated Name", "john.doe@example.com","John1Doe",Role.USER , Collections.emptyList());
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", Collections.emptyList());

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        doAnswer(invocation -> {
            UserDTO source = invocation.getArgument(0);
            userEntity.setName(source.getName());
            userEntity.setEmail(source.getEmail());
            return null;
        }).when(userMapper).updateUserFromDTO(userDTO, userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.update(id, userDTO);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testGetAllUsers() {
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", Collections.emptyList());
        UserDTO userDTO = new UserDTO(1L, "John Doe", "john.doe@example.com","John1Doe",Role.USER , Collections.emptyList());
        UserRegister userRegister = new UserRegister( "John Doe", "john.doe@example.com","John1Doe", "password");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userRegister.getEmail(), result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetByEmail() {
        String email = "john.doe@example.com";
        UserEntity userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", "password",  Role.USER, "John1Doe", Collections.emptyList());
        UserDTO userDTO = new UserDTO(1L, "John Doe", "john.doe@example.com","John1Doe",Role.USER , Collections.emptyList());
        UserRegister userRegister = new UserRegister( "John Doe", "john.doe@example.com","John1Doe", "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.getByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }
}