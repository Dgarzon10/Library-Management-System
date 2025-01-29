package com.Library.LMS.Persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private Long isbn;

    @Column(nullable = false)
    private boolean availability_status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BorrowingEntity> borrowings = new ArrayList<>();

    public void addBorrowing(BorrowingEntity borrowing) {
        borrowings.add(borrowing);
        borrowing.setBook(this);
    }

}
