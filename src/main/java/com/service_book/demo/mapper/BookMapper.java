package com.service_book.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.service_book.demo.entity.Book;
import com.service_book.demo.model.BookDTO;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toDTO(Book book);

    Book toEntity(BookDTO bookDTO);

    void updateBook(BookDTO bookDTO, @MappingTarget Book book);
}
