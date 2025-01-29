package com.Library.LMS.Mapping;

import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    // Ensure that only safe data is exposed by transforming the Entity into a DTO.
    public UserDTO toDTO(UserEntity entity){
        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .libraryId(entity.getLibraryId())
                .role(entity.getRole())
                .borrowingIds(entity.getBorrowings().stream()
                        .filter(borrowingEntity -> borrowingEntity.getReturnDate() == null)
                        .map(BorrowingEntity::getId)
                        .toList())
                .build();
    }

    // Transform the user input into a DB entity to abstract and protect sensitive information like password.
    public UserEntity toEntity(UserRegister request){

        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .libraryId(request.getLibraryId())
                .role(Role.USER)
                .build();
    }

    public void updateUserFromDTO(UserDTO dto, UserEntity entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getLibraryId() != null) {
            entity.setLibraryId(dto.getLibraryId());
        }
    }
}
