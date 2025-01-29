package com.Library.LMS.Service.Imp;


import com.Library.LMS.Mapping.UserMapper;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.Service.GenericService;
import com.Library.LMS.dto.UserDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService implements GenericService<UserDTO, Long> {

    // Service responsible for managing user data. It interacts with the UserRepository for CRUD operations
    // and ensures proper data handling and business logic. Authentication logic is handled elsewhere.


    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Override
    public UserDTO update(Long id, UserDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
        userMapper.updateUserFromDTO(dto,user);
        return userMapper.toDTO(userRepository.save(user));
    }
    public void updateToAdmin(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
        user.setRole(Role.ADMIN);
        userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
        if(user.getBorrowings().size() != 0){
            throw new ResourceConflictException("Can not Delete a user with a borrow, User with ID "+id+" has a borrow");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));

    }

    public UserDTO getByEmail(String email){
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with Email " + email + " not found."));
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }
}