package com.service_book.demo.util;

import static java.lang.String.format;

import java.util.List;

import com.service_book.demo.exception.DataNotFoundException;
import com.service_book.demo.model.BookDTO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookValidator {

    public void validateBooks(Integer userId, List<BookDTO> books) {
        if (books.isEmpty()) {
            throw new DataNotFoundException(format("No books taken for user with id: %s", userId));
        }
    }
}
