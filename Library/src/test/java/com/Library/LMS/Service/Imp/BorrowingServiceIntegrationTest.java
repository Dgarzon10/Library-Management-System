package com.Library.LMS.Service.Imp;

import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.BookRepository;
import com.Library.LMS.Persistence.Repository.BorrowingRepository;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.dto.BorrowingDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BorrowingServiceIntegrationTest {

    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @BeforeEach
    void setUp() {
        // Manual insert of an initial book
        BookEntity book = BookEntity.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn(123456789L)
                .availability_status(true)
                .build();
        bookRepository.save(book);

        // Manual insert of an initial user
        UserEntity user = UserEntity.builder()
                .email("Test Email")
                .name("Test Name")
                .libraryId("User1Test")
                .password("")
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void borrow_ShouldSaveBorrowingEntity_WhenSuccessful() {
        
        Long userId = userRepository.findAll().get(0).getId();
        Long bookId = bookRepository.findAll().get(0).getId();

        
        BorrowingDTO result = borrowingService.borrow(userId, bookId);

        Long userId2 = userRepository.findAll().get(0).getId();
        assertNotNull(userId2);

        
        assertNotNull(result);
        assertNull(result.getReturnDate());
        assertEquals(1, borrowingRepository.count());

        BorrowingEntity savedBorrowing = borrowingRepository.findAll().get(0);

        assertEquals(userId, savedBorrowing.getUser().getId());
        assertEquals(bookId, savedBorrowing.getBook().getId());
        assertEquals(LocalDate.now(), savedBorrowing.getBorrowDate());
        assertEquals(1,borrowingRepository.findByBookId(bookId).get().size());
    }

    @Test
    void borrow_ShouldThrowException_WhenBookNotAvailable() {
        
        Long userId = userRepository.findAll().get(0).getId();
        Long bookId = bookRepository.findAll().get(0).getId();

        BookEntity book = bookRepository.findById(bookId).orElseThrow();
        book.setAvailability_status(false);
        bookRepository.save(book);

         
        assertThrows(ResourceConflictException.class, () -> borrowingService.borrow(userId, bookId));
    }

    @Test
    void borrow_ShouldThrowException_WhenUserBorrowTooMuch(){
        BookEntity book2 = BookEntity.builder()
                .title("Test Title2")
                .author("Test Author2")
                .isbn(12322456789L)
                .availability_status(true)
                .build();
        bookRepository.save(book2);
        BookEntity book3 = BookEntity.builder()
                .title("Test Title3")
                .author("Test Author3")
                .isbn(12343356789L)
                .availability_status(true)
                .build();
        bookRepository.save(book3);
        BookEntity book4 = BookEntity.builder()
                .title("Test Title4")
                .author("Test Author4")
                .isbn(12344456789L)
                .availability_status(true)
                .build();
        bookRepository.save(book4);

        Long userId = userRepository.findAll().get(0).getId();
        Long bookId = bookRepository.findAll().get(0).getId();

        BorrowingDTO result = borrowingService.borrow(userId, bookId);
        BorrowingDTO result2 = borrowingService.borrow(userId, book2.getId());
        BorrowingDTO result3 = borrowingService.borrow(userId, book3.getId());


        assertThrows(ResourceConflictException.class, () -> borrowingService.borrow(userId, book4.getId()));
    }
    @Test
    void borrowReturn_shouldChangeStatusBook_WhenSuccess(){
        
        UserEntity user = userRepository.findByEmail("Test Email").get();
        BookEntity book = bookRepository.findAll().get(0);
        assertNotNull(user);
        assertNotNull(book);
        assertTrue(book.isAvailability_status(), "Book is Available from start");

        
        BorrowingDTO result = borrowingService.borrow(user.getId(), book.getId());
        borrowingService.returnBook(result.getId());

        
        assertEquals(1,borrowingRepository.findByBookId(book.getId()).get().size());
        assertTrue(bookRepository.findById(book.getId()).get().isAvailability_status());
    }

    @Test
    void getById_ShouldBorrowing_WhenBorrowingExist() {
        
        Long userId = userRepository.findAll().get(0).getId();
        Long bookId = bookRepository.findAll().get(0).getId();

        BorrowingDTO created = borrowingService.borrow(userId, bookId);
        BorrowingDTO found = borrowingService.getBorrowingById(created.getId());


        assertNotNull(found);
        assertEquals(created.getId(),found.getId());
    }
    @Test
    void getById_ShouldThrowException_WhenBorrowingNotFound() {
         
        assertThrows(ResourceNotFoundException.class, () -> borrowingService.getBorrowingById(999L));
    }
    @Test
    void getBySearcher_ShouldReturnBook_WhenBookExists() {
        
        Long userId = userRepository.findAll().get(0).getId();
        Long bookId = bookRepository.findAll().get(0).getId();

        BorrowingDTO created = borrowingService.borrow(userId, bookId);

        
        List<BorrowingDTO> borrowingsByUser = borrowingService.getAllBorrowings(Optional.of(userId), Optional.empty());
        List<BorrowingDTO> borrowingByBook = borrowingService.getAllBorrowings(Optional.empty(),Optional.of(bookId));
        List<BorrowingDTO> allBorrowings = borrowingService.getAllBorrowings(Optional.empty(),Optional.empty());


        assertNotNull(borrowingsByUser);
        assertNotNull(borrowingByBook);
        assertNotNull(allBorrowings);

    }

}
