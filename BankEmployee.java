package ATM6pack;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class BankEmployee {
    
    public static void addAccountHolder(String userId, String pin) {
        try {
            Connection conn = ATM.DBconnect();
            System.out.println("Connected Successfully");

            // Check if the user already exists
            if (ATM.authenticateuser(userId)) {
                System.out.println("User with this ID already exists. Cannot add again.");
                conn.close();
                return;
            }

            // Add the user
            String addUserQuery = "INSERT INTO user(userid, pin) VALUES (?, ?)";
            PreparedStatement addUserStmt = conn.prepareStatement(addUserQuery);
            addUserStmt.setString(1, userId);
            addUserStmt.setString(2, pin);
            addUserStmt.executeUpdate();

            // Create an account for the user
            String addAccountQuery = "INSERT INTO account(accno, userid, balance) VALUES (?, ?, ?)";
            PreparedStatement addAccountStmt = conn.prepareStatement(addAccountQuery);
            
            addAccountStmt.setString(1, generateAccountNumber());
            addAccountStmt.setString(2, userId);
            addAccountStmt.setInt(3, 0); // Initial balance is 0
            addAccountStmt.executeUpdate();

            System.out.println("Account holder added successfully.");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateAccountNumber() {
        /*Simplified account number generation logic (you might want to implement a more robust method)
       return UUID.randomUUID().toString().replace("-","") ;*/
    	  Random random = new Random();
    	    Set<String> usedAccountNumbers = new HashSet<>();

    	    while (true) {
    	        // Generate a random 11-digit number
    	        long accountNumber = 100_000_000_00L + random.nextInt(900_000_000);
    	        String accountNumberString = String.format("%011d", accountNumber);

    	        // Check if the generated number is unique
    	        if (!usedAccountNumbers.contains(accountNumberString)) {
    	            usedAccountNumbers.add(accountNumberString);
    	            return accountNumberString;
    	        }
    	    }
}
   
        
    
    
}  




