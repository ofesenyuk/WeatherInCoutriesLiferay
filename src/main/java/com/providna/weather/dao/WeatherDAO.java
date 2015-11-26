package com.providna.weather.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.WebServiceException;

import net.webservicex.GlobalWeather;
import net.webservicex.GlobalWeatherSoap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.providna.weather.data.WeatherData;
import com.providna.weather.data.WeatherStorage;
import com.providna.weather.utilities.ConnectionUtility;

public class WeatherDAO {

	public static WeatherDAO weatherDAO = null;
	
	private GlobalWeatherSoap serviceProxy;
	 
    private WeatherDAO(){
 
    }
 
    public static WeatherDAO getInstance(){
        synchronized(WeatherDAO.class){
            if(weatherDAO == null){
            	weatherDAO = new WeatherDAO();
            }
 
        }
        return weatherDAO;
    }
    
    public void saveXMLrecordToDB(String country, String xmlRecord) throws SQLException, 
        IllegalAccessException, IOException, ClassNotFoundException{
    	/**
    	  CREATE TABLE `weather` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `COUNTRY` varchar(45) DEFAULT NULL,
  `XML_RECOR` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
    	 */
        // Get connection instance
        Connection connection = ConnectionUtility.getInstance().getConnection();
        // Create Prepared Statement
        PreparedStatement query = connection.prepareStatement("INSERT "
        		+ "INTO weather (COUNTRY, XML_RECOR) VALUES (?,?)");
        // Set variables
        query.setString(1, country);
        query.setString(2, xmlRecord);
 
        try {
            // Execute
            int res = query.executeUpdate();
            System.out.println("saveToDB query " + query.toString()  + " " + res);
            return;
        }
        catch(Exception e){
            // Close statement
            query.close();
            // Close connection
            connection.close();
            // Throw another exception for notifying the Servlet
            throw new SQLException(e);
        }
    }
    
    public List<WeatherData> retrieveFromDB() {
    	
    	String selectTableSQL = "SELECT XML_RECOR from weather WHERE "
    			+ "COUNTRY = '" + WeatherStorage.getCountry() + "'";
    	Connection connection;
    	List<String> xmlData = new ArrayList<>();
		try {
			connection = ConnectionUtility.getInstance().getConnection();
	    	Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(selectTableSQL);
	    	while (rs.next()) {
	    		String xml = rs.getString("XML_RECOR");
	    		xmlData.add(xml);	    	
	    	}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String weatherResult : xmlData) {
			WeatherData wData = new WeatherData();
		    setDataFromXML(weatherResult, wData);
		    WeatherStorage.getWeatherStates().add(wData);
		}
		System.out.println("retrieveFromDB " 
		    + WeatherStorage.getWeatherStates().size());
		return WeatherStorage.getWeatherStates();
    }

	public List<String> retrieveCountriesFromInet() {
		// TODO Auto-generated method stub

		List<String> countries = new ArrayList<String>();
	    String url = "http://www.webservicex.net/country.asmx/GetCountries";
		String tagName = "Name";
		// System.setProperty("http.proxyType", "4");
        // System.setProperty("http.proxySet", "true");
        // System.setProperty("http.proxyHost", "your proxy host");
        // System.setProperty("http.proxyPort", "your proxy port");
        // System.setProperty("http.proxyDomain", "your proxy domain");

        URL wsURL = null;
		try {
			wsURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return countries;
		}
        URLConnection uc;
		try {
			uc = wsURL.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return countries;
		}

        BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return countries;
	    }
        String inputLine;

