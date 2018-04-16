package query.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import query.domain.Flight;
import query.domain.RestArray;
import query.domain.Restaurant;
import query.domain.RestaurantDetails;
import query.domain.User;
import query.ontology.Constant;
import query.ontology.LoadOntology;

public class FlightHelper {
	
	public User checkloginUser(String username, String password){
		User user = null;
		Map<String, User> userDetails = FileDBHelper.getInstance().userDetails;
		if(userDetails != null && StringUtils.isNotBlank(username)){
			user = userDetails.get(username);
			if(user != null){
				if(!StringUtils.equals(user.getPassword(),password)){
					user.setStatus("Incorrect Password");
					System.out.println("Incorrect Password");
				} else {
					user.setPlaces(LoadOntology.getInstanes().loadSubClasses(Constant.PLACE));
					user.setRestaurants(LoadOntology.getInstanes().loadAllRestaurant());
					System.out.println(user.toString());
				}
			} else {
				user = new User();
				user.setStatus("Cannot find the user");
				System.out.println("Cannot find the user");
			}
		} else {
			user = new User();
			user.setStatus("Sorry unexcepted error occurred.");
			System.out.println("Userdetails is null/empty");
		}
		return user;
	}
	
	private Map<String, RestArray> loadQuestion(){
		Map<String, RestArray> questionMap = new HashMap<String, RestArray>();
		ArrayList<String> questionClasses = LoadOntology.getInstanes().loadSubClasses(Constant.QUESTION);
		if(questionClasses != null && questionClasses.size() > 0){
			for (String qtClass : questionClasses) {
				String qtComment = LoadOntology.getInstanes().getQuestion(qtClass);
				if(StringUtils.contains(qtClass, "_")){
					String order = StringUtils.substring(qtClass, 0, StringUtils.indexOf(qtClass, "_"));
					String tClass = StringUtils.substring(qtClass, StringUtils.lastIndexOf(qtClass, "_") + 1, StringUtils.length(qtClass)); 
//				qtClass = StringUtils.contains(qtClass, "_") ? : qtClass;
					ArrayList<String> classInfo = LoadOntology.getInstanes().loadSubClasses(tClass);
					RestArray restArray = new RestArray();
					restArray.setKey(tClass);
					restArray.setQuestion(qtComment);
					restArray.setArrayList(classInfo);
					questionMap.put(order, restArray);
				}
			}
		}
		return questionMap;
	}
	
	public boolean registerUser(User user, String userPath){
		boolean isRegister = false;
		if(user != null){
			User usr = FileDBHelper.getInstance().userDetails.get(user.getUsername());
			if(usr == null){
				isRegister = FileDBHelper.getInstance().writeFile(user, userPath);
				if(isRegister){
					FileDBHelper.getInstance().userDetails.put(user.getUsername(), user);
				}
			}
		}
		return isRegister;
	}
	
	public boolean bookFlight(Flight flight, String flightPath){
		boolean isFlight = false;
		if(flight != null){
			if(StringUtils.isNotBlank(flight.getUsername())){
				User user = FileDBHelper.getInstance().userDetails.get(flight.getUsername());
				if(user != null){
					boolean isAdded = (user.bookedFlights != null) ? user.bookedFlights.add(flight) : addbookedFlight(user, flight);
					if(isAdded){
						isFlight = FileDBHelper.getInstance().writeFlightFile(flight, flightPath);
					}
					System.out.println(user.toString());
				}
			}
		}
		return isFlight;
	}
	
	private boolean addbookedFlight(User user, Flight flight){
		ArrayList<Flight> flightList = new ArrayList<Flight>();
		flightList.add(flight);
		user.bookedFlights = flightList;
		return true;
	}
	private Map<Integer, RestaurantDetails> loadPubCafesAnswer(Restaurant restaurant){
		Map<Integer, RestaurantDetails> orderMap = new HashMap<Integer, RestaurantDetails>();
		if(restaurant != null && StringUtils.isNotBlank(restaurant.getDestination())){
			ArrayList<RestaurantDetails> restaurantDetails = LoadOntology.getInstanes().loadRestaurant(restaurant.getDestination());
			int count = 0;
			for (RestaurantDetails details : restaurantDetails) {
				if(details.getType() != null && details.getType().contains(restaurant.getRequestType().toLowerCase())){
					orderMap.put(count, details);
				}
				count = count + 1;
			}
		}
		return orderMap;
	}
	
	private Map<Integer, RestaurantDetails> loadAnswer(Restaurant restaurant){
		Map<Integer, RestaurantDetails> orderMap = new HashMap<Integer, RestaurantDetails>();
		if(restaurant != null && StringUtils.isNotBlank(restaurant.getDestination())){
			ArrayList<RestaurantDetails> restaurantDetails = LoadOntology.getInstanes().loadRestaurant(restaurant.getDestination());
			int count = 0;
			for (RestaurantDetails details : restaurantDetails) {
				Map<String, RestArray> answerMap = restaurant.getAnswerMap();
				for (Map.Entry<String, RestArray> entry : answerMap.entrySet()) {
					RestArray rest = entry.getValue();
					if(StringUtils.equalsIgnoreCase(entry.getKey(), "type")){
						if(details.getType() != null && details.getType().contains(rest.getAnswer().toLowerCase())){
							orderMap.put(count, details);
						}
					} else if(StringUtils.equalsIgnoreCase(entry.getKey(), "dish")){
						if(details.getDish() != null && details.getDish().contains(rest.getAnswer().toLowerCase())){
							orderMap.put(count, details);
						}
					} else if(StringUtils.equalsIgnoreCase(entry.getKey(), "cuisine")){
						if(details.getCuisine() != null && details.getCuisine().contains(rest.getAnswer().toLowerCase())){
							orderMap.put(count, details);
						}
					}
				}
				count = count + 1;
			}
		}
		return orderMap;
	}
	
	public Restaurant loadRestaurantDetails(Restaurant restaurant){
		if(restaurant.getAnswerMap() != null && restaurant.getAnswerMap().size() > 0){
			restaurant.setRestaurants(loadAnswer(restaurant));
		} else if(StringUtils.isNotEmpty(restaurant.getRequestType())){
			restaurant.setRestaurants(loadPubCafesAnswer(restaurant));
		} else {
			restaurant.setQuestionMap(loadQuestion());
		}
		return restaurant;
	}
	
}
