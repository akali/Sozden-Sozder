package com.example.user.wordsfromword;

/**
 * Created by aqali on 8/18/16.
 */
public class Contact {
	private String name;
	private int number;

	public Contact(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
