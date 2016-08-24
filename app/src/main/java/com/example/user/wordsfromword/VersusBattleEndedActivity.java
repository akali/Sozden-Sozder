package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.facebook.appevents.AppEventsLogger;

public class VersusBattleEndedActivity extends Activity {
	private static final String TAG = "BattleEndedActivity";
	private static final int REQUEST_CODE_SHARE = 1;
	private int battleId;
	private TextView firstNameTextView, firstCityTextView, firstScoreTextView, secondNameTextView, secondCityTextView, secondScoreTextView, titleTextView;
	private ImageView exitImageView, shareImageView;
	private String loseText = "Сіз ұтылдыңыз!";
	private String winText = "Сіз ұттыңыз!";
	private String equalText = "Тең!";
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus_battle_ended);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		firstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
		firstCityTextView = (TextView) findViewById(R.id.firstCityTextView);
		firstScoreTextView = (TextView) findViewById(R.id.firstScoreTextView);
		secondNameTextView = (TextView) findViewById(R.id.secondNameTextView);
		secondCityTextView = (TextView) findViewById(R.id.secondCityTextView);
		secondScoreTextView = (TextView) findViewById(R.id.secondScoreTextView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		firstNameTextView.setTypeface(typeface);
		firstCityTextView.setTypeface(typeface);
		firstScoreTextView.setTypeface(typeface);
		secondNameTextView.setTypeface(typeface);
		secondCityTextView.setTypeface(typeface);
		secondScoreTextView.setTypeface(typeface);
		titleTextView.setTypeface(typeface);

		exitImageView = (ImageView) findViewById(R.id.exitImageView);
		shareImageView = (ImageView) findViewById(R.id.shareImageView);

		battleId = getIntent().getIntExtra("battleId", 0);
		Battle battle = DataHolder.getDataHolder().getList().get(battleId).getBattle();
		BackendlessUser user1 = battle.getUser1();
		BackendlessUser user2 = battle.getUser2();
		int score1 = battle.getScore1(), score2 = battle.getScore2();

		firstNameTextView.setText((CharSequence) user1.getProperty("name"));
		firstCityTextView.setText((CharSequence) user1.getProperty("city"));
		firstScoreTextView.setText(String.valueOf(score1));
		secondNameTextView.setText((CharSequence) user2.getProperty("name"));
		secondCityTextView.setText((CharSequence) user2.getProperty("city"));
		secondScoreTextView.setText(String.valueOf(score2));

		boolean win;

		int color = DataHolder.getDataHolder().getList().get(battleId).getColor();

		win = color == 3;

		if (!win) {
			titleTextView.setText(loseText);
		} else {
			titleTextView.setText(winText);
		}

		if (score1 == score2) {
			titleTextView.setText(equalText);
		}

		String s1 = String.format("name: %s, city: %s, score: %d", (String) user1.getProperty("name"), (String) user1.getProperty("city"), score1);
		String s2 = String.format("name: %s, city: %s, score: %d", (String) user2.getProperty("name"), (String) user2.getProperty("city"), score2);
		Log.d(TAG, s1 + " | " + s2);
		exitImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onExitButtonClick();
			}
		});
		shareImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onShareClick();
			}
		});
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		onExitButtonClick();
	}

	private void onExitButtonClick() {
		finish();
	}

	private void onShareClick() {
		Intent intent = new Intent(this, BattleShareActivity.class);
		intent.putExtra("battleId", battleId);
		startActivityForResult(intent, REQUEST_CODE_SHARE);
	}
}
