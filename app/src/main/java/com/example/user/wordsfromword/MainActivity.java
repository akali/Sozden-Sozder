package com.example.user.wordsfromword;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.logging.Logger;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private TextView pointTextView, timerTextView, currentWordTextView, scoreTextTextView;
	private ImageView doneImageView, backImageView;
	private GridView listView;
	private LinearLayout linearLayout;
	private ImageView exitButton;
	private int battleId;

	private boolean firstTime;

	private ArrayList<Letter> letterStack;
	private int currentWordIndex;

	private Set<String> answers;
	private int currentPoints;

	private CountDownTimer countDownTimer;
	private long totalTime;
	private final int timerLength = 121000 / 10;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);
		DataHolder.loadUserData(Backendless.UserService.loggedInUser());

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		battleId = getIntent().getIntExtra("battleId", -1);
		startGame();
	}

	private void startCountDownTimer() {
		Log.e(TAG, "Starting count down timer");
		countDownTimer = new CountDownTimer(totalTime, 1000) {
			@Override
			public void onTick(long l) {
				totalTime = l;

				long sec = l / 1000;

				String time = "";
				if (sec / 60 < 10)
					time += "0";
				time += sec / 60;
				time += " : ";
				if (sec % 60 < 10)
					time += "0";
				time += sec % 60;

				timerTextView.setText(time);
				displayLevel(true);
			}

			@Override
			public void onFinish() {
				onTimerFinished();
			}
		}.start();
	}

	private void onTimerFinished() {
		Log.e(TAG, "Finishing game");
		if (battleId == -1) {
			Intent intent = new Intent(this, ResultActivity.class);
			intent.putExtra("score", currentPoints);
			intent.putExtra("uniqueId", DataHolder.getDataHolder().getNextUniqueId());
			startActivityForResult(intent, Konst.REQUEST_CODE_RESULT);
		} else {
			Intent intent = new Intent(this, VersusResultActivity.class);
			intent.putExtra("score", currentPoints);
			intent.putExtra("uniqueId", DataHolder.getDataHolder().getNextUniqueId());
			intent.putExtra("battleId", battleId);
			startActivityForResult(intent, Konst.REQUEST_CODE_RESULT_VERSUS);
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Konst.REQUEST_CODE_RESULT) {
			if (resultCode == RESULT_OK) { // finish game
				finish();
			} else { // replay
				startGame();
			}
		} else {
			onDestroy();
		}
	}

	private void startGame() {
		init();
		initViews();

		startCountDownTimer();
	}

	private void init() {
		firstTime = true;
		totalTime = timerLength;
		currentPoints = 0;
		DataHolder.init();

		letterStack = new ArrayList<>();
		answers = new HashSet<>();

		loadLevels();
		loadDict();

		currentWordIndex = Rand.getRand().getRnd().nextInt(DataHolder.getWords().size()); // new Random().nextInt(DataHolder.getDataHolder().getWords().size());
	}

	private void displayLevel(boolean isFromCountDownTimer) {
		if (isFromCountDownTimer) {
			if (!firstTime) return;
			firstTime = false;
		}
		letterStack.clear();
		displayCurrentWord();

		final String word = DataHolder.getDataHolder().getWords().get(currentWordIndex);
		linearLayout.removeAllViews();
		for (int i = 0; i < word.length(); ++i) {
			final Letter l = new Letter(word.charAt(i), MainActivity.this);

			l.getTextView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onLetterClicked(l);
				}
			});
			LinearLayout x = ((LinearLayout) l.getTextView().getParent());
			if (x != null)
				x.removeView(l.getTextView());
			linearLayout.addView(l.getTextView());
		}
		updateListView();
	}

	private void onLetterClicked(Letter l) {
		if (!l.isClicked()) {
			l.setClicked(true);
			letterStack.add(l);
			displayCurrentWord();
		} else {
			if (!letterStack.isEmpty()) {
				if (letterStack.get(letterStack.size() - 1).getId() == l.getId()) {
					l.setClicked(false);
					letterStack.remove(letterStack.size() - 1);
					displayCurrentWord();
				}
			}
		}
	}

	private void updateListView() {
//		ArrayList<Fragment> fragments = new ArrayList<>();
//		for (int i = 0; i <= answers.size() / DataHolder.getDataHolder().area; ++i) {
//			int j = Math.max(i - 1, 0);
//			fragments.add(GridFragment.newInstance(i));
//		}
//		GridsAdapter adapter = new GridsAdapter(getSupportFragmentManager(), fragments);
//		viewPager.setAdapter(adapter);
		listView.setAdapter(new WordsAdapter(this));
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
	}

	private String getCurrentWordIndex() {
		String s = "";
		for (int i = 0; i < letterStack.size(); ++i)
			s += letterStack.get(i).getLetter();
		return s;
	}

	private void displayCurrentWord() {
		currentWordTextView.setText(getCurrentWordIndex());
	}

	private void loadLevels() {
		if (!DataHolder.getDataHolder().getWords().isEmpty()) return;
		ArrayList<String> list = new ArrayList<>();

		InputStream input = null;
		try {
			input = getAssets().open("words.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		while (true) {
			String cur = null;
			try {
				cur = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (cur == null) break;
			StringTokenizer st = new StringTokenizer(cur);
			while (st.hasMoreTokens()) {
				list.add(st.nextToken().toLowerCase());
			}
		}

		DataHolder.getDataHolder().setWords(list);
	}

	private void loadDict() {
		if (!DataHolder.getDataHolder().getLocalDict().isEmpty()) return;
		Set<String> dict = new TreeSet<>();
		InputStream input = null;
		try {
			input = getAssets().open("dictionary.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		while (true) {
			String cur = null;
			try {
				cur = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (cur == null) break;
			StringTokenizer st = new StringTokenizer(cur);
			while (st.hasMoreTokens())
				dict.add(st.nextToken().toLowerCase());
		}

		DataHolder.getDataHolder().setLocalDict(dict);

		Log.e("QQQ", dict.size() + "");
	}

	private void initViews() {
		scoreTextTextView = (TextView) findViewById(R.id.scoreTextTextView);
		timerTextView = (TextView) findViewById(R.id.timerTextView);
		pointTextView = (TextView) findViewById(R.id.pointTextView);
		currentWordTextView = (TextView) findViewById(R.id.currentWordTextView);

		exitButton = (ImageView) findViewById(R.id.exitButton);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");

		scoreTextTextView.setTypeface(typeface);
		timerTextView.setTypeface(typeface);
		pointTextView.setTypeface(typeface);
		currentWordTextView.setTypeface(typeface);

		backImageView = (ImageView) findViewById(R.id.backImageView);
		doneImageView = (ImageView) findViewById(R.id.doneImageView);
		listView = (GridView) findViewById(R.id.listView);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

		pointTextView.setText("0");

		doneImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onDoneClick();
			}
		});

		backImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClearingCurrentWord();
			}
		});

		exitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onExitButtonClicked();
			}
		});
	}

	private void onClearingCurrentWord() {
		for (int i = 0; i < letterStack.size(); ++i)
			letterStack.get(i).setClicked(false);
		letterStack.clear();
		displayCurrentWord();
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		onExitButtonClicked();
	}

	private void onExitButtonClicked() {
		countDownTimer.cancel();
		if (battleId == -1) {
			new AlertDialog.Builder(MainActivity.this)
					.setMessage("Ойынды бітіру")
					.setTitle("Берілгіңіз келеді ме?")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("Иә", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					})
					.setNegativeButton("Жоқ", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							startCountDownTimer();
						}
					})
					.setCancelable(false)
					.show();
		} else {
			new AlertDialog.Builder(MainActivity.this)
					.setMessage("Ойынды бітіру")
					.setTitle("Берілгіңіз келеді ме?")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("Иә", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							onTimerFinished();
						}
					})
					.setNegativeButton("Жоқ", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							startCountDownTimer();
						}
					})
					.setCancelable(false)
					.show();
		}
	}

	private void onDoneClick() {
		String currentWord = getCurrentWordIndex();
		Log.d(TAG, "Checking word " + currentWord + "; Contains in set: " + answers.contains(currentWord) + "; Dict checking: " + checkWord(currentWord));
		if (!answers.contains(currentWord) && checkWord(currentWord)) {
			addAnswer(currentWord);
		}
		displayLevel(false);
	}

	private void addAnswer(String currentWord) {
		answers.add(currentWord);
		DataHolder.getDataHolder().addAnswer(currentWord);
		pointTextView.setText(String.valueOf(++currentPoints));
		updateListView();
	}

	private boolean checkWord(String currentWord) {
		return DataHolder.getDataHolder().getLocalDict().contains(currentWord);
	}
}
