import java.util.Scanner;
import java.sql.*;
import java.io.*;
public class CSCI3170Proj {

	public static String dbAddress = "";//TODO
	public static String dbUsername = "";//TODO
	public static String dbPassword = "";//TODO

	public static Connection connectToOracle(){
		Connection con = null;
		//TODO
		return con;
	}

	public static void createTables(Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void deleteTables(Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void loadTables(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void showTables(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}


	public static void adminMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{//done
		String answer = "";
		
		while(true){
			System.out.println();
			System.out.println("-----Operations for administrator menu-----");
			System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Create all tables");
			System.out.println("2. Delete all tables");
			System.out.println("3. Load data from a dataset");
			System.out.println("4. Show number of records in each table");
			System.out.println("0. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")){
				createTables(mySQLDB);
			}else if(answer.equals("2")){
				deleteTables(mySQLDB);
			}else if(answer.equals("3")){
				loadTables(menuAns, mySQLDB);
			}else if(answer.equals("4")){
				showTables(menuAns, mySQLDB);
			}else if(answer.equals("0")){
				break;
			}else{
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}	
	}

	public static void customerMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{

	}

	public static void staffMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{

	}

	public static void main(String[] args) {
		Scanner menuAns = new Scanner(System.in);
		System.out.println("Welcome to NEAs Exploration Mission Design System!");

		while(true){
			try{
				Connection mySQLDB = connectToOracle();
				System.out.println();
				System.out.println("-----Main Menu-----");
				System.out.println("What kinds of operation would you like to perform?");
				System.out.println("1. Operations for administrator");
				System.out.println("2. Operations for exploration companied (rental customers)");
				System.out.println("3. Operations for spacecraft rental staff");
				System.out.println("0. Exit the program");
				System.out.print("Enter Your Choice: ");
				String answer = menuAns.nextLine();

				if(answer.equals("1")){
					adminMenu(menuAns, mySQLDB);
				}else if(answer.equals("2")){
					customerMenu(menuAns, mySQLDB);
				}else if(answer.equals("3")){
					staffMenu(menuAns, mySQLDB);
				}else if(answer.equals("0")){
					break;
				}else{
					System.out.println("[Error]: Wrong Input, Type in again!!!");
				}
			}catch (SQLException e){
				System.out.println(e);
			}
		}

		menuAns.close();
		System.exit(0);
	}
}
