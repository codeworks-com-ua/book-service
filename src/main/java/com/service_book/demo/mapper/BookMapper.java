package com.service_book.demo.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.service_book.demo.entity.Book;
import com.service_book.demo.model.BookDTO;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookMapper {

    static BookMapper getInstance() {
        return Mappers.getMapper(BookMapper.class);
    }

    BookDTO toDTO(Book book);

    Book toEntity(BookDTO bookDTO);

    void updateBook(BookDTO bookDTO, @MappingTarget Book book);
}
