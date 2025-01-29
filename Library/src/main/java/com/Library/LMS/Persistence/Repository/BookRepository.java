package com.Library.LMS.Persistence.Repository;

import com.Library.LMS.Persistence.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  BookRepository extends JpaRepository<BookEntity,Long> {

    Optional<BookEntity> findByTitle(String title);
    Optional<List<BookEntity>> findByAuthor(String author);
    Optional<BookEntity> findByIsbn(Long isbn);
    Boolean existsByIsbn(Long isbn);
    Optional<BookEntity> findByTitleAndAuthor(String title, String author);
}
