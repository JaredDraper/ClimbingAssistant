package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;

public class Area {
	private int id;
	private String name;
	private int location;
	private String comments;

	//used to get from db
	public Area(int id, String name, int location, String comments){
		this.id = id;
		this.name = name;
		this.location = location;
		this.comments = comments;
	}
	
	//used to create new one
	public Area(String name,int location, String comments){
		this.name = name;
		this.location = location;
		this.comments = comments;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getId() {
		return id;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public static List<Model> populateAreas(Integer locationId, DataSource db) {
		List<Area> areaObjects = db.getAllAreasPerLocation(locationId);
		List<Model> areas = new ArrayList<Model>();
		for (Area areaObject : areaObjects) {
			areas.add(new Model(areaObject.getId(), areaObject.getName()));
		}
		return areas;	
	}
}
