package com.example.user.wordsfromword;

import java.util.ArrayList;
import java.util.HashSet;

public class Level {
	private String word;
	private ArrayList<String> answers;
	private HashSet<Integer> foundWords;

	public Level(String word, ArrayList<String> answers) {
		this.word = word;
		this.answers = answers;
		this.foundWords = new HashSet<>();
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}

	public HashSet<Integer> getFoundWords() {
		return foundWords;
	}

	public void setFoundWords(HashSet<Integer> foundWords) {
		this.foundWords = foundWords;
	}
}
