package com.service_book.demo.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.service_book.demo.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(value = "SELECT * "
            + "FROM book b "
            + "JOIN app_user_book ub on b.id = ub.app_user_id "
            + "WHERE app_user_id = :userId", nativeQuery = true)
    List<Book> findAllByUserId(@Param("userId") Integer userId);
}
