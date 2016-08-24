package com.example.user.wordsfromword;

import com.backendless.BackendlessUser;

import java.io.Serializable;

/**
 * Created by aqali on 28.07.2016.
 */
public class ResultUser implements Comparable<ResultUser>, Serializable {
	private String username, score, objectId, city;
	BackendlessUser user;
	public ResultUser(Object username, Object city, String objectId) {
		this.username = (String) username;
		this.city = (String) city;
		this.objectId = objectId;
	}

	public ResultUser(BackendlessUser bUser) {
		this.username = (String) bUser.getProperty("name");
		this.city = (String) bUser.getProperty("city");
		this.objectId = bUser.getObjectId();
		this.user = bUser;
	}

	public BackendlessUser getUser() {
		return user;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ResultUser that = (ResultUser) obj;
		return (objectId.equals(that.objectId));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public int compareTo(ResultUser resultUser) {
		return objectId.compareTo(resultUser.objectId);
	}
}
