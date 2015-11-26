package com.providna.weather.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import com.providna.weather.dao.WeatherDAO;
import com.providna.weather.data.WeatherData;
import com.providna.weather.data.WeatherStorage;

@ManagedBean
@SessionScoped
public class WeatherFromNetManagedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8416411444399870941L;
//	private List<WeatherData> weatherStatesToView = new ArrayList<>();
	private static final String NOT_FULL_MESSAGE = "Данные отображены не "
			+ "полностью. Чтобы отобразить все данные, перезагрузите страницу "
			+ "позже.";
	private static final String FULL_MESSAGE = ""; // "Данные считаны полностью.";
	private String isDataFullMessage;
	
	private List<WeatherData> weatherStates = new ArrayList<>();
	
	public List<WeatherData> getWeatherStates() {
//		if (weatherStates.size() == 0) {
//			retrieveData();
//		}
		System.out.println("getWeatherStates" + weatherStates.size());
		return weatherStates;
	}
	public void setWeatherStates(List<WeatherData> weatherStates) {
		this.weatherStates = weatherStates;
	}

	public void retrieveDataI(ActionEvent event) {
		System.out.println("WeatherFromNetManagedBean.retrieveData()");
		weatherStates = WeatherDAO.getInstance().retrieveWeatherFromInet();
	}
	public String getIsDataFullMessage() {
		if (WeatherStorage.isWeaterDataFull()) {
			isDataFullMessage = FULL_MESSAGE;
		} else {
			isDataFullMessage = NOT_FULL_MESSAGE;
		}
		return isDataFullMessage;
	}
	public void setIsDataFullMessage(String isDataFullMessage) {
		this.isDataFullMessage = isDataFullMessage;
	}
	
public void saveDataToDB(ActionEvent event) {
		
		System.out.println("saveDataToDB ");
		WeatherDAO.getInstance().saveXMLToDB();
	}
	
	public void retrieveDataFromDB(ActionEvent event) {
		
		System.out.println("LocationManagedBean retrieveDataFromDB ");
		WeatherDAO dao = WeatherDAO.getInstance();
		weatherStates = dao.retrieveFromDB();
//		FacesContext context = FacesContext.getCurrentInstance();
//		final WeatherFromNetManagedBean weath 
//		    = (WeatherFromNetManagedBean) context.getELContext()
//				.getELResolver().getValue(context.getELContext(), null, 
//						"weatherFromNetManagedBean");
//		List<WeatherData> wStates =  new ArrayList<>();
//		for (String xml : xmlWeatherResponses) {
//			WeatherData wData = new WeatherData();
//			weath.setDataFromXML(xml, wData );
//			wStates.add(wData);
//		}
//		weath.setWeatherStates(wStates);
	}
}
