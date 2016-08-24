package com.example.user.wordsfromword;

import com.backendless.BackendlessUser;

/**
 * Created by aqali on 03.08.2016.
 */
public class Battle {
	private String firstPlayerId;
	private BackendlessUser user1, user2;
	private int score1, score2;
	private boolean gameFinished;
	private boolean secondPlayerPlayed;
	private String objectId;

	public boolean isSecondPlayerPlayed() {
		return secondPlayerPlayed;
	}

	public void setSecondPlayerPlayed(boolean secondPlayerPlayed) {
		this.secondPlayerPlayed = secondPlayerPlayed;
	}

	public String getFirstPlayerId() {
		return firstPlayerId;
	}

	public void setFirstPlayerId(String firstPlayerId) {
		this.firstPlayerId = firstPlayerId;
	}

	public BackendlessUser getUser1() {
		return user1;
	}

	public void setUser1(BackendlessUser user1) {
		this.user1 = user1;
	}

	public BackendlessUser getUser2() {
		return user2;
	}

	public void setUser2(BackendlessUser user2) {
		this.user2 = user2;
	}

	public int getScore1() {
		return score1;
	}

	public void setScore1(int score1) {
		this.score1 = score1;
	}

	public int getScore2() {
		return score2;
	}

	public void setScore2(int score2) {
		this.score2 = score2;
	}

	public boolean isGameFinished() {
		return gameFinished;
	}

	public void setGameFinished(boolean gameFinished) {
		this.gameFinished = gameFinished;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
