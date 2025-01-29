package com.Library.LMS.Service.Imp;

import static org.junit.jupiter.api.Assertions.*;

import com.Library.LMS.Mapping.BorrowingMapper;
import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Entity.BorrowingEntity;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.BookRepository;
import com.Library.LMS.Persistence.Repository.BorrowingRepository;
import com.Library.LMS.Persistence.Repository.UserRepository;
import com.Library.LMS.dto.BorrowingDTO;
import com.Library.LMS.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BorrowingMapper borrowingMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BorrowingService borrowingService;

    private UserEntity user;
    private BookEntity book;
    private BorrowingEntity borrowing;
    private BorrowingDTO borrowingDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);

        book = new BookEntity();
        book.setId(1L);
        book.setAvailability_status(true);

        borrowing = BorrowingEntity.builder()
                .id(1L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        borrowingDTO = new BorrowingDTO();
        borrowingDTO.setId(1L);
        borrowingDTO.setBorrowDate(borrowing.getBorrowDate());
        borrowingDTO.setDueDate(borrowing.getDueDate());
    }

    @Test
    void borrow_ShouldReturnBorrowingDTO_WhenValidRequest() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(borrowingRepository.countByUserIdAndReturnDateIsNull(1L)).thenReturn(0);
        when(borrowingRepository.save(any())).thenReturn(borrowing);
        when(borrowingMapper.toDTO(any())).thenReturn(borrowingDTO);

        // Act
        BorrowingDTO result = borrowingService.borrow(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookService, times(1)).markAsBorrowed(1L);
    }

    @Test
    void borrow_ShouldThrowException_WhenBookNotFound() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowingService.borrow(1L, 1L));
    }

    @Test
    void borrow_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowingService.borrow(1L, 1L));
    }

    @Test
    void returnBook_ShouldUpdateBorrowingAndMarkBookAsAvailable() {
        // Arrange
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));

        // Act
        borrowingService.returnBook(1L);

        // Assert
        assertNotNull(borrowing.getReturnDate());
        verify(bookService, times(1)).markAsAvailable(1L);
        verify(borrowingRepository, times(1)).save(borrowing);
    }

    @Test
    void returnBook_ShouldThrowException_WhenBorrowingNotFound() {
        // Arrange
        when(borrowingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowingService.returnBook(1L));
    }

    @Test
    void getBorrowingById_ShouldReturnBorrowingDTO_WhenFound() {
        // Arrange
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(borrowingMapper.toDTO(any())).thenReturn(borrowingDTO);

        // Act
        BorrowingDTO result = borrowingService.getBorrowingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBorrowingById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(borrowingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowingService.getBorrowingById(1L));
    }

    @Test
    void getAllBorrowings_ShouldReturnListOfBorrowingDTO_WhenNoFilter() {
        // Arrange
        when(borrowingRepository.findAll()).thenReturn(List.of(borrowing));
        when(borrowingMapper.toDTOList(any())).thenReturn(List.of(borrowingDTO));

        // Act
        List<BorrowingDTO> result = borrowingService.getAllBorrowings(Optional.empty(), Optional.empty());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
