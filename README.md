# LibraryManagementSystem
A Java-based Library Management System using JDBC and MySQL. This project implements a menu-driven program that allows library staff to manage books and readers efficiently. Features include adding books, checking availability, issuing and returning books, registering readers, and tracking book holders.

Key Features:

Add, remove, and search books with detailed info

Handle multiple copies of a book

Issue and return books while updating availability

Register and cancel reader memberships

Track which readers currently hold which books

Menu-driven console interface for easy interaction

Tech Stack:

Java (core)

MySQL (database)

JDBC (database connectivity)

Database:

Four tables: books, book_copies, readers, and transactions

Handles multiple copies of books and multiple readers

Setup Instructions:

Import the project into Eclipse IDE.

Create the database in MySQL using the provided librarymanagement.sql script.

Update MySQL credentials in TaskHandler.java.

Run Menu.java to start the application.
