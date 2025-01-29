package com.Library.LMS.Service.Imp;

import com.Library.LMS.Mapping.BookMapper;
import com.Library.LMS.Persistence.Entity.BookEntity;
import com.Library.LMS.Persistence.Repository.BookRepository;
import com.Library.LMS.Service.GenericService;
import com.Library.LMS.dto.BookDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookService implements GenericService<BookDTO, Long> {

    // Service responsible for managing books. It uses the BookRepository to interact with the database
    // and perform operations on book data, ensuring proper data handling and business logic implementation.


    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookDTO create(BookDTO dto) {
        if(bookRepository.existsByIsbn(dto.getIsbn())){
            throw new ResourceConflictException("Book with ISBN " + dto.getIsbn() +" Already exist.");
        }
        BookEntity book = bookMapper.toEntity(dto);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    @Override
    public BookDTO update(Long id, BookDTO dto) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
        bookMapper.updateBookFromDTO(dto,book);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
        if(!book.isAvailability_status()){
            throw new ResourceConflictException("Book is borrowed");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public BookDTO getById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
    }

    public List<BookDTO> searcher(String title, String author, Long isbn){
        List<BookDTO> listBooks;

        if (author!=null && title!=null){
            listBooks = new ArrayList<>();
            Optional<BookEntity> book = bookRepository.findByTitleAndAuthor(title,author);
            if (book.isPresent()) {
                listBooks.add(bookMapper.toDTO(book.get()));
            } else {
                throw new ResourceNotFoundException("Book with Author: "+author+" and title: " + title + " not found.");
            }
        }
        else if(title!=null){
            listBooks = new ArrayList<>();
            Optional<BookEntity> book = bookRepository.findByTitle(title);
            if (book.isPresent()) {
                listBooks.add(bookMapper.toDTO(book.get()));
            } else {
                throw new ResourceNotFoundException("Book with title: "+title+" not found.");
            }
        }
        else if (author != null) {
            listBooks = bookRepository.findByAuthor(author)
                    .filter(bookEntities -> !bookEntities.isEmpty())
                    .map(bookEntities -> bookEntities.stream()
                            .map(bookMapper::toDTO)
                            .toList())
                    .orElseThrow(() -> new ResourceNotFoundException("Book with Author " + author + " not found."));
        }
        else {
            listBooks = new ArrayList<>();
            if(isbn!=null){
                Optional<BookEntity> book = bookRepository.findByIsbn(isbn);
                if (book.isPresent()) {
                    listBooks.add(bookMapper.toDTO(book.get()));
                } else {
                    throw new ResourceNotFoundException("Book with ISBN " + isbn + " not found.");
                }
            }
        }
        if (listBooks.isEmpty()) {
            throw new ResourceNotFoundException("Book not found.");
        }

        return listBooks;
    }

    public BookDTO getByTitle(String title) {
        return bookRepository.findByTitle(title)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Book with title " + title + " not found."));
    }

    public List<BookDTO> getByAuthor(String author) {
        return bookRepository.findByAuthor(author).map(
                        bookEntities -> bookEntities.stream()
                                .map(bookMapper::toDTO)
                                .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Book with Author " + author + " not found." ));

    }

    public BookDTO getByIsbn(Long isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ISBN " + isbn + " not found."));
    }

    @Override
    public List<BookDTO> getAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDTO)
                .toList();
    }
    public void markAsBorrowed(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found."));
        book.setAvailability_status(false);
        bookRepository.save(book);
    }

    public void markAsAvailable(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found."));
        book.setAvailability_status(true);
        bookRepository.save(book);
    }
}