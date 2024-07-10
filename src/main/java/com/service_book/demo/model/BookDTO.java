package com.service_book.demo.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookDTO {

    private Integer id = null;

    private String title = null;

    private String isbn = null;

    private String category = null;
}
