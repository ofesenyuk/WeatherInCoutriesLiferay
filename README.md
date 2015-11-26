# WeatherInCoutriesLiferay
Using weather SOAP web service with Liferay
This project is the solution of test task.
Test task. Develop application using Liferay and invoking some web-service from net.
I selected weather SOAP web-service. I was my first (and, at present, single) project with using Liferay.
I installed Liferay in my computer and used its test settings (my installation to the oter MySQL DB did not work propertly).
DB connection properties and table creation queries are stored in ConnectionUtility.java and in WeatherDAO.java (comments in saveXMLrecordToDB), respectively.

In order to provide user the possibility to select country, the program reads countries data from Internet. 
If there are problems with connection, the program reads the date previously stored in MySQL DB.
Next page privides possibility to select city, then read weather data either from Internet, or from DB.
Next page displays weather data and provide the possiblilty to save them to DB. 

Usually, after 14:00 in Kyiv/Ukraine time SOAP weather server used does not work propertly. 
Thus, only data from DB are displayed correctly after 14:00.
