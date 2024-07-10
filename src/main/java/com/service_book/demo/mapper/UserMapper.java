package com.service_book.demo.mapper;

import org.mapstruct.Mapper;

import com.service_book.demo.entity.User;
import com.service_book.demo.model.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);

}
