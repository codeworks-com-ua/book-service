-- Create table for User entity
CREATE TABLE app_user
(
    id       SERIAL PRIMARY KEY,
    role     VARCHAR(15),
    username VARCHAR(50),
    password VARCHAR(150)
);

-- Create table for Book entity
CREATE TABLE book
(
    id       SERIAL PRIMARY KEY,
    title    VARCHAR(50),
    isbn     VARCHAR(50),
    category VARCHAR(50),
    borrowed BOOLEAN
);

-- Populating user table with default values
INSERT INTO app_user (role, username, password)
VALUES
    ('ADMIN', 'ADMIN', '$2a$10$wxXnVzP1i.yysW659Uyw.Om1KQjF0NIeg7twTtx/zf6E1hnS.roHC'),
    ('USER', 'USER', '$2a$10$8CyE1YGPNiz0V8M/SYIZKOhA//3ptxWGHs.whbKp7vwArIx1WVyC2');

-- Populating book table with default values
INSERT INTO book (title, isbn, category, borrowed)
VALUES
    ('To Kill a Mockingbird', '978-0-06-112008-4', 'Fiction', false),
    ('1984', '978-0-452-28423-4', 'Dystopian', false),
    ('The Great Gatsby', '978-0-7432-7356-5', 'Classic', false),
    ('The Catcher in the Rye', '978-0-316-76948-0', 'Classic', false),
    ('Moby-Dick', '978-0-14-243724-7', 'Adventure', false),
    ('Pride and Prejudice', '978-0-19-953556-9', 'Romance', false),
    ('The Hobbit', '978-0-618-00221-3', 'Fantasy', false),
    ('War and Peace', '978-0-19-923276-5', 'Historical', false),
    ('The Odyssey', '978-0-14-026886-7', 'Epic', false),
    ('Crime and Punishment', '978-0-14-044913-6', 'Psychological', false);
