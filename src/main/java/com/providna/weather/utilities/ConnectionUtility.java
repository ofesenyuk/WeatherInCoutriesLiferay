package com.providna.weather.utilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtility {

	private static ConnectionUtility connectionUtility = null;
	 
    private Connection connection = null;
    private final static String dbURL = "jdbc:mysql://localhost:3306/lportal62"; //"jdbc:mysql://localhost:3306/journaldev";
 
    private ConnectionUtility() {
    }
 
    public static ConnectionUtility getInstance() throws IOException, 
        IllegalAccessException, SQLException, ClassNotFoundException{
        // Synchronized against connectionUtility instance
    	System.out.println("ConnectionUtility getInstance()");
        synchronized(ConnectionUtility.class){
            // Check whether the connectionUtility is null or not
            if(connectionUtility == null){
                // Create a properties instance
                Properties properties = new Properties();
                // Load properties from classpath
//                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("connection.properties"));
                // Set connection with connectionUtility
                connectionUtility = new ConnectionUtility();
                // Load driver class
                Class.forName("com.mysql.jdbc.Driver");
                // Create connection
//                connectionUtiliy.setConnection(DriverManager.getConnection("jdbc:mysql://localhost:3306/journaldev"));
                properties.put("user", "user");
                properties.put("password", "user");
                connectionUtility.setConnection(DriverManager.getConnection(dbURL, properties));
            }
            return connectionUtility;
        }
    }
 
    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        if(connection.isClosed()){
            // Create a properties instance
            Properties properties = new Properties();
            // Load properties from classpath
            properties.load(Thread.currentThread().getContextClassLoader()
            		.getResourceAsStream("connection.properties"));
            // Load driver class
            Class.forName("com.mysql.jdbc.Driver");
            // Create connection
            connectionUtility.setConnection(DriverManager.getConnection(dbURL, properties));
        }
        System.out.println(connection.toString());
        return connection;
    }
 
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
