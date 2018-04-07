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

	public static void neaSearch(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
		String answer = "";
		String keyword = "";
		while(true){
			System.out.println();
			System.out.println("Choose the search criterion:");
			System.out.println("1. ID");
			System.out.println("2. Family");
			System.out.println("3. Resource type");
			System.out.println("My criterion:");
			answer = menuAns.nextLine();
			if (answer.equals("1")||answer.equals("2")||answer.equals("3")){
				break;
			}else{
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}
		System.out.println();
		System.out.println("Type in the search keyword");
		keyword = menuAns.nextLine();
		if(answer.equals("1")){
			;
		}else if(answer.equals("2")){
			;
		}else if(answer.equals("3")){
			;
			//TODO
		}
	}

	public static void spacecraftSearch(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
		String answer = "";
		String keyword = "";
		while(true){
			System.out.println();
			System.out.println("Choose the search criterion:");
			System.out.println("1. Agency Name");
			System.out.println("2. Type");
			System.out.println("3. Least energy [km/s]");
			System.out.println("4. Least working time [days]");
			System.out.println("5. Least capacity [m^3]");
			System.out.println("My criterion:");
			answer = menuAns.nextLine();
			if (answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4")||answer.equals("5")){
				break;
			}else{
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}
		System.out.println();
		System.out.println("Type in the search keyword");
		keyword = menuAns.nextLine();
		if(answer.equals("1")){
			;
		}else if(answer.equals("2")){
			;
		}else if(answer.equals("3")){
			;
		}else if(answer.equals("4")){
			;
		}else if(answer.equals("5")){
			;
		}
	}

	public static void certainDesign(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void bestDesign(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void customerMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{//done
		String answer = "";
		
		while(true){
			System.out.println();
			System.out.println("-----Operations for exploration companies (rental customers)-----");
			//System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Search for NEAs based on some criteria");
			System.out.println("2. Search for spacecrafts based on some criteria");
			System.out.println("3. A certain NEA exploration mission design");
			System.out.println("4. The most beneficial NEA exploration mission design");
			System.out.println("0. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")){
				neaSearch(menuAns, mySQLDB);
			}else if(answer.equals("2")){
				spacecraftSearch(menuAns, mySQLDB);
			}else if(answer.equals("3")){
				certainDesign(menuAns, mySQLDB);
			}else if(answer.equals("4")){
				bestDesign(menuAns, mySQLDB);
			}else if(answer.equals("0")){
				break;
			}else{
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}	
	}

	public static void rentSpacecraft(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void returnSpacecraft(Scanner menuAns, Connection mySQLDB) throws SQLException{
		//TODO
	}

	public static void listRentedByTime(Scanner menuAns, Connection mySQLDB) throws SQLException{

	}
	public static void listRentedNum(Scanner menuAns, Connection mySQLDB) throws SQLException{

	}

	public static void staffMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String answer = "";
		
		while(true){
			System.out.println();
			System.out.println("-----Operations for spacecraft rental staff-----");
			//System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Rent a spacecraft");
			System.out.println("2. Return a spacecraft");
			System.out.println("3. List all the spacecraft currently rented out (on a mission) for a certain period");
			System.out.println("4. List the number of spacecrafts currently rented out by each Agency");
			System.out.println("0. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")){
				rentSpacecraft(menuAns, mySQLDB);
			}else if(answer.equals("2")){
				returnSpacecraft(menuAns, mySQLDB);
			}else if(answer.equals("3")){
				listRentedByTime(menuAns, mySQLDB);
			}else if(answer.equals("4")){
				listRentedNum(menuAns, mySQLDB);
			}else if(answer.equals("0")){
				break;
			}else{
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}	
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
