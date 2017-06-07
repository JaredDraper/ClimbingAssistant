package com.rubylicious.climbingtracker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListItem implements Map<String, String> {
	public String id;
	public static final int ITEM = 0;
	public static final int SECTION = 1;

	public final int type;
	public int sectionPosition;
	public int listPosition;

	public ListItem(int type, String id) {
		super();
	    this.type = type;
	    this.id = id;
	}

	@Override public String toString() {
		return id;
	}
	
	// Map interface classes
	
	// return a count of our members
	public int size() {
		return 1;
	}
	
	// set the values of the object to null
	public void clear() {
		this.id = null;
	}
	
	
	// if the values of the members are null, return true
	public boolean isEmpty() {
		if (this.id == null) {
			return true;
		} else {
			return false;
		}
	}
	
	// return a set of the members
	public Set<String> keySet() {
		Set<String> s = new HashSet<String>();
		
		s.add("id");
		
		return s;
	}
	
	// return a set of the member values
	public Set entrySet() {
		Set<String> s = new HashSet<String>();
		
		s.add(this.id);
		
		return s;
	}
	
	// return the value of the given key
	public String get(Object key) {
		if (key.equals("id")) {
			return this.id;
		}
		// if we can't return a value, throw the exception
		throw new ClassCastException();
	}
	
	// set the value of a given key
	public String put(String key, String value) {
		if (key.equals("id")) {
			this.id = value;
		}
		return value;
	}
	
	// remove a key (nullify)
	public String remove(Object key) {
		String value = null;
		if (key.equals("id")) {
			value = this.id;
			this.id = null;
		}
		return value;
	}
	
	// return boolean if we have a member
	public boolean containsKey(Object key) {
		if (key.equals("id")) {
			return true;
		}
	
		return false;
	}
	
	// return boolean if we have a member's value
	public boolean containsValue(Object value) {
		if (value.equals(this.id)) {
			return true;
		}
		return false;
	}

	// set the values of this map to that of another
	public void putAll(Map<? extends String, ? extends String> arg0) {
		// we only need the stub.
	}

	@Override
	public Collection<String> values() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
