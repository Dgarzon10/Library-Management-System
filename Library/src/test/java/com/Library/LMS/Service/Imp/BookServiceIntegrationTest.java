package com.Library.LMS.Service.Imp;


import com.Library.LMS.dto.BookDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;


    private BookDTO bookDTO;

    @BeforeEach
    void setUp(){

        bookDTO = BookDTO.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn(123456789L)
                .availability_status(true)
                .build();
    }
    @Test
    void create_ShouldCreateBook_WhenSuccess(){
        
        BookDTO createdBook = bookService.create(bookDTO);

        
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        assertEquals(bookDTO.getTitle(),createdBook.getTitle());
        assertEquals(new ArrayList<>(),createdBook.getBorrowingIds()); //Verify that borrowingIds is initialized as an empty list by the mapper during the toEntity conversion.

    }
    @Test
    void create_ShouldThrowException_WhenBookAlreadyExists(){
        
        bookService.create(bookDTO);

        assertThrows(ResourceConflictException.class, () -> bookService.create(bookDTO));
    }
    @Test
    void update_ShouldUpdateBook_WhenValidBookDTO() {
         
        BookDTO createdBook = bookService.create(bookDTO);
        createdBook.setTitle("Updated Title");

         
        BookDTO updatedBook = bookService.update(createdBook.getId(), createdBook);

         
        assertEquals("Updated Title", updatedBook.getTitle());
    }
    @Test
    void update_ShouldThrowException_WhenBookNotFound() {

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> bookService.update(999L, bookDTO));
        assertNotNull(exception);
    }
    @Test
    void delete_ShouldDeleteBook_WhenBookIsAvailable() {
         
        BookDTO createdBook = bookService.create(bookDTO);

         
        bookService.delete(createdBook.getId());

         
        assertThrows(ResourceNotFoundException.class, () -> bookService.getById(createdBook.getId()));
    }
    @Test
    void delete_ShouldThrowException_WhenBookIsBorrowed() {
         
        BookDTO createdBook = bookService.create(bookDTO);
        bookService.markAsBorrowed(createdBook.getId());


        assertThrows(ResourceConflictException.class, () -> bookService.delete(createdBook.getId()));
    }
    @Test
    void getById_ShouldReturnBook_WhenBookExists() {
         
        BookDTO createdBook = bookService.create(bookDTO);

         
        BookDTO foundBook = bookService.getById(createdBook.getId());

         
        assertNotNull(foundBook);
        assertEquals(createdBook.getId(), foundBook.getId());
    }
    @Test
    void getById_ShouldThrowException_WhenBookNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> bookService.getById(999L));
    }
    @Test
    void getBySearcher_ShouldReturnBook_WhenBookExists() {
         
        bookService.create(bookDTO);

         
        List<BookDTO> foundBookByTitleAndAuthor = bookService.searcher(bookDTO.getTitle(),bookDTO.getAuthor(),null);
        List<BookDTO> foundBookByTitle = bookService.searcher(bookDTO.getTitle(),null,null);
        List<BookDTO> foundBookByAuthor = bookService.searcher(null,bookDTO.getAuthor(),null);
        List<BookDTO> foundBookByIsbn = bookService.searcher(null,null,bookDTO.getIsbn());


         
        assertNotNull(foundBookByTitleAndAuthor);
        assertNotNull(foundBookByTitle);
        assertNotNull(foundBookByAuthor);
        assertNotNull(foundBookByIsbn);

    }
    @Test
    void getByTitle_ShouldReturnBook_WhenBookExists() {
         
        bookService.create(bookDTO);

         
        BookDTO foundBook = bookService.getByTitle("Test Title");

         
        assertNotNull(foundBook);
        assertEquals("Test Title", foundBook.getTitle());
    }

    @Test
    void getByTitle_ShouldThrowException_WhenBookNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> bookService.getByTitle("Nonexistent Title"));
    }

    @Test
    void getByAuthor_ShouldReturnBooks_WhenBooksExist() {
         
        bookService.create(bookDTO);
        BookDTO bookDTO2 = BookDTO.builder()
                .title("Test Title2")
                .author("Test Author")
                .isbn(98764L)
                .availability_status(true)
                .build();

        bookService.create(bookDTO2); 

         
        List<BookDTO> books = bookService.getByAuthor("Test Author");

         
        assertNotNull(books);
        assertEquals(2, books.size());
    }
    @Test
    void getByAuthor_ShouldThrowException_WhenBookNotFound() {

        assertEquals(new ArrayList<>(),bookService.getByAuthor("Nonexistent Title"));
    }
    @Test
    void getByIsbn_ShouldReturnBooks_WhenBooksExist() {
         
        bookService.create(bookDTO);

         
        BookDTO book = bookService.getByIsbn(bookDTO.getIsbn());

         
        assertNotNull(book);
        assertEquals("Test Title", book.getTitle());
    }
    @Test
    void getByIsbn_ShouldThrowException_WhenBookNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> bookService.getByIsbn(999L));
    }
    @Test
    void getAll_ShouldReturnBooks_WhenBooksExist(){
        bookService.create(bookDTO);
        BookDTO bookDTO2 = BookDTO.builder()
                .title("Test Title2")
                .author("Test Author")
                .isbn(98764L)
                .availability_status(true)
                .build();

        bookService.create(bookDTO2);

        List<BookDTO> books = bookService.getAll();

        assertEquals(2,books.size());

    }
    @Test
    void getAll_ShouldReturnEmptyList_WhenNoBooksExist(){
        List<BookDTO> books = bookService.getAll();

        assertNotNull(books);
        assertTrue(books.isEmpty());

    }
    @Test
    void markAsAvailable_ShouldMakeBookAvailable_WhenBookIsNotAvailable() {
         
        BookDTO createdBook = bookService.create(bookDTO);
        bookService.markAsBorrowed(createdBook.getId());

         
        bookService.markAsAvailable(createdBook.getId());

         
        BookDTO updatedBook = bookService.getById(createdBook.getId());
        assertTrue(updatedBook.isAvailability_status());
    }

    @Test
    void markAsBorrowed_ShouldMakeBookUnavailable_WhenBookIsAvailable() {
         
        BookDTO createdBook = bookService.create(bookDTO);

         
        bookService.markAsBorrowed(createdBook.getId());

         
        BookDTO updatedBook = bookService.getById(createdBook.getId());
        assertFalse(updatedBook.isAvailability_status());
    }


}