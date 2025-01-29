package com.Library.LMS.Persistence.Repository;

import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<BorrowingEntity, Long> {

    Optional<List<BorrowingEntity>> findByUserId(Long userId);
    Optional<List<BorrowingEntity>> findByBookId(Long bookId);
    Integer countByUserIdAndReturnDateIsNull(Long userId);
}
