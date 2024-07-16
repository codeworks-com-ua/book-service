package com.service_book.demo.manager;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.service_book.demo.entity.Book;
import com.service_book.demo.entity.User;
import com.service_book.demo.exception.DataNotFoundException;
import com.service_book.demo.mapper.BookMapper;
import com.service_book.demo.model.BookDTO;
import com.service_book.demo.service.BookService;
import com.service_book.demo.service.UserService;
import com.service_book.demo.util.BookValidator;

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
    UserService userService;

    public List<BookDTO> getAll() {
        log.info("Fetching all books");

        List<BookDTO> books = bookService.getAll().stream()
                .map(bookMapper::toDTO)
                .toList();

        log.info("Fetched {} books", books.size());
        return books;
    }

    public BookDTO getById(Integer bookId) {
        log.info("Fetching book with ID: {}", bookId);

        return bookService.findById(bookId)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new DataNotFoundException("Book not found with id: " + bookId));
    }

    public String borrowBook(Integer bookId) {
        log.info("Borrowing book with ID: {}", bookId);

        return bookService.findById(bookId)
                .map(this::processBorrowBook)
                .orElseThrow(() -> new DataNotFoundException("Cannot find book by id: " + bookId));
    }

    public String returnBook(Integer bookId) {
        log.info("Returning book with ID: {}", bookId);

        return bookService.findById(bookId)
                .map(this::processReturnBook)
                .orElseThrow(() -> new DataNotFoundException("Cannot find book by id: " + bookId));
    }

    public BookDTO create(BookDTO bookDTO) {
        log.info("Creating new book with details: {}", bookDTO);

        Book savedBook = bookService.persistBook(bookMapper.toEntity(bookDTO));
        BookDTO result = bookMapper.toDTO(savedBook);

        log.info("Created book with ID: {}", result.getId());
        return result;
    }

    @Transactional
    public BookDTO update(Integer bookId, BookDTO bookDTO) {
        log.info("Updating book with ID: {} with new details: {}", bookId, bookDTO);
        return bookService.findById(bookId)
                .filter(Book::isNotBorrowed)
                .map(book -> {
                    bookMapper.updateBook(bookDTO, book);
                    Book updatedBook = bookService.persistBook(book);
                    log.info("Updated book with ID: {}", bookId);
                    return bookMapper.toDTO(updatedBook);
                })
                .orElseThrow(() -> new DataNotFoundException(format("Failed to update book with ID: %s - "
                        + "either book not found or it is borrowed", bookId)));
    }

    public void delete(Integer bookId) {
        log.info("Deleting book with ID: {}", bookId);
        boolean result = bookService.findById(bookId)
                .filter(Book::isNotBorrowed)
                .map(book -> {
                    bookService.deleteBook(bookId);
                    log.info("Deleted book with ID: {}", bookId);
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);

        if (!result) {
            log.warn("Failed to delete book with ID: {} - either book not found or it is borrowed", bookId);
            throw new DataNotFoundException(format("Failed to delete book with ID: %s - "
                    + "either book not found or it is borrowed", bookId));
        }
    }

    public List<BookDTO> getAllBooksByUser(Integer userId) {
        User user = userService.getUserById(userId);

        List<BookDTO> books = bookService.findAllByUser(user).stream()
                .map(bookMapper::toDTO)
                .toList();

        BookValidator.validateBooks(userId, books);

        return books;
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
