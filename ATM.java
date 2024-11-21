package MyATM_package;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;

public class ATM {

    public static void createDatabase() {
        try {
        	String url = "jdbc:mysql://localhost:3306/";
            String username = "root";
            String password = "Tanuvinnu@1234";

           
		    Connection conn = DriverManager.getConnection(url, username, password);
		    Statement stm = conn.createStatement();
//            System.out.println("Connected Successfully");
		 // Check if the 'atm6' database exists
	        ResultSet resultSet = stm.executeQuery("SHOW DATABASES LIKE 'atm6'");
	        if (!resultSet.next()) {
	            // If 'atm6' database doesn't exist, create it
	            String createDbQuery = "CREATE DATABASE IF NOT EXISTS atm6";
	            stm.executeUpdate(createDbQuery);
//	            System.out.println("'atm6' database created successfully.");
	        } else {
//	            System.out.println("'atm6' database already exists.");
	        }

	        // Use the 'atm4' database
	        conn.setCatalog("atm6");           
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    	public static void createTables() {
    	    try {
    	        Connection conn = DBconnect();
    	        Statement stm = conn.createStatement();
//    	        System.out.println("Connected Successfully");

    	        // Check if the 'user' table exists
    	        ResultSet resultSet = stm.executeQuery("SHOW TABLES LIKE 'user'");
    	        if (!resultSet.next()) {
    	            // If 'user' table doesn't exist, create it
    	            String createUserTableQuery = "CREATE TABLE user(userid varchar(25) primary key, pin varchar(25))";
    	            stm.executeUpdate(createUserTableQuery);
//    	            System.out.println("'user' table created successfully.");
    	        } else {
//    	            System.out.println("'user' table already exists.");
    	        }

    	            // Check if the 'account' table exists
    	        resultSet = stm.executeQuery("SHOW TABLES LIKE 'account'");
    	        if (!resultSet.next()) {
    	            // If 'account' table doesn't exist, create it
    	            String createAccountTableQuery = "CREATE TABLE account(accno varchar(35) primary key, userid varchar(20), balance int, foreign key(userid) references user(userid) on update cascade on delete cascade)";
    	            stm.executeUpdate(createAccountTableQuery);
//    	            System.out.println("'account' table created successfully.");
    	        } else {
//    	            System.out.println("'account' table already exists.");
    	        }

    	        // Check if the 'transactionhistory' table exists
    	        resultSet = stm.executeQuery("SHOW TABLES LIKE 'transactionhistory'");
    	        if (!resultSet.next()) {
    	            // If 'transactionhistory' table doesn't exist, create it
    	            String createTransactionHistoryTableQuery = "CREATE TABLE transactionhistory(transactionId varchar(16) primary key, datetime varchar(30), transactionType varchar(12),accno varchar(15), relatedAccno varchar(15),amt int, foreign key(accno) references account(accno) on update cascade on delete cascade, foreign key(relatedAccno) references account(accno) on update cascade on delete cascade)";
    	            stm.executeUpdate(createTransactionHistoryTableQuery);
//    	            System.out.println("'transactionhistory' table created successfully.");
    	        } else {
//    	            System.out.println("'transactionhistory' table already exists.");
    	        }

    	        conn.close();
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	}

    public static boolean authenticateuser(String uid) {
        try {
            Connection conn = DBconnect();
//            System.out.println("Connected Successfully");
            String query = "select userid from user where userid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, uid);
            ResultSet rs = pstmt.executeQuery();

            String searcheduserid = null;
            while (rs.next()) {
                searcheduserid = rs.getString(1);
            }
            conn.close();
            return searcheduserid != null && searcheduserid.equals(uid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
   
    public static boolean checkPassword(String uid, String pw) {
        try {
            Connection conn = DBconnect();
//            System.out.println("Connected Successfully");
            String query = "select pin from user where userid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, uid);
            ResultSet rs = pstmt.executeQuery();

            String searchedpw = null;
            while (rs.next()) {
                searchedpw = rs.getString(1);
            }
            conn.close();
            return searchedpw != null && searchedpw.equals(pw);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deposit(int amt, String accountnum) {
        try {
            if (amt < 0) {
                System.out.println("Amount invalid");
            } else {
                Connection conn = DBconnect();
                conn.setAutoCommit(false);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String datetime = dtf.format(now);
                
                String query0 = "update account set balance = balance + ? where accno = ?";
                PreparedStatement pstmt = conn.prepareStatement(query0);
                pstmt.setInt(1, amt);
                pstmt.setString(2, accountnum);
                pstmt.executeUpdate();
                String transactionId = generateUniqueTransactionId();
                String query1 = "insert into transactionhistory(transactionId , datetime ,transactionType, accno , relatedAccno ,amt)values(?, ?, ?, ?,?,?)";
                PreparedStatement pstmt1 = conn.prepareStatement(query1);
                pstmt1.setString(1,transactionId);
                pstmt1.setString(2, datetime);
                pstmt1.setString(3, "Deposit");
                pstmt1.setString(4, accountnum);
                pstmt1.setString(5, accountnum);
                pstmt1.setInt(6, amt);
               
                pstmt1.executeUpdate();
                System.out.println("Deposited successfully.");
                
                conn.commit(); // very important to save the changes
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void withdraw(int amt, String accountnum) {
        try {
            if (amt < 0) {
                System.out.println("Amount invalid");
            }else if(getBalance(accountnum)<amt){
            	System.out.println("Insufficient balance to withdraw amount");
            }
            else {
                Connection conn = DBconnect();
                conn.setAutoCommit(false);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String datetime = dtf.format(now);
                String query = "update account set balance = balance - ? where accno = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, amt);
                pstmt.setString(2, accountnum);
                pstmt.executeUpdate();
                String transactionId = generateUniqueTransactionId();
                String query1 = "insert into transactionhistory(transactionId , datetime , transactionType,accno , relatedAccno ,amt)values(?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt1 = conn.prepareStatement(query1);
                pstmt1.setString(1,transactionId);
                pstmt1.setString(2, datetime);
                pstmt1.setString(3, "Withdraw");
                pstmt1.setString(4, accountnum);
                pstmt1.setString(5, accountnum);
                pstmt1.setInt(6, amt);
               
                pstmt1.executeUpdate();
                System.out.println("Withdrawn successfully.");
                conn.commit(); // very important to save the changes
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void transfer(String senderAccno, String receiverAccno, int amountToBeSent) {
        try {
            Connection conn = DBconnect();
            conn.setAutoCommit(false);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String datetime = dtf.format(now);

            if (getBalance(senderAccno) < amountToBeSent) {
                System.out.println("Insufficient balance to transfer amount");
                conn.close();
                return;
            }

            String query1 = "update account set balance = balance - ? where accno = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(query1);
            pstmt1.setInt(1, amountToBeSent);
            pstmt1.setString(2, senderAccno);
            pstmt1.executeUpdate();

            String query2 = "update account set balance = balance + ? where accno = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setInt(1, amountToBeSent);
            pstmt2.setString(2, receiverAccno);
            pstmt2.executeUpdate();

            System.out.println("Transfer successful.");
            String transactionId = generateUniqueTransactionId();

            // Set the relatedAccno to receiverAccno for both sender and receiver in transaction history
            String query3 = "insert into transactionhistory(transactionId, datetime, transactionType, accno, relatedAccno, amt) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt3 = conn.prepareStatement(query3);
            pstmt3.setString(1, transactionId);
            pstmt3.setString(2, datetime);
            pstmt3.setString(3, "Transfer");
            pstmt3.setString(4, senderAccno);
            pstmt3.setString(5, receiverAccno);
            pstmt3.setInt(6, amountToBeSent);
            pstmt3.executeUpdate();

            conn.commit(); // very important to save the changes
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBalance(String accountNumber) {
    	int balance= 0;
        try {
            Connection conn = DBconnect();
//            System.out.println("Connected Successfully");

            String query = "SELECT balance FROM account WHERE accno = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
          
            while (rs.next()) {
            	 balance = (rs.getInt("balance"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
      
		return balance;
    }
    
    static Connection conn = null;
    public static Connection DBconnect(){
        try{
            String url = "jdbc:mysql://localhost:3306/";
            String db = "atm6"; // database name
            String username = "root";
            String password = "Tanuvinnu@1234";

           
		 conn = DriverManager.getConnection(url+db, username, password);
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }


    public static String generateUniqueTransactionId() {
        int length = 16;
        StringBuilder transactionId = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        // Ensure the first four characters are alphabetical
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(26); // 26 for the number of alphabetical characters
            char randomChar = (char) ('A' + randomIndex);
            transactionId.append(randomChar);
        }

        // Generate the remaining characters as numbers
        for (int i = 4; i < length; i++) {
            int randomDigit = random.nextInt(10); // 10 for the number of digits
            transactionId.append(randomDigit);
        }

        return transactionId.toString();
    }
           
    public static void printTransactionHistory(String tAccountnum) {
        try {
            Connection conn = DBconnect();
            
            System.out.println("Connected Successfully");

            String query = "SELECT * FROM transactionhistory " +
                           "WHERE accno = ? OR relatedAccno = ? " +
                           "ORDER BY datetime";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tAccountnum);
            pstmt.setString(2, tAccountnum);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No transaction history found for account number: " + tAccountnum);
            } else {
                System.out.printf("%-20s | %-20s | %-15s | %-15s |%-20s | %-8s%n",
                        "Transaction ID", "Datetime", "Transaction Type", "Account No", "Related Account No", "Amount");
                System.out.println("-----------------------------------------------------------------------------");

                while (rs.next()) {
                    String transactionId = rs.getString("transactionId");
                    String datetime = rs.getString("datetime");
                    String transactionType = rs.getString("transactionType");
                    String accno = rs.getString("accno");
                    String relatedAccno = rs.getString("relatedAccno");
                    int amount = rs.getInt("amt");

                    System.out.printf("%-20s | %-20s | %-15s | %-15s |%-20s | %-8s%n",
                            transactionId, datetime, transactionType, accno, relatedAccno, amount);
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


		
	}      



