package com.rubylicious.climbingtracker;

import org.joda.time.DateTime;

public class History {
	private int climbId;
	private int historyId;
	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	private DateTime date;
	private int moves;

	public History(int historyId, int climbId, DateTime date, int moves) {
		this.climbId = climbId;
		this.historyId = historyId;
		this.date = date;
		this.moves = moves;
	}
	
	public History( int climbId, int moves) {
		this.climbId = climbId;
		this.date = new DateTime();
		this.moves = moves;
	}

	public int getClimbId() {
		return climbId;
	}

	public void setClimbId(int climbId) {
		this.climbId = climbId;
	}

	public DateTime getDate() {
		return date;
	}

	public int getMoves() {
		return moves;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}


	
}
