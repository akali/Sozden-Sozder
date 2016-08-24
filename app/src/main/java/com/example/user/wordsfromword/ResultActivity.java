package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class ResultActivity extends Activity {
	private static final String TAG = "ResultActivity";
	private static final int REQUEST_CODE_SHARE = 2;
	private ImageView shareButton, playAgainButton, homeButton;
	private TextView scoreTextView, gameFinishedTextView, sozdensozderTextView;
	private ProgressDialog dialog;
	private int score, oldScore;
	private String name, city;

	private boolean toSave;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);
		DataHolder.loadUserData(Backendless.UserService.loggedInUser());

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		homeButton = (ImageView) findViewById(R.id.homeButton);
		shareButton = (ImageView) findViewById(R.id.shareButton);
		playAgainButton = (ImageView) findViewById(R.id.playAgainButton);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);

		gameFinishedTextView = (TextView) findViewById(R.id.gameFinishedTextView);
		sozdensozderTextView = (TextView) findViewById(R.id.sozdensozderTextView);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");

		scoreTextView.setTypeface(typeface);
		gameFinishedTextView.setTypeface(typeface);
		sozdensozderTextView.setTypeface(typeface);

		shareButton.setVisibility(View.VISIBLE);
		playAgainButton.setVisibility(View.VISIBLE);

		score = getIntent().getIntExtra("score", -1);

		int uniqueId = getIntent().getIntExtra("uniqueId", 0);
		toSave = (uniqueId == DataHolder.getDataHolder().lastUniqueId);
		DataHolder.getDataHolder().getNextUniqueId();

		scoreTextView.setText(String.valueOf(score));
		oldScore = 0;

		if (score != -1) {
			dialog = new ProgressDialog(this);
			dialog.setTitle("Нәтежиені сақтау");
			dialog.setMessage("Нәтежие сақталуда");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			saveGameData(score);
		}

		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onShareButtonClick();
			}
		});
		playAgainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onPlayAgainButtonClick();
			}
		});
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onHomeButtonClick();
			}
		});
	}

	@Override
	public void onBackPressed() {
		onHomeButtonClick();
	}

	private void onHomeButtonClick() {
		setResult(RESULT_OK);
		finish();
	}

	private void onPlayAgainButtonClick() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void onShareButtonClick() {
//		Toast.makeText(this, "Sharing on instagram!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ShareActivity.class);
		intent.putExtra("score", score);
		intent.putExtra("name", name);
		intent.putExtra("city", city);
		startActivityForResult(intent, REQUEST_CODE_SHARE);
//		takeScreenshot("@sozdensozder");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SHARE) {
			}
		}
	}

	private void saveGameData(final int score) {
		try {
			String currentUserId = Backendless.UserService.loggedInUser();
			Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
				@Override
				public void handleResponse(final BackendlessUser response) {
					name = (String) response.getProperty("name");
					city = (String) response.getProperty("city");
					int newScore = score;
					if (response.getProperty("score") != null)
						newScore += (int) response.getProperty("score");
					response.setProperty("score", newScore);
					if (toSave) {
						Backendless.UserService.update(response, new AsyncCallback<BackendlessUser>() {
							@Override
							public void handleResponse(BackendlessUser response) {
								Log.d(TAG, "Successfully saved user score : " + response.getProperty("score"));
							}

							@Override
							public void handleFault(BackendlessFault fault) {
								Log.e(TAG, "Failed to save user score " + fault.getMessage());
							}
						});
						Game game = new Game();
						game.setUser(response);
						game.setScore(score);
						Backendless.Persistence.of(Game.class).save(game, new AsyncCallback<Game>() {
							@Override
							public void handleResponse(Game response) {
								Log.d(TAG, "Game successfully saved!");
								if (dialog.isShowing()) dialog.dismiss();
							}

							@Override
							public void handleFault(BackendlessFault fault) {
								Log.e(TAG, "Failed to save game: " + fault.getMessage() + ", code: " + fault.getCode());
								if (dialog.isShowing()) dialog.dismiss();
							}
						});
					} else {
						if (dialog.isShowing()) dialog.dismiss();
					}
				}

				@Override
				public void handleFault(BackendlessFault fault) {
					Log.e(TAG, "Failed to retrieve user: " + fault.getMessage() + ", code: " + fault.getCode());
					displayToast("Нәтежиені сақтау сәтсіз өтті");
					if (dialog.isShowing()) dialog.dismiss();
				}
			});
		} catch (BackendlessException e) {
			e.printStackTrace();
		}
	}

	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
}
