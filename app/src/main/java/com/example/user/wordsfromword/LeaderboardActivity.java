package com.example.user.wordsfromword;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends Activity {
	private Toast toast;
	private static final String TAG = "LeaderboardActivity";
	private ListView listView;
	private TextView usernameTextView, cityTextView, scoreTextView, posTextView, leadersTextView;
	private ProgressBar progressBar;
	private ImageView backButton;
	private ArrayList<BackendlessUser> data;

	private ImageView cityImageView, typeImageView;
	private boolean isCity, isSum, isloading;
	private ArrayList<Game> games;
	private boolean displayLoaded, gamesLoading;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);


		listView = (ListView) findViewById(R.id.listView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {

			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {

			}
		});

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");

		leadersTextView = (TextView) findViewById(R.id.leadersTextView);
		usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		cityTextView = (TextView) findViewById(R.id.cityTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		posTextView = (TextView) findViewById(R.id.posTextView);
		cityImageView = (ImageView) findViewById(R.id.cityImageView);
		typeImageView = (ImageView) findViewById(R.id.typeImageView);

		leadersTextView.setTypeface(typeface);
		cityTextView.setTypeface(typeface);
		scoreTextView.setTypeface(typeface);
		usernameTextView.setTypeface(typeface);
		posTextView.setTypeface(typeface);

		backButton = (ImageView) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		cityImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onCityImageViewClick();
			}
		});
		typeImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onTypeImageViewClick();
			}
		});

		try {
			String currentUserId = Backendless.UserService.loggedInUser();
			Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
				@Override
				public void handleResponse(BackendlessUser response) {
					Backendless.UserService.setCurrentUser(response);
					BackendlessUser user = response;
					usernameTextView.setText((CharSequence) user.getProperty("name"));
					cityTextView.setText((CharSequence) user.getProperty("city"));
					if (user.getProperty("score") != null)
						scoreTextView.setText(String.valueOf((int) user.getProperty("score")));
					else
						scoreTextView.setText("0");
					Log.e(TAG, "LEADERBOARD");
					loadLeaderboard();
				}

				@Override
				public void handleFault(BackendlessFault fault) {
					scoreTextView.setText("0");
					Log.e(TAG, "failed to find such user " + fault.getMessage());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onTypeImageViewClick() {
		if(isloading){
			displayToast("Күте тұрыңыз");
		}
		else{
			int src = 0;
			int citySrc = R.drawable.bycity;
			cityImageView.setImageResource(citySrc);
			// cityImageView.setBackgroundResource(citySrc);
			isCity = false;

			if(!isSum){
				listView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				loadType();
				src = R.drawable.bysumm;
			} else {
				src = R.drawable.bymaxx;
				isloading = true;
				listView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				loadLeaderboard();
			}
			typeImageView.setImageResource(src);
//			typeImageView.setBackgroundResource(src);
			isSum = !isSum;
		}

	}

	private void loadType() {
		if ((this.games != null && !this.games.isEmpty())) {
			displayGames();
			return;
		}
		if (gamesLoading) return;
		gamesLoading = true;
		QueryOptions options = new QueryOptions();
		options.addSortByOption("score DESC");
		BackendlessDataQuery query = new BackendlessDataQuery(options);
		query.setPageSize(100);
		this.games = new ArrayList<>();

		Backendless.Persistence.of(Game.class).find(query, new AsyncCallback<BackendlessCollection<Game>>() {
			@Override
			public void handleResponse(BackendlessCollection<Game> response) {
				for (Game game : response.getCurrentPage()){
					games.add(game);
				}
				gamesLoading = false;
				displayGames();
//				if (response.getCurrentPage().isEmpty()){
//					displayGames();
//				} else {
//					// response.nextPage(this);
//				}
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "failed to load games : " + fault.getMessage());
			}
		});

	}

	private void displayGames() {
		Log.d(TAG, "Successfully loaded games");
		progressBar.setVisibility(View.GONE);

		Collections.sort(games, new Comparator<Game>() {
			@Override
			public int compare(Game game, Game t1) {
				return -Integer.valueOf(game.getScore()).compareTo(t1.getScore());
			}
		});

		ArrayList<Results> arr = new ArrayList<>();

		for (Game game : games)
			arr.add(new Results(game.getUser(), game.getScore()));

		listView.setAdapter(new LeadersAdapter(this, arr));
		listView.setVisibility(View.VISIBLE);
	}

	private void onCityImageViewClick() {
		if(isloading) {
			displayToast("Күте тұрыңыз");
		}
		else{
			int src = 0; // City
			if (!isCity) {
				loadCity();
				src = R.drawable.offcity;
			} else {
				src = R.drawable.bycity;
				isloading = true;
				listView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				loadLeaderboard();
			}
			cityImageView.setImageResource(src);
//			cityImageView.setBackgroundResource(src);
			isCity = !isCity;
		}
	}

	private void displayToast(String s) {
		if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
			toast = Toast.makeText(LeaderboardActivity.this, s, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void loadCity() {
		if (!displayLoaded) {
			displayToast("Күте тұрыңыз"); return;
		}
		HashMap <String, Integer> listMap = new HashMap<>();
		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		for (BackendlessUser user : data) {
			String city = (String) user.getProperty("city");
			Integer score = 0;
			if (user.getProperty("score") != null)
				score = (Integer) user.getProperty("score");
			if (listMap.containsKey(city))
				score += listMap.get(city);
			listMap.put(city, score);
		}
		ArrayList<City> list = new ArrayList<>();
		for (Map.Entry pair : listMap.entrySet()) {
			City city  = new City((String) pair.getKey(), (Integer) pair.getValue());
			list.add(city);
		}
		Collections.sort(list, new Comparator<City>() {
			@Override
			public int compare(City city, City t1) {
				return -city.getScore().compareTo(t1.getScore());
			}
		});

		progressBar.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		listView.setAdapter(new CityLeadersAdapter(this, list));
	}

	private void loadLeaderboard() {
		isloading = true;
		if (data != null && !data.isEmpty()) {
			displayLeaderboard();
			return;
		}
		Log.d(TAG, "Loading Leaderboard");
		BackendlessDataQuery query = new BackendlessDataQuery();
		query.setPageSize(100);
		this.data = new ArrayList<>();

		Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
			@Override
			public void handleResponse(BackendlessCollection<BackendlessUser> response) {
				if (response.getCurrentPage().size() > 0) {
					for (BackendlessUser user : response.getCurrentPage())
						data.add(user);
					response.nextPage(this);
				} else {
					isloading = false;
					displayLoaded = true;
					displayLeaderboard();
				}
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "failed to load leaderboard: " + fault.getMessage());
			}
		});
//		Backendless.Persistence.of(Game.class).find(query, new AsyncCallback<BackendlessCollection<Game>>() {
//			@Override
//			public void handleResponse(BackendlessCollection<Game> response) {
//				ArrayList<Results> results = new ArrayList<>();
//				if (response == null || response.getCurrentPage().size() <= 0) {
//					displayLeaderboard(results, true);
//					return;
//				}
////				Log.e(TAG, "Total objects size = " + String.valueOf(response.getTotalObjects()));
////				Log.d(TAG, "Successfully received data: " + response.getData().toString());
////				Log.e(TAG, "Data size = " + response.getData().size() + "");
//				ArrayList<Game> list = (ArrayList<Game>) response.getData();
//				Map<BackendlessUser, Integer> leaderboardData = new HashMap<>();
//				for (Game game : list) {
//					BackendlessUser bUser = game.getUser();
//					int score = 0;
//					if (leaderboardData.containsKey(bUser))
//						score = leaderboardData.get(bUser);
//					leaderboardData.put(bUser, score + game.getScore());
//				}
//
//				for (Map.Entry pair : leaderboardData.entrySet()) {
//					BackendlessUser user = (BackendlessUser) pair.getKey();
////					ResultUser user = (ResultUser) pair.getKey();
//					Integer score = (Integer) pair.getValue();
//					results.add(new Results(user, score));
//				}
//
//				displayLeaderboard(results, false);
//				leaderboardData.clear();
//				leaderboardData = null;
//				response.nextPage(this);
//			}
//
//			@Override
//			public void handleFault(BackendlessFault fault) {
//				Log.e(TAG, "Failed to receive data: " + fault.getMessage());
//			}
//		});
	}

	private void displayLeaderboard() {
		Log.d(TAG, "Successfully loaded leaderboard");
		progressBar.setVisibility(View.GONE);
		ArrayList<Results> list = new ArrayList<>();
		for (BackendlessUser user : data) {
			int score = 0;
			if (user.getProperty("score") != null)
				score = (int) user.getProperty("score");
			list.add (new Results(user, score));
		}
		Collections.sort(list, new Comparator<Results>() {
			@Override
			public int compare(Results results, Results t1) {
				return -Integer.valueOf(results.getScore()).compareTo(t1.getScore());
			}
		});
		int l = 0, r = list.size() - 1;
		int curScore = Integer.parseInt(scoreTextView.getText().toString());
		while (r - l > 1) {
			int m = (l + r) / 2;
			if (list.get(m).getScore() > curScore) {
				l = m;
			} else {
				r = m;
			}
		}
		int pos;
		if (list.get(l).getUser().getObjectId().equals(Backendless.UserService.loggedInUser()))
			pos = l + 1;
		else
			pos = r + 1;
		posTextView.setText(String.valueOf(pos));
		listView.setVisibility(View.VISIBLE);
		listView.setAdapter(new LeadersAdapter(this, list));
		isloading = false;
	}

