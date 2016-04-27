package edu.cmu.team029.termproj;

import java.util.List;

/**
 * Created by auta on 23/4/16.
 */
public class Business {
	public Business() {
	}

	private String bizId;
	private String name;
	private String fullAddress;
	private String categories;
	private String city;
	private String state;
	private double latitude;
	private double longtitude;
	private long priceRange;
	private String openTime;
	private String closeTime;
	private double stars;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public long getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(long priceRange) {
		this.priceRange = priceRange;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	@Override
	public String toString() {
		String retStr = "Business id: " + this.bizId + "\nBusiness name: " + this.name + "\nFull Address: " + this
				.fullAddress
				+ "\nCategories: " + this.categories + "\nCity: " + this.city + "\nState: " + this.state + "\nPrice " +
				"range: " + "\nStars: " + this.stars;
		for (int i = 0; i < priceRange; i++) {
			retStr += "$";
		}
		retStr += "\n";
		return retStr;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public double getStars() {
		return stars;
	}

	public void setStars(double stars) {
		this.stars = stars;
	}
}
