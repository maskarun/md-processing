package query.domain;

import java.util.ArrayList;

public class User {
	
	public String name;
	public String username;
	public String password;
	public String gender;
	public String dateOfBirth;
	public String email;
	public String status;
	public ArrayList<Flight> bookedFlights;
	public ArrayList<String> places;
	public ArrayList<RestaurantDetails> restaurants;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<Flight> getBookedFlights() {
		return bookedFlights;
	}
	public void setBookedFlights(ArrayList<Flight> bookedFlights) {
		this.bookedFlights = bookedFlights;
	}
	public ArrayList<String> getPlaces() {
		return places;
	}
	public void setPlaces(ArrayList<String> places) {
		this.places = places;
	}
	public ArrayList<RestaurantDetails> getRestaurants() {
		return restaurants;
	}
	public void setRestaurants(ArrayList<RestaurantDetails> restaurants) {
		this.restaurants = restaurants;
	}
	public String toString(){
		return this.username +","+
				this.password +","+
				this.gender +","+
				this.dateOfBirth +","+
				this.email +","+ 
				"Booked Flight : " + ((this.bookedFlights != null) ? this.bookedFlights.size() : "0") +","+ 
				"Restaurants : " + ((this.restaurants != null) ? this.restaurants.size() : "0");
	}
}
