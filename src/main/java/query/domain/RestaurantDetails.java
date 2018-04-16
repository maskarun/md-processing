package query.domain;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class RestaurantDetails {
	
	public ArrayList<String> type;
	public ArrayList<String> dish;
	public ArrayList<String> cuisine;
	public String address;
	public String startHour;
	public String stopHour;
	public String name;
	public String place;
	
	public ArrayList<String> getType() {
		return type;
	}
	
	public void setType(String type) {
		if(this.type == null){
			this.type = new ArrayList<String>();
			if(StringUtils.isNotBlank(type))
				this.type.add(type);
		} else {
			if(StringUtils.isNotBlank(type))
				this.type.add(type);
		}
	}
	
	public ArrayList<String> getDish() {
		return dish;
	}
	public void setDish(String dish) {
		if(this.dish == null){
			this.dish = new ArrayList<String>();
			if(StringUtils.isNotBlank(dish))
				this.dish.add(dish);
		} else {
			if(StringUtils.isNotBlank(dish))
				this.dish.add(dish);
		}
	}
	public ArrayList<String> getCuisine() {
		return cuisine;
	}
	public void setCuisine(String cuisine) {
		if(this.cuisine == null){
			this.cuisine = new ArrayList<String>();
			if(StringUtils.isNotBlank(cuisine))
				this.cuisine.add(cuisine);
		} else {
			if(StringUtils.isNotBlank(cuisine))
				this.cuisine.add(cuisine);
		}
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStartHour() {
		return startHour;
	}
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
	public String getStopHour() {
		return stopHour;
	}
	public void setStopHour(String stopHour) {
		this.stopHour = stopHour;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String toString(){
		return this.name +","+
				this.place +","+
				this.address +","+
				this.startHour +","+
				this.stopHour +","+
				this.type != null ? this.type.toString() : "" +","+
				this.dish != null ? this.dish.toString() : "" +","+
				this.cuisine!= null ? this.cuisine.toString() : "";
	}
}