        String xmlResult = "";
        try {
			while ((inputLine = in.readLine()) != null) {
			      inputLine = inputLine.replaceAll("&lt;", "<");
			      inputLine = inputLine.replaceAll("&gt;", ">");

			      xmlResult += inputLine;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
		        try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return countries;
				}
			}
		}
		countries = parseListFromXML(xmlResult, tagName);	    
	    System.out.println("countries.size(): " + countries.size());
		return countries;
	}
	
	public List<String> parseListFromXML(String xmlResult, String tagName) {
		// TODO Auto-generated method stub
		List<String> parsedList= new ArrayList<>();
		try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlResult));
            Document doc = db.parse(is);

            NodeList nodes = doc.getElementsByTagName(tagName);

            for (int i = 0; i < nodes.getLength(); i++) {
                  String node = nodes.item(i).getChildNodes().item(0).getTextContent();
                  parsedList.add(node);
            }
        } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
		return parsedList;
	}

	public List<String> retrieveCountriesFromDB() {
		// TODO Auto-generated method stub
		String selectTableSQL = "SELECT DISTINCT COUNTRY from weather";
    	Connection connection;
    	List<String> countries = new ArrayList<>();
		try {
			connection = ConnectionUtility.getInstance().getConnection();
	    	Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(selectTableSQL);
			System.out.println(selectTableSQL);
	    	while (rs.next()) {
	    		String country = rs.getString("COUNTRY");
	    		countries.add(country);	 
				System.out.println("from SQL " + country);   	
	        }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countries;
	}

	public List<String> retrieveCitiesFromInet(String country) {
		// TODO Auto-generated method stub

		GlobalWeather globalWeather = null;
		try {
			globalWeather 
			    = new GlobalWeather(new URL("http://www.webservicex.net"
			    		+ "/globalweather.asmx?WSDL"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (WebServiceException e1) {
			// TODO Auto-generated catch block
			return new ArrayList<String>();
		} 
		serviceProxy = globalWeather.getGlobalWeatherSoap12();
		String citiesResult = serviceProxy.getCitiesByCountry(country);
		System.out.println("citiesResult " + citiesResult);
		List<String >cities = parseListFromXML(citiesResult, "City");
		System.out.println("cities size " + cities.size());
//		for (String city : cities) {
//			String weatherResult = serviceProxy.getWeather(city, country);
//			System.out.println("weatherResult " + weatherResult);
//		}
		return cities;
	}

	public List<String> retrieveCitiesFromDB() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> retrieveCitiesFromDB(String country) {
		// TODO Auto-generated method stub
		String selectTableSQL = "SELECT XML_RECOR from weather WHERE "
    			+ "COUNTRY = '" + country + "'";
    	Connection connection;
    	List<String> cities = new ArrayList<>();
		try {
			connection = ConnectionUtility.getInstance().getConnection();
	    	Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(selectTableSQL);
			System.out.println(selectTableSQL);
	    	while (rs.next()) {
	    		String xmlRes = parseListFromXML(rs.getString("XML_RECOR"), 
	    				"Location").get(0);
	    		String city = xmlRes.contains(country) 
	    				? xmlRes.substring(0, xmlRes.indexOf(country)) : "";
	    		city = city.contains(",") 
	    				? city.substring(0, city.indexOf(",")) : city;
	    		cities.add(city);	 
				System.out.println("from SQL " + city);   	
	        }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cities;
	}

	public void saveXMLToDB() {
		// TODO Auto-generated method stub
		;
//		synchronized(dao) {
//			while (!isWeaterDataFull) {
//				try {
//					dao.wait();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				}
//			}
//		}
//		List<String> xmlWeatherResponses = new ArrayList<>();
//		for (int i = 0; i < WeatherStorage.getCities().size() 
//				&& i < WeatherStorage.maxRecordsToView; i++) {
//		       String city = WeatherStorage.getCities().get(i);
//		       System.out.println("writeLastData running " 
//		           + city + " " + i);
//		       String weatherResult = serviceProxy.getWeather(city, 
//		    		   country);
//		       if (!weatherResult.contains(country)) {
//					continue;
//				}
//				xmlWeatherResponses.add(weatherResult);
//		}
//		WeatherStorage.getXmlWeatherResponses().addAll(xmlWeatherResponses);
//		System.out.println("saveDataToDB xmlWeatherResponses" 
//		    + xmlWeatherResponses.size());
		for (String record : WeatherStorage.getXmlWeatherResponses()) {
			try {
				saveXMLrecordToDB(WeatherStorage.getCountry(), record);
				System.out.println("saveXMLToDB: " + record);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<WeatherData> retrieveWeatherFromInet() {
		// TODO Auto-generated method stub

//		FacesContext context = FacesContext.getCurrentInstance();
//		final LocationManagedBean loc = (LocationManagedBean) context.getELContext()
//				.getELResolver().getValue(context.getELContext(), null, 
//						"locationManagedBean");
//		final GlobalWeatherSoap serviceProxy = loc.serviceProxy;
		final List<String> cities = WeatherStorage.getCities();
		final String country = WeatherStorage.getCountry();
		List<WeatherData> weatherStates = new ArrayList<>();
		if (!WeatherStorage.getWeatherStates().isEmpty() 
				&& WeatherStorage.getWeatherStates().get(0).getLocation()
				.contains(country) || !WeatherStorage.isWeaterDataFull()) {
			return WeatherStorage.getWeatherStates();
		}
		System.out.println(cities.size() + " cities in country " + country);
		
		for (int i = 0; i < cities.size() 
				&& i < WeatherStorage.maxRecordsToView; i++) {
			String city = cities.get(i);
			System.out.println("city " + city);
			String weatherResult = serviceProxy.getWeather(city, country);
			WeatherData wData = new WeatherData();
			System.out.println("weatherResult " + weatherResult);
			if (!weatherResult.contains(country)) {
				continue;
			}
			WeatherStorage.getXmlWeatherResponses().add(weatherResult);
			setDataFromXML(weatherResult, wData);
			System.out.println("setVisibility " + wData.getVisibility() + " " 
			    + wData.getWind());
			weatherStates.add(wData);
		}
		WeatherStorage.setWeatherStates(weatherStates);
		Thread writeLastDataThread = new Thread() {
			public void run() {
				for (int i = WeatherStorage.maxRecordsToView; 
						i < cities.size(); i++) {
					WeatherStorage.setWeaterDataFull(false);
				    String city = cities.get(i);
				    System.out.println("writeLastDataThread running " 
				        + city + " " + i);
				    String weatherResult = serviceProxy.getWeather(city, 
				        country);
				    if (!weatherResult.contains(country)) {
						continue;
					}
				    synchronized(WeatherStorage.getXmlWeatherResponses()) {
					    WeatherStorage.getXmlWeatherResponses()
					        .add(weatherResult);
				    }
				    WeatherData wData = new WeatherData();
				    setDataFromXML(weatherResult, wData);
				    synchronized(WeatherStorage.getWeatherStates()) {
				    	WeatherStorage.getWeatherStates().add(wData);
				    }
				}
				WeatherStorage.setWeaterDataFull(true);
			}
		};
		writeLastDataThread.start();
//		loc.setXmlWeatherResponses(xmlWeatherResponses);
		System.out.println(weatherStates.size() + " weatherStates size ");
//		weatherStatesToView.addAll(weatherStates.subList(0, maxRecordsToView));
		return WeatherStorage.getWeatherStates();
	}

	public void setDataFromXML(String weatherResult, WeatherData wData) {
		// TODO Auto-generated method stub

		wData.setDewPoint(parseItemFromXML(weatherResult, WeatherStorage.DEW_POINT));
//		System.out.println("setDewPoint ");
		wData.setLocation(parseItemFromXML(weatherResult, WeatherStorage.LOCATION));
//		System.out.println("setLocation ");
		wData.setPressure(parseItemFromXML(weatherResult, WeatherStorage.PRESSURE));
		wData.setRelativeHumidity(parseItemFromXML(weatherResult, 
				WeatherStorage.RELATIVE_HUMIDITY));
		wData.setStatus(parseItemFromXML(weatherResult, 
				WeatherStorage.STATUS));
		wData.setTemperature(parseItemFromXML(weatherResult, 
				WeatherStorage.TEMPERATURE));
		wData.setTime(parseItemFromXML(weatherResult, 
				WeatherStorage.TIME));
		wData.setVisibility(parseItemFromXML(weatherResult, 
				WeatherStorage.VISIBILITY));
		wData.setWind(parseItemFromXML(weatherResult, WeatherStorage.WIND));
		
	}

	private String parseItemFromXML(String xmlResult, String tagName) {
		// TODO Auto-generated method stub
		try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlResult));
            Document doc = db.parse(is);

            NodeList nodes = doc.getElementsByTagName(tagName);
            return (nodes.getLength() == 0 ? "" : nodes.item(0).getChildNodes()
            		.item(0).getTextContent());
        } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
		return "";
	}
}
