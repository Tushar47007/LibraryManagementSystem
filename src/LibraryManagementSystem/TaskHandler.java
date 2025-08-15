package LibraryManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class TaskHandler {

    private Connection conn;
    private Scanner sc = new Scanner(System.in);

    // 1. Establish DB connection
    public void establishConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/librarymanagement";
        String username = "root";
        String pwd = "root";

        conn = DriverManager.getConnection(url, username, pwd);
        System.out.println((conn != null) ? "Database Connected!\n" : "Database Connection Failed\n");
    }

    // Close connection
    public void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. Add Book
    public void addBook() {
        try {
            System.out.print("Enter book title: ");
            String title = sc.nextLine();
            System.out.print("Enter author name: ");
            String author = sc.nextLine();
            System.out.print("Enter number of copies: ");
            int copies = sc.nextInt();
            sc.nextLine(); // consume newline

            String queryBook = "INSERT INTO books (title, author) VALUES (?, ?)";
            PreparedStatement psBook = conn.prepareStatement(queryBook, Statement.RETURN_GENERATED_KEYS);
            psBook.setString(1, title);
            psBook.setString(2, author);
            psBook.executeUpdate();

            ResultSet rs = psBook.getGeneratedKeys();
            int bookId = 0;
            if (rs.next()) bookId = rs.getInt(1);

            String queryCopy = "INSERT INTO book_copies (book_id, availability) VALUES (?, TRUE)";
            PreparedStatement psCopy = conn.prepareStatement(queryCopy);
            for (int i = 0; i < copies; i++) {
                psCopy.setInt(1, bookId);
                psCopy.executeUpdate();
            }

            System.out.println("Book and copies added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // 2. Check Availability
    public void checkAvailability() {
        try {
            System.out.print("Enter book title: ");
            String title = sc.nextLine();

            String query = "SELECT COUNT(*) AS available_count FROM books b " +
                    "JOIN book_copies c ON b.book_id = c.book_id " +
                    "WHERE b.title = ? AND c.availability = TRUE";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("available_count");
                System.out.println("Available copies: " + count);
            }
        } catch (SQLException e) {
            System.out.println("Error checking availability: " + e.getMessage());
        }
    }

    // 3. Find Book with Details
    public void findBook() {
        try {
            System.out.print("Enter book title: ");
            String title = sc.nextLine();

            String query = "SELECT b.book_id, b.title, b.author, COUNT(c.copy_id) AS total_copies, " +
                    "SUM(c.availability) AS available_copies " +
                    "FROM books b " +
                    "JOIN book_copies c ON b.book_id = c.book_id " +
                    "WHERE b.title = ? " +
                    "GROUP BY b.book_id, b.title, b.author";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("book_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Total Copies: " + rs.getInt("total_copies"));
                System.out.println("Available Copies: " + rs.getInt("available_copies"));
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error finding book: " + e.getMessage());
        }
    }

    // 4. Remove Book
    public void removeBook() {
        try {
            System.out.print("Enter book ID to remove: ");
            int bookId = sc.nextInt();
            sc.nextLine();

            PreparedStatement psCopy = conn.prepareStatement("DELETE FROM book_copies WHERE book_id = ?");
            psCopy.setInt(1, bookId);
            psCopy.executeUpdate();

            PreparedStatement psBook = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");
            psBook.setInt(1, bookId);
            int rows = psBook.executeUpdate();

            if (rows > 0) System.out.println("Book removed successfully!");
            else System.out.println("Book not found.");
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    // 5. Issue Book
    public void issueBook() {
        try {
            System.out.print("Enter book title to issue: ");
            String title = sc.nextLine();
            System.out.print("Enter reader ID: ");
            int readerId = sc.nextInt();
            sc.nextLine();

            String query = "SELECT copy_id FROM book_copies c JOIN books b ON c.book_id = b.book_id " +
                    "WHERE b.title = ? AND c.availability = TRUE LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int copyId = rs.getInt("copy_id");

                PreparedStatement psTrans = conn.prepareStatement(
                        "INSERT INTO transactions (copy_id, reader_id, issue_date, status) VALUES (?, ?, CURDATE(), 'issued')");
                psTrans.setInt(1, copyId);
                psTrans.setInt(2, readerId);
                psTrans.executeUpdate();

                PreparedStatement psUpdate = conn.prepareStatement("UPDATE book_copies SET availability = FALSE WHERE copy_id = ?");
                psUpdate.setInt(1, copyId);
                psUpdate.executeUpdate();

                System.out.println("Book issued successfully! Copy ID: " + copyId);
            } else {
                System.out.println("No available copies for this book.");
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    // 6. Return Book
    public void returnBook() {
        try {
            System.out.print("Enter copy ID to return: ");
            int copyId = sc.nextInt();
            sc.nextLine();

            PreparedStatement psTrans = conn.prepareStatement(
                    "UPDATE transactions SET return_date = CURDATE(), status = 'returned' WHERE copy_id = ? AND status = 'issued'");
            psTrans.setInt(1, copyId);
            int rows = psTrans.executeUpdate();

            if (rows > 0) {
                PreparedStatement psUpdate = conn.prepareStatement("UPDATE book_copies SET availability = TRUE WHERE copy_id = ?");
                psUpdate.setInt(1, copyId);
                psUpdate.executeUpdate();

                System.out.println("Book returned successfully!");
            } else {
                System.out.println("This copy is not currently issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    // 7. Check Book Holders
    public void checkBookHolders() {
        try {
            System.out.print("Enter book title: ");
            String title = sc.nextLine();

            String query = "SELECT r.reader_id, r.name, t.copy_id, t.issue_date " +
                    "FROM transactions t " +
                    "JOIN book_copies c ON t.copy_id = c.copy_id " +
                    "JOIN books b ON c.book_id = b.book_id " +
                    "JOIN readers r ON t.reader_id = r.reader_id " +
                    "WHERE b.title = ? AND t.status = 'issued'";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Reader ID: " + rs.getInt("reader_id") +
                        ", Name: " + rs.getString("name") +
                        ", Copy ID: " + rs.getInt("copy_id") +
                        ", Issue Date: " + rs.getDate("issue_date"));
            }

            if (!found) System.out.println("No active holders for this book.");
        } catch (SQLException e) {
            System.out.println("Error checking book holders: " + e.getMessage());
        }
    }

    // 8. Register Reader
    public void registerReader() {
        try {
            System.out.print("Enter reader name: ");
            String name = sc.nextLine();
            System.out.print("Enter email: ");
            String email = sc.nextLine();
            System.out.print("Enter phone: ");
            String phone = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO readers (name, email, phone, membership_date, status) VALUES (?, ?, ?, CURDATE(), TRUE)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.executeUpdate();

            System.out.println("Reader registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error registering reader: " + e.getMessage());
        }
    }

    // 9. Cancel Membership
    public void cancelMembership() {
        try {
            System.out.print("Enter reader ID to cancel: ");
            int readerId = sc.nextInt();
            sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("UPDATE readers SET status = FALSE WHERE reader_id = ?");
            ps.setInt(1, readerId);
            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("Membership cancelled successfully!");
            else System.out.println("Reader not found.");
        } catch (SQLException e) {
            System.out.println("Error cancelling membership: " + e.getMessage());
        }
    }
}