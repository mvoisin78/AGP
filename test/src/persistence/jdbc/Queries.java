package persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.QueriesPersistenceAPI;

public class Queries implements QueriesPersistenceAPI {
	private PreparedStatement preparedStatement = null;
	private ResultSet resultsSet;

	
	/**
	 * Print an error message to inform the user
	 */
	public void dataInit() {
		System.err.println("Please don't forget to create tables manually by importing config/create_tables.sql and config/insert_tahiti.sql in your database !");
	}
	
	/**
	 * Close the preparedStatement
	 */
	private void closePreparedStatement() {
		try {
			this.getPreparedStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize the iterator to the first element
	 * 
	 * @param results
	 */
	public void initIterator(ResultSet results){
		this.setResultsSet(results);
	}
	
	/**
	 * Get the next element of the iterator
	 */
	public boolean nextIterator(){
		boolean currentElement = false;
		try {
			currentElement = this.getResultsSet().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(currentElement == false){
			this.closePreparedStatement();
			return false;
		}
		return true;
	}

	
	/**
	 * Search by hotel price range
	 * 
	 * @param startRange
	 * @param endRange
	 */
	public void searchHotelByPrice(int startRange, int endRange) {
		try {
			String query = "SELECT * FROM hotel WHERE price <= ? AND price >= ?";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			
			this.preparedStatement.setInt(1, endRange);
			this.preparedStatement.setInt(2, startRange);
	
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	/**
	 * Get sites with the correspond type in database
	 * 
	 * @param type
	 */
	public void searchSiteByType(String type) {
		try {
			String query = "SELECT * FROM site WHERE type = ?";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			
			this.preparedStatement.setString(1, type);
	
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	/**
	 * Get all sites in database
	 * 
	 * @return result
	 */
	public void getSites() {
		try {
			String query = "SELECT * FROM site";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	/**
	 * Search by site price range
	 * 
	 * @param startRange
	 * @param endRange
	 * 
	 * @return result
	 */
	public void searchSitesByPrice(int startRange, int endRange) {
		try {
			String query = "SELECT * FROM site WHERE price <= ? AND price >= ?";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			
			this.preparedStatement.setInt(1, endRange);
			this.preparedStatement.setInt(2, startRange);
	
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	/**
	 * Get all rides (with information about sites and transport)
	 * 
	 * @param startRange
	 * @param endRange
	 * 
	 * @return result
	 */
	public void getRides() {
		try {
			String query = "SELECT ride.id_ride, siteS.*, siteE.*, transport.*" + 
							" FROM ride" + 
							" INNER JOIN site AS siteS ON siteS.id_site = ride.departure_site" + 
							" INNER JOIN site AS siteE ON siteE.id_site = ride.arrival_site" + 
							" INNER JOIN transport ON transport.id_transport = ride.id_transport;";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
	
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	public int getTotalPriceRidesAndHotel(ArrayList<Integer> idRides, int idHotel) {
		ResultSet result = null;
		ResultSet result1 = null;
		int totalPrice = 0;
		try {

			String query =	"SELECT transport.price, transport.is_per_km, coordS.latitude, coordS.longitude, coordE.latitude, coordE.longitude, siteS.price" + 
					" FROM ride" + 
					" INNER JOIN site AS siteS ON siteS.id_site = ride.departure_site" + 
					" INNER JOIN site AS siteE ON siteE.id_site = ride.arrival_site" + 
					" INNER JOIN coordinates AS coordS ON coordS.id_coordinates = siteS.id_coordinates" + 
					" INNER JOIN coordinates AS coordE ON coordE.id_coordinates = siteE.id_coordinates" + 
					" INNER JOIN transport ON transport.id_transport = ride.id_transport" + 
					" WHERE ride.id_ride IN (?)";
			
			for(int ride : idRides) {
				
				this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
				
				this.preparedStatement.setInt(1, ride);
				result = this.preparedStatement.executeQuery();
				
				result.next();
								
				totalPrice += result.getInt(7);
			    
			    if(result.getBoolean(2) == true) {   	
			    	
			    	totalPrice += (int) (business.ExcursionCalculator.getDistanceKM(result.getDouble(3),result.getDouble(4),result.getDouble(5),result.getDouble(6)) * result.getInt(1));
			    }else {
			    	totalPrice += result.getInt(1);
			    }
			    
			    this.preparedStatement.close();
			}

			query =	"SELECT *" + 
					" FROM hotel" +  
					" WHERE hotel.id_hotel = (?)";

			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			this.preparedStatement.setInt(1, idHotel);
	
			result1 = this.preparedStatement.executeQuery();
			result1.next();
			
			totalPrice += result1.getInt(3);
	
			this.preparedStatement.close();
			
			
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return totalPrice;
	}

	/**
	 * Get all sites in database
	 * 
	 * @return result
	 */
	public void getSitesOrderByActivity(String type) {
		try {
			String query = "SELECT * FROM site ORDER BY type = ? DESC";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(query);
			

			this.preparedStatement.setString(1, type);
	
			this.initIterator(this.preparedStatement.executeQuery());
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	/**
	 * Insert lines in database to add a site
	 * 
	 * @param name
	 * @param type
	 * @param price
	 * @param latitude
	 * @param longitude
	 * 
	 * @return idSite
	 */
	public int addSite(String name, String type, int price, double latitude, double longitude) {
		int idSite = 0;
		try {
			String addCoordinates = "INSERT INTO coordinates (latitude, longitude) VALUES (?, ?)";
	
			this.preparedStatement = JdbcConnection.getConnection().prepareStatement(addCoordinates);
			
			this.preparedStatement.setDouble(1, latitude);
			this.preparedStatement.setDouble(2, longitude);
	
			int result = this.preparedStatement.executeUpdate();
			
			ResultSet keys = this.preparedStatement.getGeneratedKeys();
			keys.next();
			int idCoordinates = keys.getInt(1);
			this.closePreparedStatement();
			
			if(result != 0) {
				String addSite = "INSERT INTO site (name, type, price, id_coordinates) VALUES (?, ?, ?, ?)";
			
				this.preparedStatement = JdbcConnection.getConnection().prepareStatement(addSite);
				
				this.preparedStatement.setString(1, name);
				this.preparedStatement.setString(2, type);
				this.preparedStatement.setInt(3, price);
				this.preparedStatement.setInt(4, idCoordinates);
		
				result = this.preparedStatement.executeUpdate();
				
				keys = this.preparedStatement.getGeneratedKeys();
				keys.next();
				idSite = keys.getInt(1);
				this.closePreparedStatement();
			}
			else {
				return 0;
			}
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		
		return idSite;
	}

	/**
	 * @return the preparedStatement
	 */
	private PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}


	/**
	 * @param preparedStatement the preparedStatement to set
	 */
	private void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}


	/**
	 * @return the resultsSet
	 */
	public ResultSet getResultsSet() {
		return resultsSet;
	}


	/**
	 * @param resultsSet the resultsSet to set
	 */
	private void setResultsSet(ResultSet resultsSet) {
		this.resultsSet = resultsSet;
	}
	
}
