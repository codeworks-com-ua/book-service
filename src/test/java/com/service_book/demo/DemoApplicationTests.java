package com.service_book.demo;

import static com.service_book.demo.config.JwtRequestFilter.AUTHORIZATION;
import static com.service_book.demo.config.JwtRequestFilter.BEARER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.service_book.demo.dao.BookRepository;

@AutoConfigureMockMvc
class DemoApplicationTests extends IntegrationTestConfig {

	private static final String USER = "USER";
	private static final String ADMIN = "ADMIN";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BookRepository bookRepository;

	@AfterEach
	void tearDown() {
		bookRepository.findById(1)
				.ifPresent(book -> {
					book.setBorrowed(Boolean.FALSE);
					bookRepository.save(book);
				});
	}

	@Test
	@DisplayName("Scenario when USER trying to access create book api. Should receive FORBIDDEN")
	void userTriesToCreateBook_thenForbidden() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, USER)
						.param(PASSWORD, USER))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(post("/api/books")
						.header(AUTHORIZATION, BEARER + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\": \"Test Book\", \"isbn\": \"123-456-789\", \"category\": \"Test Category\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Scenario when USER trying to delete a book. Should receive FORBIDDEN")
	void userTriesToDeleteBook_thenForbidden() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, USER)
						.param(PASSWORD, USER))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(delete("/api/books/{id}", 1)
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Scenario when USER trying to update a book. Should receive FORBIDDEN")
	void userTriesToUpdateBook_thenForbidden() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, USER)
						.param(PASSWORD, USER))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(put("/api/books/{id}", 1)
						.header(AUTHORIZATION, BEARER + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\": \"Updated Title\", \"isbn\": \"123-456-789\", \"category\": \"Updated Category\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Scenario when USER tries to authenticate with wrong credentials. Should receive UNAUTHORIZED")
	void userTriesToAuthenticateWithWrongCredentials_thenUnauthorized() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, USER)
						.param(PASSWORD, "WRONG_PASSWORD"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Scenario when USER logs in, borrows a book, and tries to borrow the same book again")
	void userLogsInBorrowsBook_thenTriesToBorrowAgain() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, USER)
						.param(PASSWORD, USER))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(post("/api/books/1/borrow")
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isOk())
				.andExpect(content().string("Book successfully borrowed"));

		mockMvc.perform(post("/api/books/1/borrow")
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isOk())
				.andExpect(content().string("Book already borrowed"));
	}

	@Test
	@DisplayName("Scenario when ADMIN logs in, tries to delete a book but it's borrowed")
	void adminTriesToDeleteBorrowedBook_thenForbidden() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, ADMIN)
						.param(PASSWORD, ADMIN))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(post("/api/books/1/borrow")
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isOk())
				.andExpect(content().string("Book successfully borrowed"));

		mockMvc.perform(delete("/api/books/1")
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Forbidden: You are not allowed to delete the book or the book is borrowed."));
	}

	@Test
	@DisplayName("Scenario when ADMIN logs in, tries to update a book but it's borrowed")
	void adminTriesToUpdateBorrowedBook_thenForbidden() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.param(USERNAME, ADMIN)
						.param(PASSWORD, ADMIN))
				.andExpect(status().isOk())
				.andReturn();

		String token = loginResult.getResponse().getContentAsString();

		mockMvc.perform(post("/api/books/1/borrow")
						.header(AUTHORIZATION, BEARER + token))
				.andExpect(status().isOk())
				.andExpect(content().string("Book successfully borrowed"));

		mockMvc.perform(put("/api/books/1")
						.header(AUTHORIZATION, BEARER + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\": \"Updated Title\", \"isbn\": \"123-456-789\", \"category\": \"Updated Category\"}"))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Forbidden: You are not allowed to update the book or the book is borrowed."));
	}
}
