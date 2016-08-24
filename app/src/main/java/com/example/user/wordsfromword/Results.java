package com.example.user.wordsfromword;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class Results {
//	private ResultUser user;
	private BackendlessUser user;
	private int score;

	public BackendlessUser getUser() {
		return user;
	}

	public void setUser(BackendlessUser user) {
		this.user = user;
	}

	public Results(BackendlessUser user, int score) {
		this.user = user;
		this.score = score;
	}

//	public Results(ResultUser user, int score) {
//		this.user = user;
//		this.score = score;
//	}
//
//	public ResultUser getUser() {
//		return user;
//	}
//
//	public void setUser(ResultUser user) {
//		this.user = user;
//	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Object getProperty(String name) {
		return user.getProperty(name);
	}
}

// old code: http://paste.ubuntu.com/21619876/