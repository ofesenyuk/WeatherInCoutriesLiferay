package com.providna.weather.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.providna.weather.dao.WeatherDAO;

public class WeatherStorage {
	
	public static  final String DEW_POINT = "DewPoint";
	public static  final String LOCATION = "Location";
	public static  final String PRESSURE = "Pressure";
	public static  final String RELATIVE_HUMIDITY = "RelativeHumidity";
	public static  final String STATUS = "Status";
	public static  final String TEMPERATURE = "Temperature";
	public static  final String TIME = "Time";
	public static  final String VISIBILITY = "Visibility";
	public static  final String WIND = "Wind";
	public static final int maxRecordsToView = 5;

	private static String country;
	private static List<String> countries = new ArrayList<>();
	private static List<String> cities = new ArrayList<>();;
	private static String city;
	
	private static List<String> xmlWeatherResponses = new ArrayList<>();
	private static List<WeatherData> weatherStates = new ArrayList<>();
	
	private static boolean isWeaterDataFull = true;
	private static boolean isCountriesListFull = false;

	public static String getCountry() {
		return country;
	}

	public static void setCountry(String country) {
		if (WeatherStorage.country != country) {
			cities.clear();
			xmlWeatherResponses.clear();
			weatherStates.clear();
			isWeaterDataFull = true;
			cities.addAll(WeatherDAO.getInstance()
					.retrieveCitiesFromInet(country));
			if (cities.isEmpty()) {
				cities.addAll(WeatherDAO.getInstance()
						.retrieveCitiesFromDB(country));
			}
		}
		WeatherStorage.country = country;
	}

	public static List<String> getCountries() {
//		if (countries.isEmpty()) {
			Thread fromNet = new Thread() {
				public void run() {
					isCountriesListFull = false;
					System.out.println("Thread fromNet");
					List<String> countriesI = 
							WeatherDAO.getInstance()
							.retrieveCountriesFromInet();
					if (null == countriesI || countriesI.isEmpty()) {
						return;
					}
					Set<String> countriesSet = new HashSet<>(countries);
					countriesSet.addAll(countriesI);
					synchronized(countries) {
						countries = new ArrayList<String>(countriesSet);
						Collections.sort(countries);
					}
					isCountriesListFull = true;
			    }
			};
			if (!isCountriesListFull) {
				fromNet.start();
			}
//		}
		if (countries.isEmpty()) {
			countries.addAll(WeatherDAO.getInstance().retrieveCountriesFromDB());
		}
		return countries;
	}

	public static void setCountries(List<String> countries) {
		WeatherStorage.countries = countries;
	}

	public static List<String> getCities() {
		return cities;
	}

	public static void setCities(List<String> cities) {
		WeatherStorage.cities = cities;
	}

	public static String getCity() {
		return city;
	}

	public static void setCity(String city) {
		WeatherStorage.city = city;
	}

	public static boolean isWeaterDataFull() {
		return isWeaterDataFull;
	}

	public static void setWeaterDataFull(boolean isWeaterDataFull) {
		WeatherStorage.isWeaterDataFull = isWeaterDataFull;
	}

	public static List<String> getXmlWeatherResponses() {
		return xmlWeatherResponses;
	}

	public static void setXmlWeatherResponses(List<String> xmlWeatherResponses) {
		WeatherStorage.xmlWeatherResponses = xmlWeatherResponses;
	}

	public static List<WeatherData> getWeatherStates() {
		return weatherStates;
	}

	public static void setWeatherStates(List<WeatherData> weatherStates) {
		WeatherStorage.weatherStates = weatherStates;
	}

	public static boolean isCountriesListFull() {
		return isCountriesListFull;
	}

	public static void setCountriesListFull(boolean isCountriesListFull) {
		WeatherStorage.isCountriesListFull = isCountriesListFull;
	}
}
