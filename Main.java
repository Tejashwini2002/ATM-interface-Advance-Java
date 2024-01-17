package ATM6pack;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
    	ATM.createDatabase();
		ATM.createTables();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the ATM simulation!");

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. User");
            System.out.println("2. Bank Employee");
            System.out.println("3. Exit");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (option) {
                case 1:
                    userMenu();
                    break;
                case 2:
                    bankEmployeeMenu();
                    break;
                case 3:
                    System.out.println("Exiting ATM simulation. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void userMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();

        if (ATM.authenticateuser(userId)) {
            System.out.print("Enter your PIN: ");
            String pin = scanner.nextLine();

            if (ATM.checkPassword(userId, pin)) {
                System.out.println("Authentication successful!");

                while (true) {
                    System.out.println("Select a transaction:");
                    System.out.println("1. Deposit");
                    System.out.println("2. Withdraw");
                    System.out.println("3. Transfer");
                    System.out.println("4. Transaction History");
                    System.out.println("5. Exit");
                    

                    int userOption = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    switch (userOption) {
                        case 1:
                            System.out.print("Enter amount to deposit: ");
                            int depositAmount = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                            System.out.print("Enter your account number: ");
                            String ano = scanner.next();
                            ATM.deposit(depositAmount,ano);
                            break;
                        case 2:
                            System.out.print("Enter amount to withdraw: ");
                            int withdrawAmount = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                            System.out.print("Enter your_account_number: ");
                            String wano = scanner.next();
                            ATM.withdraw(withdrawAmount, wano);
                            break;
                        case 3:
                        	System.out.print("Enter sender account number for transfer: ");
                            String senderAccount = scanner.nextLine();
                            System.out.print("Enter recipient account number for transfer: ");
                            String recipientAccount = scanner.nextLine();
                            System.out.print("Enter amount to transfer: ");
                            int transferAmount = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                            ATM.transfer(senderAccount, recipientAccount, transferAmount);
                            break;
                        case 4: 
                        	System.out.print("Enter account number to get your Transaction History: ");
                            String TAccountnum = scanner.nextLine();
                            ATM.printTransactionHistory(TAccountnum);
                        case 5:
                            System.out.println("Exiting user menu.");
                            return;
                        
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            } else {
                System.out.println("Invalid PIN. Exiting user menu.");
            }
        } else {
            System.out.println("User not found. Exiting user menu.");
        }
    }

    private static void bankEmployeeMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your employee ID: ");
        String employeeId = scanner.nextLine();

        if (employeeId.equals("admin")) {
            System.out.println("Authentication successful!");

            while (true) {
                System.out.println("Select an option:");
                System.out.println("1. Add Account Holder");
                System.out.println("2. Exit");

                int bankEmployeeOption = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                
                switch (bankEmployeeOption) {
                    case 1:
                        System.out.print("Enter user ID: ");
                        String userId = scanner.nextLine();
                        System.out.print("Enter PIN: ");
                        String pin = scanner.nextLine();
                        BankEmployee.addAccountHolder(userId, pin);
                        break;
                    // To Do:  Add cases for  bank employee to update account holders.
                    
                    case 2:
                        System.out.println("Exiting bank employee menu.");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } else {
            System.out.println("Authentication failed. Only bank employees can access this feature.");
        }
    }
}




