package com.example.user.wordsfromword;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class DataHolder {
	public static DataHolder dataHolder;
	private static ArrayList<String> words;
	private static ArrayList<String> answers;
	private static Set<String> localDict;

	public static String username, city;
	public static int score;
	public static int lastUniqueId;
	private static ArrayList<Battles> list;

	public static Set<String> getLocalDict() {
		return localDict;
	}

	public static void setLocalDict(Set<String> localDict) {
		DataHolder.localDict = localDict;
	}

	public static final int area = 20;

	public static DataHolder getDataHolder() {
		if (dataHolder == null) {
			dataHolder = new DataHolder();
			words = new ArrayList<>();
			answers = new ArrayList<>();
			localDict = new TreeSet<>();
			lastUniqueId = 0;
			username = city = "";
			score = 0;
			list = new ArrayList<>();
		}
		return dataHolder;
	}

	public static ArrayList<String> getWords() {
		return words;
	}

	public static void setWords(ArrayList<String> words) {
		DataHolder.words = words;
	}

	public static ArrayList<String> getAnswers() {
		return answers;
	}

	public static void setAnswers(ArrayList<String> answers) {
		DataHolder.answers = answers;
	}

	public static void addAnswer(String s) {
		answers.add(s);
	}

	public static void init() {
		if (dataHolder != null) {
			answers = new ArrayList<>();
		}
	}

	public static void loadUserData(final String userObjectId) {
		if (username != null && city != null)
		Backendless.UserService.findById(userObjectId, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				username = (String) response.getProperty("name");
				city = (String) response.getProperty("city");
				Backendless.Persistence.find(Game.class, new BackendlessDataQuery("user.objectId='" + userObjectId + "'"), new AsyncCallback<BackendlessCollection<Game>>() {
					@Override
					public void handleResponse(BackendlessCollection<Game> response) {
						for (Game game : response.getData())
							score += game.getScore();
						if (response.getCurrentPage().size() > 0)
							response.nextPage(this);
					}

					@Override
					public void handleFault(BackendlessFault fault) {}
				});
			}

			@Override
			public void handleFault(BackendlessFault fault) {}
		});
	}

	public static int getNextUniqueId() {
		return ++lastUniqueId;
	}

	public static void setList(ArrayList<Battles> list) {
		DataHolder.list = list;
	}

	public static ArrayList<Battles> getList() {
		return list;
	}
}
