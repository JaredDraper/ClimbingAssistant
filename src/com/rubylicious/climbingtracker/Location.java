package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.List;

public class Location {
	private int id;
	private String name;
	private String comments;
	private String address;
	private String gps;
	
	//used to get an old one
	public Location(int id, String name, String address, String gps, String comments){
		this.id = id;
		this.name = name;
		this.comments = comments;
		this.address = address;
		this.gps = gps;
	}
	//used to create a new one
	public Location(String name, String address, String gps, String comments){
		this.name = name;
		this.comments = comments;
		this.address = address;
		this.gps = gps;
	}

	public int getId() {
		return id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAddress() {
		return address;
	}
    
	public static List<Model> populateLocations(DataSource db) {
		List<Location> locations = db.getAllLocations();
		List<Model> locModels = new ArrayList<Model>();
		for (Location location : locations) {
			locModels.add(new Model(location.getId(), location.getName()));
		}
		return locModels;	
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getGpsCoordinates() {
		return gps;
	}

	public void setGpsCoordinates(String gpsCoordinates) {
		this.gps = gpsCoordinates;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
