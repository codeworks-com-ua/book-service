package com.service_book.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.service_book.demo.entity.Book;
import com.service_book.demo.model.BookDTO;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookDTO bookDTO);

    BookDTO toDTO(Book book);

    void updateBook(BookDTO bookDTO, @MappingTarget Book book);
}
