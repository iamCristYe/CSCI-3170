import java.util.Scanner;
import java.util.Date;
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
		//String Resource_SQL = "INSERT INTO Resource (RType, Density, Value) VALUES (?,?,?)";
		//String NEA_SQL = "INSERT INTO NEA (NID, Distance, Family, Duration, Energy, RType) VALUES (?,?,?,?,?,?)";
		//String Spacecraft_Model_SQL = "INSERT INTO Spacecraft_Model (Agency, MID, Num, Charge, Duration, Energy, Capacity, Type) VALUES (?,?,?,?,?,?,?,?)";
		//String RentalRecord_SQL = "INSERT INTO RentalRecord (Agency, MID, SNum, CheckoutDate, ReturnDate) VALUES (?,?,?,STR_TO_DATE(?,'%d-%m-%Y'),STR_TO_DATE(?,'%d-%m-%Y'))";

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
			PreparedStatement stmt = mySQLDB.prepareStatement("INSERT INTO Resource (RType, Density, Value) VALUES (?,?,?)");
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
			PreparedStatement stmt = mySQLDB.prepareStatement("INSERT INTO NEA (NID, Distance, Family, Duration, Energy, RType) VALUES (?,?,?,?,?,?)");
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
			PreparedStatement stmt = mySQLDB.prepareStatement("INSERT INTO Spacecraft_Model (Agency, MID, Num, Charge, Duration, Energy, Capacity, Type) VALUES (?,?,?,?,?,?,?,?)");

			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/spacecrafts.txt"));
			dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString(1, dataFields[0]);						//Agency
				stmt.setString(2, dataFields[1]);						//MID
				stmt.setInt(3, Integer.parseInt(dataFields[2]));		//Num
				stmt.setInt(4, Integer.parseInt(dataFields[7]));		//Charge
				stmt.setInt(5, Integer.parseInt(dataFields[5]));		//Duration
				stmt.setDouble(6, Double.parseDouble(dataFields[4]));	//Energy
				if (dataFields[6].equals("null"))
					stmt.setNull(7, java.sql.Types.INTEGER);
				else
					stmt.setInt(7, Integer.parseInt(dataFields[6]));	//Capacity
				stmt.setString(8, dataFields[3]);						//Type
				stmt.addBatch();
			}

			stmt.executeBatch();
			stmt.close();
			dataReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			PreparedStatement stmt = mySQLDB.prepareStatement("INSERT INTO RentalRecord (Agency, MID, SNum, CheckoutDate, ReturnDate) VALUES (?,?,?,STR_TO_DATE(?,'%d-%m-%Y'),STR_TO_DATE(?,'%d-%m-%Y'))");
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
		//NOTE: 2 issue(s)
		//1. output. The requirement text is inconsistant with the sample result screenshot
		//2. Num or SNum? Do we want the number of a certain MODEL in the search result,
		//or the SNum for each INSTANCE?

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
		//Testdata: FUCKSHIT not available in NEA list
		//Testdata: 2008GD110 no resource
		//Testdata: ??? very large Duration/Energy?
		//For Duration/Energy, do we use > or >=? AGAIN not consistant in project.pdf (Here I used >= as stated in section 4.3)
		String answer = "";
		while (true){
			System.out.print("Typing in the NEA ID:");
			answer = menuAns.nextLine();
			if (!answer.isEmpty()) break;
		}
		String NID = answer;

		String indexSQL = "";
		//Create a view of int range(1,100), for converting spacecraft_model Num to spacecraft SNum
		indexSQL += "CREATE OR REPLACE VIEW singles AS ";
		indexSQL += "SELECT 0 single ";
		indexSQL += "UNION ALL SELECT   1 UNION ALL SELECT   2 UNION ALL SELECT   3 ";
		indexSQL += "UNION ALL SELECT   4 UNION ALL SELECT   5 UNION ALL SELECT   6 ";
		indexSQL += "UNION ALL SELECT   7 UNION ALL SELECT   8 UNION ALL SELECT   9; ";
		PreparedStatement stmt = mySQLDB.prepareStatement(indexSQL);
		stmt.execute();
		indexSQL = "";
		indexSQL += "CREATE OR REPLACE VIEW numbers AS ";
		indexSQL += "SELECT (S1.single * 10 + S2.single) AS number ";
		indexSQL += "FROM singles S1, singles S2 ";
		indexSQL += "WHERE (S1.single + S2.single) >0 ";
		indexSQL += "ORDER BY number ASC ;";
		stmt = mySQLDB.prepareStatement(indexSQL);
		stmt.execute();

		String smSQL = "";
		//Create a view of Spacecraft Models that are capable to do the job
		smSQL += "CREATE OR REPLACE VIEW models AS ";
		smSQL += "SELECT S.Agency, S.MID, S.Num, (N.Duration*S.Charge) AS Cost, (1000000*R.Density*R.Value*S.Capacity-N.Duration*S.Charge) AS Benefit ";
		smSQL += "FROM Resource R, NEA N, Spacecraft_Model S ";
		smSQL += "WHERE R.RType=N.RType ";
		smSQL += "AND S.Type='A' AND S.Energy>=N.Energy AND S.Duration>=N.Duration ";
		smSQL += "AND N.NID=? ;";
		stmt = mySQLDB.prepareStatement(smSQL);
		stmt.setString(1,NID);
		stmt.execute();

		String spacecraftSQL = "";
		//Query of spacecrafts that are avalible to be rented
		//If a spacecraft does not have a rental record, we regard it as one that has never been rented
		spacecraftSQL += "SELECT M.Agency, M.MID, NO.number AS SNum, M.Cost, M.Benefit ";
		spacecraftSQL += "FROM models M, numbers NO ";
		spacecraftSQL += "WHERE M.Num>=NO.number ";
		spacecraftSQL += "AND (M.Agency, M.MID, NO.number) NOT IN ( ";
		spacecraftSQL += "    SELECT D.Agency, D.MID, D.SNum ";
		spacecraftSQL += "    FROM RentalRecord D ";
		spacecraftSQL += "    WHERE D.ReturnDate IS NULL) ";
		spacecraftSQL += "ORDER BY M.Benefit DESC, SNum ASC; ";
		stmt = mySQLDB.prepareStatement(spacecraftSQL);
		
		System.out.println("|Agency|MID|SNum|Cost|Benefit|");
		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i<=5; i++){
				System.out.print("|" + resultSet.getString(i));//TODO: Adjust output format
			}
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();

		String dropViewSQL = "";
		dropViewSQL	+= "DROP VIEW IF EXISTS singles, numbers, models; ";
		stmt = mySQLDB.prepareStatement(dropViewSQL);
		stmt.execute();
		stmt.close();

		//OLD ALGO (NOT FINISHED):
		//Double Density = 0;
		//Double Value =0;
		// PreparedStatement stmt = mySQLDB.prepareStatement("SELECT R.Density, R.Value from Resource R, NEA where NEA.RType=R.RType and NEA.NID=?;");		
		// stmt.setString(1,NID);
		// ResultSet rs = stmt.executeQuery();
		// if (!rs.next()) {  //empty set returned
		// 	System.out.print("[Error]: NEA not found or NEA has no resource."); 
		// } else {
		// 		Density=rs.getDouble(1);
		// 		Value=rs.getDouble(2);
		// 		String viewSQL = ""
		// 		viewSQL += "CREATE VIEW goodSM AS SELECT SM.Agency, SM.MID, SM.Num, SM.Charge, SM.Capacity from Spacecraft_Model SM,NEA where SM.Type='A' and SM.Energy>NEA.Energy and SM.Duration>NEA.Duration and NEA.NID=?;"
		// 		viewSQL += "CREATE VIEW goodS AS SELECT  SM.Agency, SM.MID, SM.Num, SM.Charge, SM.Capacity;"
		// 		stmt = mySQLDB.prepareStatement();

		// 		stmt.setString(1,NID);
		// 		ResultSet rs = stmt.executeQuery();
		// 		stmt = mySQLDB.prepareStatement("SELECT * FROM goodSM;")
		// 		if (!rs.next()) {  //empty set returned
		// 			System.out.print("[Error]: No Spacecraft is able to carry out the mission as NEA's Duration is too long or NEA's Energy is too high."); 
		// 		} 
		// 		else{
					
		// 			while(rs.next()){
		// 				for (int i = 1; i<=8; i++){
		// 					listOfStringArrays.add(new String[] {resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5)});
		// 				}
	
		// 			}

		// 			Collections.sort(listOfStringArrays,new Comparator<String[]>() {
		// 				public int compare(String[] strings, String[] otherStrings) {
		// 					return strings[1].compareTo(otherStrings[1]);
		// 				}
		// 			});

		// 		}
		// 	}
		// }
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
		//Testdata: RSA 0199 5 not available in spacecraft list
		//Testdata: RSA 0198 9 SNum > Num
		//Testdata: RSA 0292 5 returned
		//Testdata: RSA 0292 6 unreturned
		//Testdata: RSA 0219 2 unable to find in RentalRecord (need add entry)

		System.out.print("Enter the space agency name:");
		String agency=menuAns.nextLine(); 
		System.out.print("Enter the MID:");
		String mid=menuAns.nextLine(); 
		System.out.print("Enter the SNum:");
		int snum=Integer.parseInt(menuAns.nextLine()); 

		PreparedStatement stmt = mySQLDB.prepareStatement("select SM.Num from Spacecraft_Model SM where SM.Agency = ? AND SM.MID= ?");
		stmt.setString(1, agency);
		stmt.setString(2, mid);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			System.out.print("[Error]: Rental not possible because the spacecraft is not found."); //not available in spacecraft list
		} else if (snum>Integer.parseInt(rs.getString(1))) {
			System.out.print("[Error]: Rental not possible because the spacecraft is not found."); //SNum > Num
		}
		else {
			stmt = mySQLDB.prepareStatement("select RR.ReturnDate from RentalRecord RR where RR.Agency = ? AND RR.MID= ? AND RR.SNum =?");
			stmt.setString(1, agency);
			stmt.setString(2, mid);
			stmt.setInt(3, snum);
			rs = stmt.executeQuery();
			
			if (rs.next()&&rs.getString(1)==null ){//unreturned
				System.out.print("[Error]: Rental not possible because the spacecraft has not yet been returned.");
			} else {
				stmt = mySQLDB.prepareStatement("INSERT INTO RentalRecord (Agency, MID, SNum,CheckoutDate,ReturnDate) VALUES(?, ?, ?,?,?) ON DUPLICATE KEY UPDATE CheckoutDate=?, ReturnDate=?");
				stmt.setString(1, agency);
				stmt.setString(2, mid);
				stmt.setInt(3, snum);
				java.util.Date utilDate = new java.util.Date();
   				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				stmt.setDate(4, sqlDate);
				stmt.setNull(5, java.sql.Types.DATE);
				stmt.setDate(6, sqlDate);
				stmt.setNull(7, java.sql.Types.DATE);
				stmt.executeUpdate();
				System.out.print("Spacecraft rented successfully!");
			}
		}

		rs.close();
		stmt.close();
	}

	public static void returnSpacecraft(Scanner menuAns, Connection mySQLDB) throws SQLException {
		//Testdata: RSA 0199 5 not available in spacecraft list
		//Testdata: RSA 0198 9 SNum > Num
		//Testdata: RSA 0292 5 returned
		//Testdata: RSA 0292 6 unreturned
		
		System.out.print("Enter the space agency name:");
		String agency=menuAns.nextLine(); 
		System.out.print("Enter the MID:");
		String mid=menuAns.nextLine(); 
		System.out.print("Enter the SNum:");
		int snum=Integer.parseInt(menuAns.nextLine()); 

		PreparedStatement stmt = mySQLDB.prepareStatement("select SM.Num from Spacecraft_Model SM where SM.Agency = ? AND SM.MID= ?");
		stmt.setString(1, agency);
		stmt.setString(2, mid);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			System.out.print("[Error]: Return not possible because the spacecraft is not found."); //not available in spacecraft list
		} else if (snum>Integer.parseInt(rs.getString(1))) {
			System.out.print("[Error]: Return not possible because the spacecraft is not found."); //SNum > Num
		}
		else {
			stmt = mySQLDB.prepareStatement("select RR.ReturnDate from RentalRecord RR where RR.Agency = ? AND RR.MID= ? AND RR.SNum =?");
			stmt.setString(1, agency);
			stmt.setString(2, mid);
			stmt.setInt(3, snum);
			rs = stmt.executeQuery();
			
			if (rs.next()&&rs.getString(1)==null ){//unreturned
				stmt = mySQLDB.prepareStatement("INSERT INTO RentalRecord (Agency, MID, SNum) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE ReturnDate=?");
				stmt.setString(1, agency);
				stmt.setString(2, mid);
				stmt.setInt(3, snum);
				java.util.Date utilDate = new java.util.Date();
   				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				stmt.setDate(4, sqlDate);
				stmt.setNull(5, java.sql.Types.DATE);
				stmt.executeUpdate();
				System.out.print("Spacecraft returned successfully!");				
			} else {
				System.out.print("[Error]: Return not possible because the spacecraft has not yet been rented.");				
			}
		}

		rs.close();
		stmt.close();
	}

	public static void listRentedByTime(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String startTime = "";
		PreparedStatement stmt = mySQLDB.prepareStatement("SELECT RR.Agency, RR.MID, RR.SNum, RR.CheckoutDate FROM RentalRecord RR where RR.CheckoutDate>=STR_TO_DATE(?,'%d-%m-%Y') and RR.CheckoutDate<=STR_TO_DATE(?,'%d-%m-%Y') order by CheckoutDate desc ");
		
		System.out.println();
		System.out.print("Typing in the starting date [DD-MM-YYYY]:");
		stmt.setString(1,menuAns.nextLine());
		System.out.print("Typing in the ending date [DD-MM-YYYY]:");
		stmt.setString(2,menuAns.nextLine());
		
		System.out.println("List of the unrented spacecraft:");
		System.out.println("|Agency| MID|SNum|Checkout Date|");
		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			System.out.print("|" + String.format("%1$6s", resultSet.getString(1)));   
			System.out.print("|" + String.format("%1$3s", resultSet.getString(2)));   
			System.out.print("|" + String.format("%1$4s", resultSet.getString(3)));   
			System.out.print("|" + String.format("%1$13s", resultSet.getString(4)));   
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
	}

	public static void listRentedNum(Scanner menuAns, Connection mySQLDB) throws SQLException {
		String startTime = "";
		PreparedStatement stmt = mySQLDB.prepareStatement("select RR.Agency,count(*) from RentalRecord RR where RR.returndate is null group by RR.agency order by agency asc; ");
		System.out.println("|Agency|Number|");
		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			System.out.print("|" + String.format("%1$6s", resultSet.getString(1)));   
			System.out.print("|" + String.format("%1$6s", resultSet.getString(2)));
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
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
