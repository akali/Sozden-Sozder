package com.example.user.wordsfromword;

/**
 * Created by aqali on 10.08.2016.
 */
public class City {
	private String name;
	private Integer score;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public City(String name, Integer score) {

		this.name = name;
		this.score = score;
	}
}
