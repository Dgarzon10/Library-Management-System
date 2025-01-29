package com.Library.LMS.Mapping;

import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.dto.BorrowingDTO;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {

    // Ensure that only safe data is exposed by transforming the Entity into a DTO.
    public BorrowingDTO toDTO(BorrowingEntity entity) {
        return BorrowingDTO.builder()
                .id(entity.getId())
                .bookId(entity.getBook() != null ? entity.getBook().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .borrowDate(entity.getBorrowDate())
                .returnDate(entity.getReturnDate())
                .dueDate(entity.getDueDate())
                .build();
    }

    public List<BorrowingDTO> toDTOList(List<BorrowingEntity> borrowings) {
        if (borrowings == null || borrowings.isEmpty()) {
            return Collections.emptyList();
        }

        return borrowings.stream()
                .map(this::toDTO)
                .toList();
    }
}
