package com.rubylicious.climbingtracker;

import org.joda.time.LocalDate;

public class Climb {
	private int id;
	private String name;
	private int locationId;
	private int areaId;
	private int gradeId;
	private int moves;
	private int cal;
	private int slopeId;
	private int typeId;
	private int color;
	private int injury;
	private int status;
	private boolean isGym;
	private LocalDate firstAttemptDate;
	private int firstAttemptTotal;
	private LocalDate sendDate;
	private int sendTotal;
	private byte[] photo;
	private String comments;
	private Double rating;

	public Climb() {
	}

	// use this to get a climb
	public Climb(int id, String name, int locationId, int areaId, int gradeId,
			int moves, int cal, int slopeId, int typeId, int color, int injury,
			int status, boolean isGym, LocalDate firstAttemptDate,
			int firstAttemptTotal, LocalDate sendDate, int sendTotal,
			byte[] bs, String photoComments, Double rating) {
		this.id = id;
		this.name = name;
		this.locationId = locationId;
		this.areaId = areaId;
		this.gradeId = gradeId;
		this.moves = moves;
		this.cal = cal;
		this.slopeId = slopeId;
		this.typeId = typeId;
		this.color = color;
		this.injury = injury;
		this.status = status;
		this.isGym = isGym;
		this.firstAttemptDate = (firstAttemptDate == null) ? null
				: firstAttemptDate;
		this.firstAttemptTotal = firstAttemptTotal;
		this.sendDate = (sendDate == null) ? null : sendDate;
		this.sendTotal = sendTotal;
		this.photo = bs;
		this.comments = photoComments;
		this.rating = rating;
	}

	// use this to create a climb
	public Climb(String name, int locationId, int areaId, int gradeId,
			int moves, int cal, int slopeId, int typeId, int color, int injury,
			int status, boolean terrain, LocalDate firstAttemptDate,
			Integer firstAttemptTotal, LocalDate sendDate, Integer sendTotal,
			byte[] photo, String photoComments, Double rating) {
		this.name = name;
		this.locationId = locationId;
		this.areaId = areaId;
		this.gradeId = gradeId;
		this.moves = moves;
		this.cal = cal;
		this.slopeId = slopeId;
		this.typeId = typeId;
		this.color = color;
		this.injury = injury;
		this.status = status;
		this.isGym = terrain;
		this.firstAttemptDate = (firstAttemptDate == null) ? null
				: firstAttemptDate;
		this.firstAttemptTotal = firstAttemptTotal;
		this.sendDate = (sendDate == null) ? null : sendDate;
		this.sendTotal = sendTotal;
		this.photo = photo;
		this.comments = photoComments;
		this.rating = rating;
	}

	public void setLocation(int locationId) {
		this.locationId = locationId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGradeId() {
		return gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	public Integer getSendTotal() {
		return sendTotal;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public byte[] getPhoto() {
		return photo;
	}
	public Double getRating() {
		return rating;
	}
	public boolean isGym() {
		return isGym;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public void setGym(boolean isGym) {
		this.isGym = isGym;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String photoComments) {
		this.comments = photoComments;
	}

	public void setSendTotal(Integer sendTotal) {
		this.sendTotal = sendTotal;
	}

	public Integer getMoves() {
		return moves;
	}

	public void setMoves(Integer moves) {
		this.moves = moves;
	}

	public Integer getCal() {
		return cal;
	}

	public void setCal(Integer cal) {
		this.cal = cal;
	}

	public int getSlopeId() {
		return slopeId;
	}

	public void setSlopeId(int slopeId) {
		this.slopeId = slopeId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getInjury() {
		return injury;
	}

	public void setInjury(int injury) {
		this.injury = injury;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setInside(boolean terrain) {
		this.isGym = terrain;
	}

	public LocalDate getFirstAttemptDate() {
		return firstAttemptDate;
	}

	public void setFirstAttemptDate(LocalDate firstAttempt) {
		this.firstAttemptDate = firstAttempt;
	}

	public LocalDate getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDate sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getFirstAttemptTotal() {
		return firstAttemptTotal;
	}

	public void setFirstAttemptTotal(Integer firstAttemptTotal) {
		this.firstAttemptTotal = firstAttemptTotal;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public void setId(Integer climbId) {
		this.id = climbId;
	}
}
