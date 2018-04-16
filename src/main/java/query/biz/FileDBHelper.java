package query.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import query.domain.Flight;
import query.domain.User;

public class FileDBHelper {
	private static FileDBHelper fileDBHelper;
	public Map<String, User> userDetails = new HashMap<String, User>();
//	private File file = new File("database.txt");
//	private File fileflight = new File("flight.txt");
	
	public Resource userResource;
	public Resource flightResource;
	
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	private FileDBHelper(){
//		readFile();
//		readFlightFile();
	}
	
//	public FileDBHelper(){
//		if(file != null && !file.exists()){
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if(fileflight != null && !fileflight.exists()){
//			try {
//				fileflight.createNewFile();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
	
	public static FileDBHelper getInstance(){
		if(fileDBHelper == null){
			fileDBHelper = new FileDBHelper();
		}
		return fileDBHelper;
	}
	
	public Map<String, User> readFile(Resource userResource){
		try{
			String verify;
//			fileReader = new FileReader(file);
			if(userResource != null){
				bufferedReader = new BufferedReader(new InputStreamReader(userResource.getInputStream()));
				while( (verify=bufferedReader.readLine()) != null ){
				    if(verify != null){ 
				    	User user = formUser(verify);
				        userDetails.put(user.getUsername(), user);
				    }
				}
				System.out.println("User details is loaded successfully : " + userDetails.size());
				bufferedReader.close();
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fileReader != null)
					fileReader.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return userDetails;
	}
	
	public Map<String, User> readFlightFile(Resource flightResource){
		try{
			String verify;
//			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(new InputStreamReader(flightResource.getInputStream()));
			while( (verify=bufferedReader.readLine()) != null ){
			    if(verify != null){ 
			    	Flight flight = formFlight(verify);
			    	User user = userDetails.get(flight.getUsername());
			        if(user != null){
			        	if(user.getBookedFlights() == null){
			        		ArrayList<Flight> flights = new ArrayList<Flight>();
			        		flights.add(flight);
			        		user.setBookedFlights(flights);
			        	} else {
			        		user.getBookedFlights().add(flight);
			        	}
			        	
			        }
			    }
			}
			bufferedReader.close();
			System.out.println(" Booked flights loaded successfully ");
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fileReader != null)
					fileReader.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return userDetails;
	}
	
	public boolean writeFile(User user, String userPath){
		try{
			if(StringUtils.isNotEmpty(userPath)){
				File file = new File(userPath);
				fileWriter = new FileWriter(file, true);
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(formString(user));
				bufferedWriter.flush();
				bufferedWriter.close();
				return true;
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fileWriter != null)
					fileWriter.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean writeFlightFile(Flight flight, String flightPath){
		try{
			if(StringUtils.isNotEmpty(flightPath)){
				File fileflight = new File(flightPath);
				fileWriter = new FileWriter(fileflight, true);
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(formFlightStr(flight));
				bufferedWriter.flush();
				bufferedWriter.close();
				return true;
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fileWriter != null)
					fileWriter.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return false;
	}
	
	private String formString(User user){
		String value = "";
		if(user != null){
			value = user.getUsername() +"="+ 
					user.getPassword() +","+
					user.getName() +","+
					user.getDateOfBirth() +","+
					user.getGender() +","+
					user.getEmail() + "\n";
		}
		return value;
	}
	
	private User formUser(String value){
		User user = new User();
		if(StringUtils.isNotBlank(value)){
			String []values = StringUtils.split(value, "=");
			user.setUsername(values[0]);
			String []details = StringUtils.split(values[1], ",");
			user.setPassword(details[0]);
			user.setName(details[1]);
			user.setDateOfBirth(details[2]);
			user.setGender(details[3]);
			user.setEmail(details[4]);
		}
		return user;
	}
	
	private Flight formFlight(String value){
		Flight flight = new Flight();
		if(StringUtils.isNotBlank(value)){
			String values[] = StringUtils.split(value, "=");
			flight.setUsername(values[0]);
			String details[] = StringUtils.split(values[1], ",");
			flight.setFromPlace(details[0]);
			flight.setToPlace(details[1]);
			flight.setDepartureDate(details[2]);
			flight.setReturnDate(details[3]);
			flight.setAdult(details[4]);
			flight.setChild(details[5]);
			flight.setClassType(details[6]);
			flight.setAirline(details[7]);
		}
		return flight;
	}
	
	private String formFlightStr(Flight flight){
		String value = "";
		if(flight != null){
			value = flight.getUsername() +"="+
					flight.getFromPlace() +","+
					flight.getToPlace() +","+
					flight.getDepartureDate() +","+
					flight.getReturnDate() +","+
					flight.getAdult() +","+
					flight.getChild() +","+
					flight.getClassType() +","+
					flight.getAirline() + "\n";
		}
		return value;
	}
	
	public static void main(String[] args) {

	}

}
