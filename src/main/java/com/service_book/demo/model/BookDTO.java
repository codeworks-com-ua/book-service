package com.service_book.demo.model;

public class BookDTO {

    private Integer id = null;

    private String title = null;

    private String isbn = null;

    private String category = null;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }
}
