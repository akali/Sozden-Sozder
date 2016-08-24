package com.example.user.wordsfromword;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aqali on 14.07.2016.
 */
public class Rand {
	public static Rand rand;
	private static Random rnd;
	public static Rand getRand() {
		if (rand == null) {
			rand = new Rand();
			rnd = new Random();
		}
		return rand;
	}

	public static Random getRnd() {
		return rnd;
	}
}
