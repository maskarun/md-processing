package query.domain;

import java.util.Map;

public class Restaurant {
	
	public String requestType;
	public String destination;
	public Map<String, RestArray> questionMap;
	public Map<String, RestArray> answerMap;
	public Map<Integer, RestaurantDetails> restaurants;
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Map<String, RestArray> getQuestionMap() {
		return questionMap;
	}
	public void setQuestionMap(Map<String, RestArray> questionMap) {
		this.questionMap = questionMap;
	}
	public Map<String, RestArray> getAnswerMap() {
		return answerMap;
	}
	public void setAnswerMap(Map<String, RestArray> answerMap) {
		this.answerMap = answerMap;
	}
	public Map<Integer, RestaurantDetails> getRestaurants() {
		return restaurants;
	}
	public void setRestaurants(Map<Integer, RestaurantDetails> restaurants) {
		this.restaurants = restaurants;
	}
}