//	private void displayLeaderboard(ArrayList<Results> data, boolean toDisplay) {
//		for (Results results : data) {
//			this.data.add(results);
//		}
//		if (toDisplay) {
//			Collections.sort(this.data, new Comparator<Results>() {
//				@Override
//				public int compare(Results results, Results t1) {
//					return -Integer.valueOf(results.getScore()).compareTo(Integer.valueOf(t1.getScore()));
//				}
//			});
//			int pos = 1, score = 0;
//			for (Results results : this.data) {
//				if (results.getUser().getObjectId().equals(Backendless.UserService.loggedInUser())) {
//					score = results.getScore();
//					break;
//				}
//				++pos;
//			}
//			scoreTextView.setText(String.valueOf(score));
//			posTextView.setText(String.valueOf(pos));
//			Log.e(TAG, "Going to display leaderboard");
//			progressBar.setVisibility(View.GONE);
//			listView.setVisibility(View.VISIBLE);
//			for (Results res : this.data) {
//				Log.d(TAG, res.getUser().getEmail() + " " + res.getScore());
//			}
//			LeadersAdapter adapter = new LeadersAdapter(this, this.data);
//			listView.setAdapter(adapter);
//			this.data = new ArrayList<>();
//		}
//	}

}

// old code: http://paste.ubuntu.com/21619785/
// new old code: http://paste.ubuntu.com/21624231/
// new new old code: http://paste.ubuntu.com/21659030/
// released old code: http://paste.ubuntu.com/21919122/
