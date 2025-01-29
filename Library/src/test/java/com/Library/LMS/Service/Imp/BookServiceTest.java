package com.Library.LMS.Service.Imp;

import com.Library.LMS.Mapping.BookMapper;
import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Repository.BookRepository;
import com.Library.LMS.dto.BookDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void testCreateBook() {
        
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1L, false, Collections.emptyList());
        BookEntity bookEntity = new BookEntity(1L, "Title1", "Author", 1234567890L, true, Collections.emptyList());

        when(bookMapper.toEntity(bookDTO)).thenReturn(bookEntity);
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        
        BookDTO result = bookService.create(bookDTO);

        
        assertNotNull(result);
        assertEquals( bookDTO.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void testGetById() {
        
        Long id = 1L;
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        
        BookDTO result = bookService.getById(id);

        
        assertNotNull(result);
        assertEquals(bookDTO.getIsbn(), result.getIsbn());
        verify(bookRepository, times(1)).findById(id);
    }
    @Test
    void testDeleteBook() {
        
        Long id = 1L;
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));

        
        bookService.delete(id);

        
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).deleteById(id);
    }
    @Test
    void testUpdateBook() {
        
        Long id = 1L;
        BookDTO bookDTO = new BookDTO(1L, "Updated Title", "Updated Author", 1234567890L, true, Collections.emptyList());
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        doAnswer(invocation -> {
            BookDTO source = invocation.getArgument(0);
            bookEntity.setTitle(source.getTitle());
            bookEntity.setAuthor(source.getAuthor());
            return null;
        }).when(bookMapper).updateBookFromDTO(bookDTO, bookEntity);
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        
        BookDTO result = bookService.update(id, bookDTO);

        
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void testGetAllBooks() {
        
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        
        List<BookDTO> result = bookService.getAll();

        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }
    @Test
    void testSearcherWithTitleAndAuthor() {
        String title = "Title";
        String author = "Author";
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findByTitleAndAuthor(title, author)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.searcher(title, author, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleAndAuthor(title, author);
    }

    @Test
    void testSearcherWithTitleOnly() {
        String title = "Title";
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findByTitle(title)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.searcher(title, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitle(title);
    }

    @Test
    void testSearcherWithAuthorOnly() {
        String author = "Author";
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findByAuthor(author)).thenReturn(Optional.of(Collections.singletonList(bookEntity)));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.searcher(null, author, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findByAuthor(author);
    }

    @Test
    void testSearcherWithIsbnOnly() {
        Long isbn = 1234567890L;
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", isbn, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", isbn, true, Collections.emptyList());

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.searcher(null, null, isbn);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    void testSearcherWithNoResults() {
        String title = "Nonexistent Title";

        assertThrows(ResourceNotFoundException.class, () -> bookService.searcher(title, null, null));
    }
    @Test
    void testGetByTitle() {
        String title = "Title";
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findByTitle(title)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        BookDTO result = bookService.getByTitle(title);

        assertNotNull(result);
        assertEquals(bookDTO.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findByTitle(title);
    }

    @Test
    void testGetByAuthor() {
        String author = "Author";
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findByAuthor(author)).thenReturn(Optional.of(Collections.singletonList(bookEntity)));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.getByAuthor(author);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findByAuthor(author);
    }

    @Test
    void testGetByIsbn() {
        Long isbn = 1234567890L;
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", isbn, true, Collections.emptyList());
        BookDTO bookDTO = new BookDTO(1L, "Title", "Author", isbn, true, Collections.emptyList());

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDTO(bookEntity)).thenReturn(bookDTO);

        BookDTO result = bookService.getByIsbn(isbn);

        assertNotNull(result);
        assertEquals(bookDTO.getIsbn(), result.getIsbn());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }



    @Test
    void testMarkAsBorrowed() {
        Long bookId = 1L;
        BookEntity bookEntity = new BookEntity(bookId, "Title", "Author", 1234567890L, true, Collections.emptyList());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        bookService.markAsBorrowed(bookId);

        assertFalse(bookEntity.isAvailability_status());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void testMarkAsAvailable() {
        Long bookId = 1L;
        BookEntity bookEntity = new BookEntity(bookId, "Title", "Author", 1234567890L, false, Collections.emptyList());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        bookService.markAsAvailable(bookId);

        assertTrue(bookEntity.isAvailability_status());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void testDeleteBookWhenBorrowed() {
        Long id = 1L;
        BookEntity bookEntity = new BookEntity(id, "Title", "Author", 1234567890L, false, Collections.emptyList()); // Libro prestado

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));

        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
            bookService.delete(id);
        });

        assertEquals("Book is borrowed", exception.getMessage());
        verify(bookRepository, times(1)).findById(id);
    }
}