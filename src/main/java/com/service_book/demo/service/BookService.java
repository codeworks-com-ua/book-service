package com.service_book.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.service_book.demo.dao.BookRepository;
import com.service_book.demo.entity.Book;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookService {

    BookRepository bookRepository;

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Integer id) {
        return bookRepository.findById(id);
    }

    public Book persistBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Integer id, Book book) {

        if (bookRepository.existsById(id)) {
            book.setId(id);
            return bookRepository.save(book);
        }

        return null;
    }

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }


    public Book returnBook(Integer id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent() && book.get().isBorrowed()) {
            book.get().setBorrowed(false);
            return bookRepository.save(book.get());
        }
        return null;
    }

    public void markBorrowedAndPersist(Book book) {
        book.setBorrowed(Boolean.TRUE);
        bookRepository.save(book);
    }

    public void markReturnedAndPersist(Book book) {
        book.setBorrowed(Boolean.FALSE);
        bookRepository.save(book);
    }

}
