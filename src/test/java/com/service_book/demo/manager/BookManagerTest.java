package com.service_book.demo.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.service_book.demo.entity.Book;
import com.service_book.demo.exception.DataNotFoundException;
import com.service_book.demo.model.BookDTO;
import com.service_book.demo.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookManagerTest {

    private static final Integer BOOK_ID = 1;
    private static final String CATEGORY = "book_category";
    private static final String ISBN = "isbn";
    private static final String TITLE = "title";

    @Mock
    BookService bookService;
    @InjectMocks
    BookManager bookManager;

    @Test
    @DisplayName("Should retrieve entities, map to dto and return list.")
    void getAll_happyPass_shouldReturnCollection() {
        when(bookService.getAll()).thenReturn(List.of(generateBook()));

        List<BookDTO> actualResult = bookManager.getAll();

        assertThat(actualResult)
                .extracting(BookDTO::getId)
                .containsExactly(BOOK_ID);
        assertThat(actualResult)
                .extracting(BookDTO::getIsbn)
                .containsExactly(ISBN);
        assertThat(actualResult)
                .extracting(BookDTO::getCategory)
                .containsExactly(CATEGORY);
        assertThat(actualResult)
                .extracting(BookDTO::getTitle)
                .containsExactly(TITLE);
    }

    @Test
    @DisplayName("Should retrieve book, map to dto and return.")
    void getById_happyPass_shouldReturnBook() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(generateBook()));

        BookDTO actualResult = bookManager.getById(BOOK_ID);

        assertThat(actualResult.getId()).isEqualTo(BOOK_ID);
        assertThat(actualResult.getIsbn()).isEqualTo(ISBN);
        assertThat(actualResult.getCategory()).isEqualTo(CATEGORY);
        assertThat(actualResult.getTitle()).isEqualTo(TITLE);
    }

    @Test
    @DisplayName("Should throw exception when book does not exist.")
    void getById_bookDoesNotExists_shouldThrowDataNotFoundException() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookManager.getById(BOOK_ID))
                .hasMessageContaining("Book not found with id: 1")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when book does not exist.")
    void borrowBook_bookDoesNotExists_shouldThrowDataNotFoundException() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookManager.borrowBook(BOOK_ID))
                .hasMessageContaining("Cannot find book by id: 1")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should return message that book already borrowed.")
    void borrowBook_bookAlreadyBorrowed_shouldReturnCorrectMessage() {
        Book borrowedBook = generateBook();
        borrowedBook.setBorrowed(Boolean.TRUE);
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(borrowedBook));

        String actualResult = bookManager.borrowBook(BOOK_ID);

        assertThat(actualResult).isEqualTo("Book already borrowed");
    }

    @Test
    @DisplayName("Should mark book as borrowed and persist.")
    void borrowBook_happyPass_shouldBorrowBook() {
        Book book = generateBook();
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));

        String actualResult = bookManager.borrowBook(BOOK_ID);

        assertThat(actualResult).isEqualTo("Book successfully borrowed");
        verify(bookService).markBorrowedAndPersist(book);
    }

    @Test
    @DisplayName("Should throw exception when book does not exist.")
    void returnBook_bookDoesNotExists_shouldThrowDataNotFoundException() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookManager.returnBook(BOOK_ID))
                .hasMessageContaining("Cannot find book by id: 1")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should return message that book already borrowed.")
    void returnBook_bookIsNotBorrowed_shouldReturnCorrectMessage() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(generateBook()));

        String actualResult = bookManager.returnBook(BOOK_ID);

        assertThat(actualResult).isEqualTo("Book is not borrowed");
    }

    @Test
    @DisplayName("Should mark book as not borrowed and persist.")
    void returnBook_happyPass_shouldReturnBook() {
        Book book = generateBook();
        book.setBorrowed(Boolean.TRUE);
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));

        String actualResult = bookManager.returnBook(BOOK_ID);

        assertThat(actualResult).isEqualTo("Book successfully returned");
        verify(bookService).markReturnedAndPersist(book);
    }

    @Test
    @DisplayName("Should persist book, map to dto and return.")
    void create_happyPass_shouldReturnDto() {
        BookDTO bookDTO = generateBookDTO();
        Book book = generateBook();
        when(bookService.persistBook(any(Book.class))).thenReturn(book);

        BookDTO actualResult = bookManager.create(bookDTO);

        assertThat(actualResult.getId()).isEqualTo(BOOK_ID);
        assertThat(actualResult.getIsbn()).isEqualTo(ISBN);
        assertThat(actualResult.getCategory()).isEqualTo(CATEGORY);
        assertThat(actualResult.getTitle()).isEqualTo(TITLE);
    }

    @Test
    @DisplayName("Should not update book when can not find it by id.")
    void update_bookNotFound_shouldThrowDataNotFoundException() {
        BookDTO bookDTO = generateBookDTO();
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookManager.update(BOOK_ID, bookDTO))
                .hasMessageContaining("Failed to update book with ID: 1 - either book not found or it is borrowed")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should update book.")
    void update_happyPass_shouldUpdate() {
        BookDTO bookDTO = generateBookDTO();
        bookDTO.setCategory("custom_category");
        Book book = generateBook();
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookService.persistBook(book)).thenReturn(book);

        bookManager.update(BOOK_ID, bookDTO);
    }

    @Test
    @DisplayName("Should not update book when book is borrowed.")
    void update_bookIsBorrowed_shouldThrowDataNotFoundException() {
        BookDTO bookDTO = generateBookDTO();
        Book book = generateBook();
        book.setBorrowed(Boolean.TRUE);
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookManager.update(BOOK_ID, bookDTO))
                .hasMessageContaining("Failed to update book with ID: 1 - either book not found or it is borrowed")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should not delete book when can not find it by id.")
    void delete_bookNotFound_shouldThrowDataNotFoundException() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookManager.delete(BOOK_ID))
                .hasMessageContaining("Failed to delete book with ID: 1 - either book not found or it is borrowed")
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete book.")
    void delete_happyPass_shouldDelete() {
        Book book = generateBook();
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));

        bookManager.delete(BOOK_ID);

        verify(bookService).deleteBook(BOOK_ID);
    }

    @Test
    @DisplayName("Should not delete book when book is borrowed.")
    void delete_bookIsBorrowed_shouldNThrowDataNotFoundException() {
        Book book = generateBook();
        book.setBorrowed(Boolean.TRUE);
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookManager.delete(BOOK_ID))
                .hasMessageContaining("Failed to delete book with ID: 1 - either book not found or it is borrowed")
                .isInstanceOf(DataNotFoundException.class);
    }

    private static Book generateBook() {
        Book book = new Book();
        book.setId(BOOK_ID);
        book.setBorrowed(Boolean.FALSE);
        book.setCategory(CATEGORY);
        book.setIsbn(ISBN);
        book.setTitle(TITLE);

        return book;
    }

    private static BookDTO generateBookDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(BOOK_ID);
        bookDTO.setCategory(CATEGORY);
        bookDTO.setIsbn(ISBN);
        bookDTO.setTitle(TITLE);

        return bookDTO;
    }
}
