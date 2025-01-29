package com.Library.LMS.Persistence.Repository;

import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest

class BorrowingRepositoryIntegrationTest {
    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private UserEntity johnDoe;
    private UserEntity janeSmith;
    private BookEntity book1;
    private BookEntity book2;

    @BeforeEach
    public void setUp() {


        johnDoe = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .libraryId("JD001")
                .role(Role.USER)
                .password("")
                .build()
        );
        janeSmith = userRepository.save(UserEntity.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .libraryId("JS001")
                .password("")
                .role(Role.USER)
                .build()
        );

        book1 = bookRepository.save(BookEntity.builder()
                .title("title 1")
                .author("famous author1")
                .isbn(1234567890L)
                .availability_status(true)
                .build()
        );
        book2 = bookRepository.save(BookEntity.builder()
                .title("title 2")
                .author("famous author2")
                .isbn(987654321L)
                .availability_status(true)
                .build()
        );
    }

    @Test
    public void testCreateBorrowing() {

        //arrange
        BorrowingEntity borrowing1 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 1))
                .dueDate(LocalDate.of(2025, 1, 1))
                .build();

        //act
        BorrowingEntity savedBorrowing = borrowingRepository.save(borrowing1);

        // assert
        assertNotNull(savedBorrowing);
        assertEquals(borrowing1.getUser(), savedBorrowing.getUser());
        assertEquals(borrowing1.getBook(), savedBorrowing.getBook());
        assertEquals(borrowing1.getBorrowDate(), savedBorrowing.getBorrowDate());
    }

    @Test
    public void testFindBorrowingsByUser() {

        BorrowingEntity borrowing1 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 1))
                .dueDate(LocalDate.of(2025, 1, 1))
                .build();
        BorrowingEntity borrowing2 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book2)
                .borrowDate(LocalDate.of(2024, 12, 5))
                .dueDate(LocalDate.of(2025, 1, 15))
                .build();
        borrowingRepository.save(borrowing1);
        borrowingRepository.save(borrowing2);

        List<BorrowingEntity> borrowings = borrowingRepository.findByUserId(johnDoe.getId()).get();

        assertNotNull(borrowings);
        assertEquals(2, borrowings.size());
    }

    @Test
    public void testFindBorrowingsByBook() {

        BorrowingEntity borrowing1 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 1))
                .dueDate(LocalDate.of(2025, 1, 1))
                .build();
        BorrowingEntity borrowing2 = BorrowingEntity.builder()
                .user(janeSmith)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 5))
                .dueDate(LocalDate.of(2025, 1, 15))
                .build();
        borrowingRepository.save(borrowing1);
        borrowingRepository.save(borrowing2);


        List<BorrowingEntity> borrowings = borrowingRepository.findByBookId(book1.getId()).get();


        assertNotNull(borrowings);
        assertEquals(2, borrowings.size());
    }

    @Test
    public void testUpdateBorrowing(){
        BorrowingEntity borrowing1 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 1))
                .dueDate(LocalDate.of(2025, 1, 1))
                .build();

        borrowingRepository.save(borrowing1);
        borrowing1.setDueDate(LocalDate.of(2025, 1, 25));
        BorrowingEntity borrowingUpdated = borrowingRepository.save(borrowing1);

        assertEquals(borrowing1.getDueDate(),borrowingUpdated.getDueDate());

    }

    @Test
    public void testCountBorrowsActive(){
        BorrowingEntity borrowing1 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book1)
                .borrowDate(LocalDate.of(2024, 12, 1))
                .dueDate(LocalDate.of(2025, 1, 1))
                .build();
        BorrowingEntity borrowing2 = BorrowingEntity.builder()
                .user(johnDoe)
                .book(book2)
                .borrowDate(LocalDate.of(2024, 12, 5))
                .dueDate(LocalDate.of(2025, 1, 15))
                .build();

        borrowingRepository.save(borrowing1);
        borrowingRepository.save(borrowing2);

        int count = borrowingRepository.countByUserIdAndReturnDateIsNull(johnDoe.getId());

        assertEquals(2,count);

    }
}