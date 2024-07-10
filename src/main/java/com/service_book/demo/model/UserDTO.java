package com.service_book.demo.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDTO {

    private String username;
    private String password;
    private String apiKey;
}
