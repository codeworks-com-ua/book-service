package com.service_book.demo.manager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.service_book.demo.entity.Book;
import com.service_book.demo.entity.User;
import com.service_book.demo.mapper.BookMapper;
import com.service_book.demo.model.BookDTO;
import com.service_book.demo.service.BookService;
import com.service_book.demo.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookManager {

    private static final String BOOK_ALREADY_BORROWED = "Book already borrowed";
    private static final String BOOK_IS_NOT_BORROWED = "Book is not borrowed";
    private static final String BOOK_SUCCESSFULLY_BORROWED = "Book successfully borrowed";
    private static final String BOOK_SUCCESSFULLY_RETURNED = "Book successfully returned";

    BookMapper bookMapper;
    BookService bookService;

    public List<BookDTO> getAll() {
        return bookService.getAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getById(Integer bookId) {
        return bookService.findById(bookId)
                .map(bookMapper::toDTO);
    }

    public String borrowBook(Integer bookId) {
        return bookService.findById(bookId)
                .map(this::processBorrowBook)
                .orElse(String.format("Cannot find book by id: [%s]", bookId));
    }

    public String returnBook(Integer bookId) {
        return bookService.findById(bookId)
                .map(this::processReturnBook)
                .orElse(String.format("Cannot find book by id: [%s]", bookId));
    }

    public BookDTO create(BookDTO bookDTO) {
        Book savedBook = bookService.persistBook(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(savedBook);
    }

    public Optional<BookDTO> update(Integer bookId, BookDTO bookDTO) {
        return bookService.findById(bookId)
                .filter(Book::isNotBorrowed)
                .map(book -> {
                    bookMapper.updateBook(bookDTO, book);
                    Book updatedBook = bookService.persistBook(book);
                    return bookMapper.toDTO(updatedBook);
                });
    }

    public boolean delete(Integer bookId) {
        return bookService.findById(bookId)
                .filter(book -> !book.isBorrowed())
                .map(book -> {
                    bookService.deleteBook(bookId);
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }

    private String processBorrowBook(Book book) {

        if (book.isBorrowed()) {
            return BOOK_ALREADY_BORROWED;
        }

        bookService.markBorrowedAndPersist(book);
        return BOOK_SUCCESSFULLY_BORROWED;
    }

    private String processReturnBook(Book book) {

        if (book.isNotBorrowed()) {
            return BOOK_IS_NOT_BORROWED;
        }

        bookService.markBorrowedAndPersist(book);
        return BOOK_SUCCESSFULLY_RETURNED;
    }
}
