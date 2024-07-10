package com.service_book.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String title = null;

    @Column(length = 50)
    private String isbn = null;

    @Column(length = 50)
    private String category = null;

    private boolean borrowed = false;

    public boolean isNotBorrowed() {
        return !this.borrowed;
    }
}
