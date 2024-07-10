package com.service_book.demo.manager;

import static com.service_book.demo.constant.UserRole.ADMIN;

import org.springframework.stereotype.Component;

import com.service_book.demo.entity.User;
import com.service_book.demo.mapper.UserMapper;
import com.service_book.demo.model.UserDTO;
import com.service_book.demo.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserManager {

    UserMapper userMapper;
    UserService userService;

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User persistedUser = userService.persistUser(user);
        return userMapper.toDTO(persistedUser);
    }

    public UserDTO createAdmin(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setRole(ADMIN.getValue());
        User persistedUser = userService.persistUser(user);
        return userMapper.toDTO(persistedUser);
    }

}
