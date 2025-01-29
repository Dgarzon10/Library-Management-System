package com.Library.LMS.Persistence.Repository;

import com.Library.LMS.Persistence.Entity.BookEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    private BookEntity testBook;

    @BeforeEach
    public void setup() {
        testBook = BookEntity.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn(123456789L)
                .availability_status(true)
                .build();

        bookRepository.save(testBook);
    }

    @AfterEach
    public void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    public void saveBookTest() {
        BookEntity newBook = BookEntity.builder()
                .title("Another Title")
                .author("Another Author")
                .isbn(987654321L)
                .availability_status(true)
                .build();

        BookEntity savedBook = bookRepository.save(newBook);

        assertNotNull(savedBook.getId());
        assertEquals("Another Title", savedBook.getTitle());
    }

    @Test
    public void getBookByIdTest() {
        Optional<BookEntity> optionalBook = bookRepository.findById(testBook.getId());

        assertTrue(optionalBook.isPresent());
        assertEquals(testBook.getId(), optionalBook.get().getId());
    }
    @Test
    public void getBookByTitleTest() {

        Optional<BookEntity> optionalBookTitle = bookRepository.findByTitle("Test Title");

        assertTrue(optionalBookTitle.isPresent());
        assertEquals(testBook.getTitle(), optionalBookTitle.get().getTitle());
    }
    @Test
    public void getBookByAuthorTest() {

        List<BookEntity> ListBooks = bookRepository.findByAuthor("Test Author").get();

        assertEquals(1, ListBooks.size());
        assertEquals(testBook.getAuthor(), ListBooks.get(0).getAuthor());
    }
    @Test
    public void getBookByIsbnTest() {
        Optional<BookEntity> optionalBook = bookRepository.findByIsbn(testBook.getIsbn());

        assertTrue(optionalBook.isPresent());
        assertEquals(testBook.getId(), optionalBook.get().getId());
    }
    @Test
    public void getBookByTitleAndAuthorTest() {
        Optional<BookEntity> optionalBook = bookRepository.findByTitleAndAuthor(testBook.getTitle(), testBook.getAuthor());

        assertTrue(optionalBook.isPresent());
        assertEquals(testBook.getId(), optionalBook.get().getId());
    }

    @Test
    public void existByIsbnTest() {
        Boolean existBook = bookRepository.existsByIsbn(testBook.getIsbn());

        assertTrue(existBook);
    }
    @Test
    public void getAllTest() {
        List<BookEntity> books = bookRepository.findAll();

        assertEquals(1, books.size());
        assertEquals(testBook.getTitle(), books.get(0).getTitle());
    }

    @Test
    public void updateBookTest() {
        testBook.setAvailability_status(false);
        BookEntity updatedBook = bookRepository.save(testBook);

        assertFalse(updatedBook.isAvailability_status());
    }

    @Test
    public void deleteBookTest() {
        bookRepository.delete(testBook);

        Optional<BookEntity> optionalBook = bookRepository.findById(testBook.getId());
        assertFalse(optionalBook.isPresent());
    }

}
