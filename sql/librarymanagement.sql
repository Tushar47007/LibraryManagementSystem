-- 1. Create database
CREATE DATABASE IF NOT EXISTS librarymanagement;
USE librarymanagement;

-- 2. Create books table
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

-- 3. Create book_copies table
CREATE TABLE IF NOT EXISTS book_copies (
    copy_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    availability BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

-- 4. Create readers table
CREATE TABLE IF NOT EXISTS readers (
    reader_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    membership_date DATE,
    status BOOLEAN DEFAULT TRUE  -- TRUE = active, FALSE = cancelled
);

-- 5. Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    copy_id INT NOT NULL,
    reader_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'issued', -- issued / returned
    FOREIGN KEY (copy_id) REFERENCES book_copies(copy_id) ON DELETE CASCADE,
    FOREIGN KEY (reader_id) REFERENCES readers(reader_id) ON DELETE CASCADE
);

-- 6. Sample Books
INSERT INTO books (title, author) VALUES
('The Alchemist', 'Paulo Coelho'),
('1984', 'George Orwell'),
('To Kill a Mockingbird', 'Harper Lee');

-- 7. Sample Book Copies
INSERT INTO book_copies (book_id, availability) VALUES
(1, TRUE),(1, TRUE),(1, TRUE),   -- 3 copies of The Alchemist
(2, TRUE),(2, TRUE),              -- 2 copies of 1984
(3, TRUE);                         -- 1 copy of To Kill a Mockingbird

-- 8. Sample Readers
INSERT INTO readers (name, email, phone, membership_date, status) VALUES
('Alice', 'alice@example.com', '1234567890', CURDATE(), TRUE),
('Bob', 'bob@example.com', '9876543210', CURDATE(), TRUE);

-- 9. Sample Transactions (for testing issued books)
-- Let's say Alice issued a copy of The Alchemist
INSERT INTO transactions (copy_id, reader_id, issue_date, status) VALUES
(1, 1, CURDATE(), 'issued');  