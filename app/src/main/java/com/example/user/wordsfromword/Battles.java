package com.example.user.wordsfromword;

import com.backendless.BackendlessUser;

/**
 * Created by aqali on 04.08.2016.
 */
public class Battles {
	/**
	 * color:
	 *  0 gray - no battle
	 *  1 yellow - waiting for response
	 *  2 blue - opponent is waiting for your response
	 *  3 green - won battle
	 *  4 red - lose battle
	 */
	private Battle battle;
	private int color; // 0 -> gray, 1 -> yellow, 2 -> blue, 3 -> green, 4 -> red
	private int id;
	private BackendlessUser opponent;

	Battles(Battle battle) {
		this.battle = battle;
		this.color = 0;
		this.id = 0;
		this.opponent = battle.getUser2();
	}

	public Battle getBattle() {
		return battle;
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public BackendlessUser getOpponent() {
		return opponent;
	}

	public void setOpponent(BackendlessUser opponent) {
		this.opponent = opponent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
