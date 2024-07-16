package com.service_book.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service_book.demo.dao.BookRepository;
import com.service_book.demo.entity.Book;
import com.service_book.demo.entity.User;

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

    @Transactional
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public void markBorrowedAndPersist(Book book) {
        book.setBorrowed(Boolean.TRUE);
        bookRepository.save(book);
    }

    @Transactional
    public void markReturnedAndPersist(Book book) {
        book.setBorrowed(Boolean.FALSE);
        bookRepository.save(book);
    }

    public List<Book> findAllByUser(User user) {
        return bookRepository.findAllByUserId(user.getId());
    }
}
