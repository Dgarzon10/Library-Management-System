package com.Library.LMS.Mapping;

import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.dto.BookDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Ensure that only safe data is exposed by transforming the Entity into a DTO.
    public BookDTO toDTO(BookEntity entity){
        return BookDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .isbn(entity.getIsbn())
                .author(entity.getAuthor())
                .availability_status(entity.isAvailability_status())
                .borrowingIds(entity.getBorrowings().stream()
                        .map(BorrowingEntity::getId)
                        .toList())
                .build();
    }

    public BookEntity toEntity(BookDTO dto){

        return BookEntity.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .availability_status(dto.isAvailability_status())
                .isbn(dto.getIsbn())
                .build();
    }

    // Only the fields provided by the user will be updated to ensure that only those are changed.
    public void updateBookFromDTO(BookDTO dto, BookEntity entity){
        if(dto.getTitle() != null){
            entity.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null){
            entity.setAuthor(dto.getAuthor());
        }
        if(dto.getIsbn() != null){
            entity.setIsbn(dto.getIsbn());
        }
    }
}
