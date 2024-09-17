package assignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class FlightBookingManager {
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/java1";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "";
	private Connection connection;

	// CONSTRUCTOR FOR ESHTABLISHING CONNECTION
	public FlightBookingManager() {
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
		} catch ( SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public static void main(String[] args) {
		FlightBookingManager manager = new FlightBookingManager();
		Scanner sc = new Scanner(System.in);
		// FLIGHT BOOKING MANAGEMENT SYSTEM
		while (true)
		{
			System.out.println("FLIGHT BOOKING MANAGEMENT SYSTEM");
			System.out.println("1.ADD FLIGHT");
			System.out.println("2.BOOK FLIGHT");
			System.out.println("3.SHOW FLIGHTS");
			System.out.println("4.VIEW PASSENGERS DETAILS");
			System.out.println("5.EXIT");
			System.out.println("CHOOSE AN OPTION:");
			int choice = sc.nextInt();
			
			switch(choice)
			{
			case 1: System.out.println("Enter Flight Number:");
					String flightNumber = sc.next();
					System.out.println("Enter origin:");
					String origin = sc.next();
					System.out.println("Enter Destination:");
					String destination = sc.next();
					System.out.println("Enter seats:");
					int seatsAvailable = sc.nextInt();
					
					manager.addFlights(flightNumber, origin, destination, seatsAvailable);
				break;
			case 2: 
					System.out.println("Enter Flight ID:");
					int flightId = sc.nextInt();
					System.out.println("Enter Passenger Name:");
					String passengerName = sc.next();
					System.out.println("Enter Passenger Contact:");
					String passengerContact = sc.next();
				
					manager.bookFlight(flightId,passengerName,passengerContact);
				break;
			case 3:
					manager.showFlights();
				break;
			case 4:
						System.out.println("Enter Flight ID to view passenger details");
						 int viewFlightId = sc.nextInt();
					manager.viewPassengerDetails(viewFlightId);
				break;
			case 5:
				//Exit code
				return;
		 default:
				System.out.println("Invalid Option:Try Again");
			}
			
		}
	}
			// FLIGHT ADD CODE IS HERE
			public void addFlights(String flightNumber,String origin,String destination,int seatsAvailable)
			{
			String query = 
				"INSERT into flights (flight_number,origin,destination,seats_available) "
				+ "VALUES (?,?,?,?)";   //departure_time,arrival_time,
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement(query);
				statement.setString(1, flightNumber);
				statement.setString(2, origin);
				statement.setString(3, destination);
				
				//Get the current Timestamp
				//Timestamp departure_time = Timestamp.valueOf(LocalDateTime.now());
				//Timestamp arrival_time = Timestamp.valueOf(LocalDateTime.now().plusHours(2));  // ASSUMING A 2-hour Flight duration demo
				
				//statement.setString(4, "Null");
				//statement.setString(5, "Null");
				statement.setInt(4, seatsAvailable);
				if (statement.executeUpdate() == 1)
				{
					System.out.println("Flight Details Inserted");
				}
				
				} catch (SQLException e) {
				System.out.println("addFlight statement not found");
				}
					
			}
			// FLIGHT BOOKING CODE IS HERE
			
			public void bookFlight(int flightId,String passengerName,String passengerContact)
			{
				String checkSeatsQuery = "SELECT seats_available FROM flights WHERE flight_id=?";
				
				String bookQuery = 
						"INSERT into bookings(booking_id,passenger_name,passenger_contact) VALUES (?,?,?)";
				
				String updateSeatsQuery = 
						"UPDATE flights SET seats_available=(seats_available) WHERE flight_id=?";
				
				PreparedStatement checkSeatsStmt = null;
				PreparedStatement bookStmt = null;
				PreparedStatement updateSeatsStmt = null;
				try {
					checkSeatsStmt = connection.prepareStatement(checkSeatsQuery); 
						
					bookStmt = connection.prepareStatement(bookQuery);
					
					updateSeatsStmt = connection.prepareStatement(updateSeatsQuery);
					
					// Check if flight exist and have available seats
					 
					checkSeatsStmt.setInt(1, flightId);
					ResultSet rs = checkSeatsStmt.executeQuery();  
					
					if(rs.next())
					{
						int seatsAvailable = rs.getInt("seats_available");
						
						if(seatsAvailable > 0 )
						  {
								// BOOK FLIGHT	
							
							bookStmt.setInt(1, flightId);
							bookStmt.setString(2, passengerName);
							bookStmt.setString(3, passengerContact);
							if(bookStmt.executeUpdate() == 1)
							{ System.out.println("Flight Booked Succesfully"); }
						
							
								//UPDATE COUNT
							
							updateSeatsStmt.setInt(1, flightId);
							if(updateSeatsStmt.executeUpdate() == 1)
							{
							System.out.println("Flight Booked Succesfully");
						  } }
						else {	System.out.println("No seats available"); 	} 
					} 	else {	System.out.println("Flight not found");     }
				} catch (SQLException e) {
					System.out.println("Check bookFlight Stmt");
				}
			}
			
			// SHOW FLIGHT CODE IS HERE
			public void showFlights()
			{
				String query = "SELECT * from flights";
				
				try {
					PreparedStatement statement = connection.prepareStatement(query);
					ResultSet rs = statement.executeQuery();{
						while(rs.next())
						{
						System.out.println("Flight ID: "+rs.getInt("flight_id"));
						System.out.println("Flight Number: "+rs.getString("flight_number"));
						System.out.println("Origin: "+rs.getString("origin"));
						System.out.println("Destination: "+rs.getString("destination"));
						System.out.println("Departure Time: "+rs.getString("departure_time"));
						System.out.println("Arrival Time: "+rs.getString("arrival_time"));
						System.out.println("Seats Available: "+rs.getString("seats_available"));
						System.out.println("---------------------");
						}
					}
				} catch (SQLException e) {
					System.out.println("Check Show Flight Statement ");
				}
			}
			
			// VIEW PASSENGER DETAILS
			public void viewPassengerDetails(int flightId)
			{
				String query = "SELECT * FROM bookings WHERE flight_id=?";
				
				try {
					PreparedStatement statement = connection.prepareStatement(query);
					
					statement.setInt(1, flightId);
					
					ResultSet rs = statement.executeQuery();
					
					
					while (rs.next())
					{
						System.out.println("Booking ID"+rs.getInt("booking_id"));
						
						System.out.println("Passenger Name"+rs.getString("passenger_name"));
						
						System.out.println("Passenger Contact"+rs.getString("passenger_contact"));
						
						System.out.println("---------------------");
						
					}
					
				} catch (SQLException e) {
					System.out.println("Check PassengerDetails Statement");
				}	
			}
			
			public void exit() throws InterruptedException {
				System.out.println("EXITING ");
				
				int i = 5;
				
				while(i != 0)
				{
					try {
						Thread.sleep(450);
						System.out.print(". ");
						i--;
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
				}
				System.out.println();
				System.out.println("THANKS FOR USING FLIGHT BOOKING SYSTEM ✈️");
				
			}			
			
			
}





















