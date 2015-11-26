package com.providna.weather.beans;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.providna.weather.data.WeatherStorage;

@ManagedBean
@SessionScoped
public class LocationManagedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3919809177427233426L;
	private static final String NOT_FULL_MESSAGE = "Данные отображены не "
			+ "полностью. Чтобы отобразить все данные, перезагрузите страницу "
			+ "позже.";
	private static final String FULL_MESSAGE = "Данные считаны полностью."; // "Данные считаны полностью.";
	private String isCountriesListFullMessage;
	private List<String> countries = new ArrayList<>();
	private String country;
	private List<String> cities;
	private String city;
	private boolean isWeaterDataFull = true;
		
	public void init() {
		System.out.println("LocationManagedBean init");
		countries = WeatherStorage.getCountries();
//		FacesContext context = FacesContext.getCurrentInstance();
//		final WeatherFromNetManagedBean weath 
//		    = (WeatherFromNetManagedBean) context.getELContext()
//				.getELResolver().getValue(context.getELContext(), null, 
//						"weatherFromNetManagedBean");
//		weath.getWeatherStates().clear();
//		weath.getXmlWeatherResponses().clear();
    }

//	public List<String> parseListFromXML(String xmlResult, String tagName) {
//		// TODO Auto-generated method stub
//		List<String> parsedList= new ArrayList<>();
//		try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource(new StringReader(xmlResult));
//            Document doc = db.parse(is);
//
//            NodeList nodes = doc.getElementsByTagName(tagName);
//
//            for (int i = 0; i < nodes.getLength(); i++) {
//                  String node = nodes.item(i).getChildNodes().item(0).getTextContent();
//                  parsedList.add(node);
//            }
//        } catch (Exception e) {
//          System.out.println("Error: " + e.getMessage());
//        }
//		return parsedList;
//	}
	
	public List<String> getCountries() {
		init();
		System.out.println("getCountries " + 
		   (null == countries || countries.size() == 0 ? "null" : ""));
		if (WeatherStorage.isCountriesListFull()) {
			isCountriesListFullMessage =FULL_MESSAGE;
		} else {
			isCountriesListFullMessage = NOT_FULL_MESSAGE;
		}
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		System.out.println("setCountry " + country);
		this.country = country;
		WeatherStorage.setCountry(country);
	}

	public List<String> getCities() {
		cities = WeatherStorage.getCities();
		return cities;
	}

	public void setCities(List<String> cities) {
		WeatherStorage.setCities(cities);
		this.cities = cities;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		WeatherStorage.setCity(city);
		this.city = city;
	}

	public boolean isWeaterDataFull() {
		return isWeaterDataFull;
	}

	public void setWeaterDataFull(boolean isWeaterDataFull) {
		this.isWeaterDataFull = isWeaterDataFull;
	}

	public String getIsCountriesListFullMessage() {
		return isCountriesListFullMessage;
	}

	public void setIsCountriesListFullMessage(String isCountriesListFullMessage) {
		this.isCountriesListFullMessage = isCountriesListFullMessage;
	}
}
