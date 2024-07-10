package com.service_book.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service_book.demo.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

}
