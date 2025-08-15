package LibraryManagementSystem;

import java.util.Scanner;

public class Menu {
	public static void main(String[] args) throws Exception {
		TaskHandler ts = new TaskHandler();
		ts.establishConnection();
		System.out.println("*********************WELCOME*********************\n\nPlease Enter Your Choice\n");
		int choice;
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println(
					"1. Add book\n2. Check availability of book\n3. Find a book with detailed info\n4. Remove books"
							+ "\n5. Issue a book\n6. Return book\n7. Check book holders\n8. Register reader\n9. Cancel reader's membership"
							+ "\n10. Exit");
			choice = sc.nextInt();

			switch (choice) {
			case 1:
				ts.addBook();
				break;
			case 2:
				ts.checkAvailability();
				break;
			case 3:
				ts.findBook();
				break;
			case 4:
				ts.removeBook();
				break;
			case 5:
				ts.issueBook();
				break;
			case 6:
				ts.returnBook();
				break;
			case 7:
				ts.checkBookHolders();
				break;
			case 8:
				ts.registerReader();
				break;
			case 9:
				ts.cancelMembership();
				break;
			case 10:
				ts.closeConnection();
				System.out.println("Exited");
				sc.close();
				System.exit(0);
			default:
				System.out.println("Invalid choice! Please try again.");
			}

		}
	}

}
