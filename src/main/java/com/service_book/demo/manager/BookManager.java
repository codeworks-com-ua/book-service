package com.service_book.demo.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.service_book.demo.entity.Book;
import com.service_book.demo.mapper.BookMapper;
import com.service_book.demo.model.BookDTO;
import com.service_book.demo.service.BookService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookManager {

    private static final String BOOK_ALREADY_BORROWED = "Book already borrowed";
    private static final String BOOK_IS_NOT_BORROWED = "Book is not borrowed";
    private static final String BOOK_SUCCESSFULLY_BORROWED = "Book successfully borrowed";
    private static final String BOOK_SUCCESSFULLY_RETURNED = "Book successfully returned";

    BookMapper bookMapper = BookMapper.getInstance();
    BookService bookService;

    public List<BookDTO> getAll() {
        log.info("Fetching all books");

        List<BookDTO> books = bookService.getAll().stream()
                .map(bookMapper::toDTO)
                .toList();

        log.info("Fetched {} books", books.size());
        return books;
    }

    public Optional<BookDTO> getById(Integer bookId) {
        log.info("Fetching book with ID: {}", bookId);

        Optional<BookDTO> book = bookService.findById(bookId)
                .map(bookMapper::toDTO);

        if (book.isPresent()) {
            log.info("Fetched book with ID: {}", bookId);
        } else {
            log.warn("Book with ID: {} not found", bookId);
        }

        return book;
    }

    public String borrowBook(Integer bookId) {
        log.info("Borrowing book with ID: {}", bookId);

        String result = bookService.findById(bookId)
                .map(this::processBorrowBook)
                .orElse(String.format("Cannot find book by id: [%s]", bookId));

        log.info("Result of borrowing book with ID {}: {}", bookId, result);
        return result;
    }

    public String returnBook(Integer bookId) {
        log.info("Returning book with ID: {}", bookId);

        String result = bookService.findById(bookId)
                .map(this::processReturnBook)
                .orElse(String.format("Cannot find book by id: [%s]", bookId));

        log.info("Result of returning book with ID {}: {}", bookId, result);
        return result;
    }


    public BookDTO create(BookDTO bookDTO) {
        log.info("Creating new book with details: {}", bookDTO);

        Book savedBook = bookService.persistBook(bookMapper.toEntity(bookDTO));
        BookDTO result = bookMapper.toDTO(savedBook);

        log.info("Created book with ID: {}", result.getId());
        return result;
    }

    public Optional<BookDTO> update(Integer bookId, BookDTO bookDTO) {
        log.info("Updating book with ID: {} with new details: {}", bookId, bookDTO);

        Optional<BookDTO> result = bookService.findById(bookId)
                .filter(Book::isNotBorrowed)
                .map(book -> {
                    bookMapper.updateBook(bookDTO, book);
                    Book updatedBook = bookService.persistBook(book);
                    return bookMapper.toDTO(updatedBook);
                });

        if (result.isPresent()) {
            log.info("Updated book with ID: {}", bookId);
        } else {
            log.warn("Failed to update book with ID: {} - either book not found or it is borrowed", bookId);
        }

        return result;
    }

    public boolean delete(Integer bookId) {
        log.info("Deleting book with ID: {}", bookId);

        boolean result = bookService.findById(bookId)
                .filter(Book::isNotBorrowed)
                .map(book -> {
                    bookService.deleteBook(bookId);
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);

        if (result) {
            log.info("Deleted book with ID: {}", bookId);
        } else {
            log.warn("Failed to delete book with ID: {} - either book not found or it is borrowed", bookId);
        }

        return result;
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

        bookService.markReturnedAndPersist(book);
        return BOOK_SUCCESSFULLY_RETURNED;
    }
}
