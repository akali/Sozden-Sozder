package com.example.user.wordsfromword;

import com.backendless.BackendlessUser;

/**
 * Created by aqali on 18.07.2016.
 */
public class Game {
	private String objectId;
	private BackendlessUser user;
	private int score;

	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public BackendlessUser getUser() {
		return user;
	}
	public void setUser(BackendlessUser user) {
		this.user = user;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}


	public Object getProperty(String score) {
		return user.getProperty(score);
	}
}
