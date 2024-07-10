package com.service_book.demo.entity;

import static com.service_book.demo.constant.UserRole.ADMIN;
import static com.service_book.demo.constant.UserRole.USER;

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
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 15)
    private String role = USER.getValue();

    @Column(length = 50)
    private String username;

    @Column(length = 50)
    private String password;
}
