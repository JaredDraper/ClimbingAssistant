package com.rubylicious.climbingtracker;

import java.util.HashMap;
import java.util.Map;

public class AttrMaps {
	public static Map<Integer, String> injuryMap;
	public static Map<Integer, String> statusMap;
	public static Map<Integer, String> typeMap;
	public static Map<Integer, String> slopeMap;
	public static Map<Integer, String> colorMap;
	public static Map<Integer, String> gradeMap;

	public static void load() {
		injuryMap = new HashMap<Integer, String>() {
			{
				put(0, "ok_injury");
				put(1, "ow");
			}
		};

		statusMap = new HashMap<Integer, String>() {
			{
				put(0, "project");
				put(1, "redpoint");
				put(2, "onsight");
			}
		};

		gradeMap = new HashMap<Integer, String>() {
			{
				put(0, "VB");
				put(1, "V1");
				put(2, "V2");
				put(3, "V3");
				put(4, "V5");
			}
		};

		colorMap = new HashMap<Integer, String>() {
			{
				put(0, "all_colors");
				put(1, "black");
				put(2, "brown");
				put(3, "dark_blue");
				put(4, "dark_green");
				put(5, "green");
				put(6, "hot_pink");
				put(7, "light_blue");
				put(8, "olive_green");
				put(9, "orange");
				put(10, "red");
				put(11, "tan");
				put(12, "yellow");
			}
		};

		slopeMap = new HashMap<Integer, String>() {
			{
				put(0, "mixed_slope");
				put(1, "slab");
				put(2, "vertical");
				put(3, "overhanging");
			}
		};

		typeMap = new HashMap<Integer, String>() {
			{
				put(0, "top_rope");
				put(1, "bouldering");
				put(2, "sport_route");
				put(3, "gear");
			}
		};
	}
}
