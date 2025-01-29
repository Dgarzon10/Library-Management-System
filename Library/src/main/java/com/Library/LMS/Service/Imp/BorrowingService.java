package com.Library.LMS.Service.Imp;


import com.Library.LMS.Mapping.BorrowingMapper;
import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.BookRepository;
import com.Library.LMS.Persistence.Repository.BorrowingRepository;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.dto.BorrowingDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BorrowingService {

    // Service responsible for managing borrowing. It uses the BorrowingRepository to interact with the database
    // and perform operations on borrowing data, and business logic implementation.

    private final BorrowingRepository borrowingRepository;
    private final BorrowingMapper borrowingMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final UserService userService;



    public BorrowingDTO borrow(Long userId, Long bookId){

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found."));

        validateBorrowing(user,book);

        BorrowingEntity borrowingEntity = BorrowingEntity.builder()
                .book(book)
                .user(user)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        bookService.markAsBorrowed(bookId);
        book.addBorrowing(borrowingEntity);
        user.addBorrowing(borrowingEntity);

        return borrowingMapper.toDTO(borrowingRepository.save(borrowingEntity));
    }

    public void returnBook(Long borrowingId){
         BorrowingEntity borrowing = borrowingRepository.findById(borrowingId)
                 .orElseThrow(() -> new ResourceNotFoundException("Borrowing with ID " + borrowingId + " not found."));         UserEntity user = borrowing.getUser();
         user.removeBorrowing(borrowing);
         borrowing.setReturnDate(LocalDate.now());
         bookService.markAsAvailable(borrowing.getBook().getId());
         borrowingRepository.save(borrowing);

    }

    private void validateBorrowing(UserEntity user, BookEntity book){
        if(!book.isAvailability_status()){
            throw new ResourceConflictException("Book with ID " + book.getId() + " is not available.");
        }
        int activeBorrowings = borrowingRepository.countByUserIdAndReturnDateIsNull(user.getId());
        if(activeBorrowings >= 3){
            throw new ResourceConflictException("User has reached the maximum number of borrowings.");
        }
    }

    public BorrowingDTO getBorrowingById(Long borrowingId) {
        return borrowingRepository.findById(borrowingId)
                .map(borrowingMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing with ID " + borrowingId + " not found."));
    }

    public List<BorrowingDTO> getAllBorrowings(Optional<Long> userId, Optional<Long> bookId) {
        List<BorrowingEntity> borrowings;
        if (userId.isPresent()){
            userRepository.findById(userId.get())

                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId.get() + " not found."));
            borrowings = borrowingRepository.findByUserId(userId.get()).get();

        } else if (bookId.isPresent()){
            bookRepository.findById(bookId.get())
                    .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId.get() + " not found."));
            borrowings = borrowingRepository.findByBookId(bookId.get()).get();

        } else {
            borrowings = borrowingRepository.findAll();

        }
        return borrowingMapper.toDTOList(borrowings);
    }
}
