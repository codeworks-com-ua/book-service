package com.service_book.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_book.demo.manager.BookManager;
import com.service_book.demo.model.BookDTO;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/books")
public class BookController {

    private static final String BOOK_UPDATED_SUCCESSFULLY = "Book updated successfully";
    private static final String BOOK_UPDATE_UNSUCCESSFUL_MESSAGE = "Forbidden: You are not allowed to update the book "
            + "or the book is borrowed.";
    private static final String BOOK_DELETED_SUCCESSFULLY = "Book deleted successfully";
    private static final String BOOK_DELETE_UNSUCCESSFUL_MESSAGE = "Forbidden: You are not allowed to delete the book "
            + "or the book is borrowed.";

    BookManager bookManager;

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookManager.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookManager.getById(id));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable Integer id) {
        return ResponseEntity.ok(bookManager.borrowBook(id));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Integer id) {
        return ResponseEntity.ok(bookManager.returnBook(id));
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookManager.create(bookDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Integer id, @RequestBody BookDTO bookDTO) {
        bookManager.update(id, bookDTO);
        return ResponseEntity.ok(BOOK_UPDATED_SUCCESSFULLY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        bookManager.delete(id);
        return ResponseEntity.ok(BOOK_DELETED_SUCCESSFULLY);
    }

    @GetMapping("/users/{id}")
    public List<BookDTO> getAllBooksByUser(@PathVariable Integer id) {
        return bookManager.getAllBooksByUser(id);
    }
}
