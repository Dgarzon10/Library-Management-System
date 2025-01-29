package com.Library.LMS.Controller;

import com.Library.LMS.Service.Imp.BorrowingService;
import com.Library.LMS.dto.BorrowingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/Borrowing")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    // GET /api/v1/Borrowing/users/{userId}/books - View borrowed books
    @GetMapping("/users/{userId}/books")
    
    public ResponseEntity<List<BorrowingDTO>> getBorrowedBooks(@PathVariable Long userId) {
        List<BorrowingDTO> borrowings = borrowingService.getAllBorrowings(Optional.of(userId), Optional.empty());
        return ResponseEntity.ok(borrowings);
    }

    // GET /api/v1/Borrowing/books/{id}/borrowings - View borrowings of a book
    @GetMapping("/books/{bookId}")
    
    public ResponseEntity<List<BorrowingDTO>> getBorrowingsByBook(@PathVariable Long bookId) {
        List<BorrowingDTO> borrowings = borrowingService.getAllBorrowings(Optional.empty(), Optional.of(bookId));
        return ResponseEntity.ok(borrowings);
    }

    // GET /api/v1/Borrowing - View all borrowings
    @GetMapping("")
    
    public ResponseEntity<List<BorrowingDTO>> getAllBorrowings() {
        List<BorrowingDTO> borrowings = borrowingService.getAllBorrowings(Optional.empty(), Optional.empty());
        return ResponseEntity.ok(borrowings);
    }

    // GET /api/v1/Borrowing/borrowings/{id} - Get borrowing details
    @GetMapping("/{id}")
    
    public ResponseEntity<BorrowingDTO> getBorrowingDetails(@PathVariable Long id) {
        BorrowingDTO borrowing = borrowingService.getBorrowingById(id);
        return ResponseEntity.ok(borrowing);
    }

    // POST /api/v1/Borrowing/users/{userId}/books - Borrow a book
    @PostMapping("/users/{userId}/books/{bookId}")
    
    public ResponseEntity<BorrowingDTO> borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        BorrowingDTO borrowing = borrowingService.borrow(userId, bookId);
        return new ResponseEntity<>(borrowing, HttpStatus.CREATED);
    }

    // POST /api/v1/Borrowing/borrowings/{id}/return - Return a book
    @PostMapping("/{id}/return")
    
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        borrowingService.returnBook(id);
        return ResponseEntity.noContent().build();
    }
}
