import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class CSCI3170Proj {

	public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db07";
	public static String dbUsername = "Group07";
	public static String dbPassword = "mogician";

	// mysql --host=projgw --port=2312 -u Group07 -p
	public static Connection connectToOracle() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		} catch (ClassNotFoundException e) {
			System.out.println("[Error]: Java MySQL DB Driver not found!!");
			System.exit(0);
		} catch (SQLException e) {
			System.out.println(e);
			System.exit(0);//To avoid "false connection"
		}
		return con;
	}

	public static void createTables(Connection mySQLDB) throws SQLException {
		String Resource_SQL = "CREATE TABLE Resource (";
		Resource_SQL += "RType VARCHAR(2) PRIMARY KEY NOT NULL,";
		Resource_SQL += "Density DOUBLE NOT NULL,";
		Resource_SQL += "Value DOUBLE NOT NULL)";
		Resource_SQL += "COLLATE=latin1_general_cs";//for case sensitive

		String NEA_SQL = "CREATE TABLE NEA (";
		NEA_SQL += "NID VARCHAR(10) PRIMARY KEY NOT NULL,";
		NEA_SQL += "Distance DOUBLE NOT NULL,";
		NEA_SQL += "Family VARCHAR(6) NOT NULL,";
		NEA_SQL += "Duration INT NOT NULL,";
		NEA_SQL += "Energy DOUBLE NOT NULL,";
		NEA_SQL += "RType VARCHAR(2),";
		NEA_SQL += "FOREIGN KEY (RType) REFERENCES Resource(RType),";
		NEA_SQL += "CHECK (Duration BETWEEN 100 AND 999))";
		NEA_SQL += "COLLATE=latin1_general_cs";


		String Spacecraft_Model_SQL = "CREATE TABLE Spacecraft_Model (";
		Spacecraft_Model_SQL += "Agency VARCHAR(4) NOT NULL,";
		Spacecraft_Model_SQL += "MID VARCHAR(4) NOT NULL,";
		Spacecraft_Model_SQL += "Num INT NOT NULL,";
		Spacecraft_Model_SQL += "Charge INT NOT NULL,";
		Spacecraft_Model_SQL += "Duration INT NOT NULL,";
		Spacecraft_Model_SQL += "Energy DOUBLE NOT NULL,";
		Spacecraft_Model_SQL += "Capacity INT,";
		Spacecraft_Model_SQL += "Type VARCHAR(1) NOT NULL,";
		Spacecraft_Model_SQL += "PRIMARY KEY (Agency, MID),";
		Spacecraft_Model_SQL += "CHECK (Num BETWEEN 1 AND 99),";
		Spacecraft_Model_SQL += "CHECK (Charge BETWEEN 1 AND 99999),";
		Spacecraft_Model_SQL += "CHECK (Capacity BETWEEN 1 AND 99),";
		Spacecraft_Model_SQL += "CHECK (Duration BETWEEN 100 AND 999))";
		Spacecraft_Model_SQL += "COLLATE=latin1_general_cs";

		String RentalRecord_SQL = "CREATE TABLE RentalRecord (";
		RentalRecord_SQL += "Agency VARCHAR(4) NOT NULL,";
		RentalRecord_SQL += "MID VARCHAR(4) NOT NULL,";
		RentalRecord_SQL += "SNum INT NOT NULL,";
		RentalRecord_SQL += "CheckoutDate DATE,";
		RentalRecord_SQL += "ReturnDate DATE,";
		RentalRecord_SQL += "PRIMARY KEY (Agency, MID, SNum),";
		RentalRecord_SQL += "FOREIGN KEY (Agency, MID) REFERENCES Spacecraft_Model(Agency, MID),";
		RentalRecord_SQL += "CHECK (SNum BETWEEN 1 AND 99))";
		RentalRecord_SQL += "COLLATE=latin1_general_cs";

		Statement stmt = mySQLDB.createStatement();
		System.out.print("Processing...");
		stmt.execute(Resource_SQL);
		stmt.execute(NEA_SQL);
		stmt.execute(Spacecraft_Model_SQL);
		stmt.execute(RentalRecord_SQL);
		System.out.println("Done! Database is initialized!");
		stmt.close();
	}

	public static void deleteTables(Connection mySQLDB) throws SQLException {
		Statement stmt = mySQLDB.createStatement();
		System.out.print("Processing...");
		stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
		stmt.execute("DROP TABLE IF EXISTS Resource");
		stmt.execute("DROP TABLE IF EXISTS NEA");
		stmt.execute("DROP TABLE IF EXISTS Spacecraft_Model");
		stmt.execute("DROP TABLE IF EXISTS RentalRecord");
		stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
		System.out.println("Done! Database is removed!");
		stmt.close();
	}

	public static void loadTables(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String Resource_SQL = "INSERT INTO Resource (RType, Density, Value) VALUES (?,?,?)";
		String NEA_SQL = "INSERT INTO NEA (NID, Distance, Family, Duration, Energy, RType) VALUES (?,?,?,?,?,?)";
		String Spacecraft_Model_SQL = "INSERT INTO Spacecraft_Model (Agency, MID, Num, Charge, Duration, Energy, Capacity, Type) VALUES (?,?,?,?,?,?,?,?)";
		String RentalRecord_SQL = "INSERT INTO RentalRecord (Agency, MID, SNum, CheckoutDate, ReturnDate) VALUES (?,?,?,STR_TO_DATE(?,'%d-%m-%Y'),STR_TO_DATE(?,'%d-%m-%Y'))";

		String filePath = "";
		while (true) {
			System.out.println("");
			System.out.print("Type in the Source Data Folder Path: ");
			filePath = menuAns.nextLine();
			if ((new File(filePath)).isDirectory())
				break;
		}

		System.out.print("Processing...");
		try {
			PreparedStatement stmt = mySQLDB.prepareStatement(Resource_SQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/resources.txt"));
			dataReader.readLine(); //skip the first line which is not data
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString(1, dataFields[0]);
				stmt.setDouble(2, Double.parseDouble(dataFields[1]));
				stmt.setDouble(3, Double.parseDouble(dataFields[2]));
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
			dataReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			PreparedStatement stmt = mySQLDB.prepareStatement(NEA_SQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/neas.txt"));
			dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString(1, dataFields[0]);
				stmt.setDouble(2, Double.parseDouble(dataFields[1]));
				stmt.setString(3, dataFields[2]);
				stmt.setInt(4, Integer.parseInt(dataFields[3]));
				stmt.setDouble(5, Double.parseDouble(dataFields[4]));
				//  System.out.println(dataFields[5]);
				if (dataFields[5].equals("null"))
					stmt.setNull(6, java.sql.Types.VARCHAR);
				else
					stmt.setString(6, dataFields[5]);
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
			dataReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			PreparedStatement stmt = mySQLDB.prepareStatement(Spacecraft_Model_SQL);

			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/spacecrafts.txt"));
			dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString(1, dataFields[0]);//Agency
				stmt.setString(2, dataFields[1]);//MID
				stmt.setInt(3, Integer.parseInt(dataFields[2]));//Num
				stmt.setInt(4, Integer.parseInt(dataFields[7]));//Charge
				stmt.setInt(5, Integer.parseInt(dataFields[5]));//Duration
				stmt.setDouble(6, Double.parseDouble(dataFields[4]));//Energy
				if (dataFields[6].equals("null"))
					stmt.setNull(7, java.sql.Types.INTEGER);
				else
					stmt.setInt(7, Integer.parseInt(dataFields[6]));//Capacity
				stmt.setString(8, dataFields[3]);//Type
				stmt.addBatch();
				// Agency	MID	Num	Type	Energy(km/s)	T(days)	Capacity(m3)	Charge($/day)
			}

			stmt.executeBatch();
			stmt.close();
			dataReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			PreparedStatement stmt = mySQLDB.prepareStatement(RentalRecord_SQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/rentalrecords.txt"));
			dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString(1, dataFields[0]);//Agency
				stmt.setString(2, dataFields[1]);
				stmt.setInt(3, Integer.parseInt(dataFields[2]));
				stmt.setString(4, dataFields[3]);
				if (dataFields[4].equals("null"))
					stmt.setNull(5, java.sql.Types.DATE);
				else
					stmt.setString(5, dataFields[4]);
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
			dataReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("Data are successfully loaded!");
	}

	public static void showTables(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String[] table_name = { "Resource", "NEA", "Spacecraft_Model", "RentalRecord" };

		System.out.println("Number of records in each table:");
		for (int i = 0; i < 4; i++) {
			Statement stmt = mySQLDB.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from " + table_name[i]);

			rs.next();
			System.out.println(table_name[i] + ": " + rs.getString(1));
			rs.close();
			stmt.close();
		}
	}

	public static void adminMenu(Scanner menuAns, Connection mySQLDB) throws SQLException {//done
		String answer = "";

		while (true) {
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

			if (answer.equals("1")) {
				createTables(mySQLDB);
			} else if (answer.equals("2")) {
				deleteTables(mySQLDB);
			} else if (answer.equals("3")) {
				loadTables(menuAns, mySQLDB);
			} else if (answer.equals("4")) {
				showTables(menuAns, mySQLDB);
			} else if (answer.equals("0")) {
				break;
			} else {
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}
	}

	public static void neaSearch(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String method = "";
		String keyword = "";
		String searchSQL = "";
		PreparedStatement stmt = null;

		searchSQL += "SELECT N.NID, N.Distance, N.Family, N.Duration, N.Energy, N.RType ";
		searchSQL += "FROM NEA N ";

		while (true) {
			System.out.println();
			System.out.println("Choose the search criterion:");
			System.out.println("1. ID");
			System.out.println("2. Family");
			System.out.println("3. Resource type");
			System.out.print("My criterion:");
			method = menuAns.nextLine();
			if (method.equals("1") || method.equals("2") || method.equals("3")) {
				break;
			} else {
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}

		while (true){
			System.out.print("Type in the search keyword");
			keyword = menuAns.nextLine();
			if (!keyword.isEmpty()) break;
		}
		
		if (method.equals("1")) {
			searchSQL += "WHERE N.NID='" + keyword +"'";
		} else if (method.equals("2")) {
			searchSQL += "WHERE N.Family LIKE '%" + keyword + "%'";
		} else if (method.equals("3")) {
			searchSQL += "WHERE N.RType LIKE '%" + keyword + "%'";
		}

		stmt = mySQLDB.prepareStatement(searchSQL);
		
		System.out.println("|ID|Distance|Family|Duration|Energy|Resources|");
	
		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i<=6; i++){
				System.out.print("|" + resultSet.getString(i));
			}
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
	}

	public static void spacecraftSearch(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//NOTE: 1 issue(s)
		//1. output. The requirement text is inconsistant with the sample result screenshot
		

		String answer = "";
		String keyword = "";
		String searchSQL = "";
		PreparedStatement stmt = null;

		searchSQL += "SELECT S.Agency, S.MID, S.Num, S.Type, S.Energy, S.Duration, S.Capacity, S.Charge ";
		searchSQL += "FROM Spacecraft_Model S ";
		
		while (true) {
			System.out.println();
			System.out.println("Choose the search criterion:");
			System.out.println("1. Agency Name");
			System.out.println("2. Type");
			System.out.println("3. Least energy [km/s]");
			System.out.println("4. Least working time [days]");
			System.out.println("5. Least capacity [m^3]");
			System.out.print("My criterion:");
			answer = menuAns.nextLine();
			if (answer.equals("1") || answer.equals("2") || answer.equals("3") || answer.equals("4")
					|| answer.equals("5")) {
				break;
			} else {
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}

		while (true){
			System.out.print("Type in the search keyword");
			keyword = menuAns.nextLine();
			if (!keyword.isEmpty()) break;
		}
		
		if (answer.equals("1")) {
			searchSQL += "WHERE S.Agency=?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1,keyword);
		} else if (answer.equals("2")) {
			searchSQL += "WHERE S.Type=?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1,keyword);
		} else if (answer.equals("3")) {
			searchSQL += "WHERE S.Energy>?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setDouble(1,Double.parseDouble(keyword));
		} else if (answer.equals("4")) {
			searchSQL += "WHERE S.Duration>?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setInt(1,Integer.parseInt(keyword));
		} else if (answer.equals("5")) {
			searchSQL += "WHERE S.Capacity>?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setInt(1,Integer.parseInt(keyword));
		}
		
		System.out.println("|Agency|MID|SNum|Type|Energy|T|Capacity|Charge|");
		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i<=8; i++){
				System.out.print("|" + resultSet.getString(i));
			}
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
	}

	public static void certainDesign(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//TODO
	}

	public static void bestDesign(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//TODO
	}

	public static void customerMenu(Scanner menuAns, Connection mySQLDB) throws SQLException {//done
		String answer = "";

		while (true) {
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

			if (answer.equals("1")) {
				neaSearch(menuAns, mySQLDB);
			} else if (answer.equals("2")) {
				spacecraftSearch(menuAns, mySQLDB);
			} else if (answer.equals("3")) {
				certainDesign(menuAns, mySQLDB);
			} else if (answer.equals("4")) {
				bestDesign(menuAns, mySQLDB);
			} else if (answer.equals("0")) {
				break;
			} else {
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}
	}

	public static void rentSpacecraft(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//TODO
	}

	public static void returnSpacecraft(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//TODO
	}

	public static void listRentedByTime(Scanner menuAns, Connection mySQLDB) throws SQLException {

	}

	public static void listRentedNum(Scanner menuAns, Connection mySQLDB) throws SQLException {

	}

	public static void staffMenu(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String answer = "";

		while (true) {
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

			if (answer.equals("1")) {
				rentSpacecraft(menuAns, mySQLDB);
			} else if (answer.equals("2")) {
				returnSpacecraft(menuAns, mySQLDB);
			} else if (answer.equals("3")) {
				listRentedByTime(menuAns, mySQLDB);
			} else if (answer.equals("4")) {
				listRentedNum(menuAns, mySQLDB);
			} else if (answer.equals("0")) {
				break;
			} else {
				System.out.println("[Error]: Wrong Input, Type in again!!!");
			}
		}
	}

	public static void main(String[] args) {
		Scanner menuAns = new Scanner(System.in);
		System.out.println("Welcome to NEAs Exploration Mission Design System!");

		while (true) {
			try {
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

				if (answer.equals("1")) {
					adminMenu(menuAns, mySQLDB);
				} else if (answer.equals("2")) {
					customerMenu(menuAns, mySQLDB);
				} else if (answer.equals("3")) {
					staffMenu(menuAns, mySQLDB);
				} else if (answer.equals("0")) {
					break;
				} else {
					System.out.println("[Error]: Wrong Input, Type in again!!!");
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		}

		menuAns.close();
		System.exit(0);
	}
}
