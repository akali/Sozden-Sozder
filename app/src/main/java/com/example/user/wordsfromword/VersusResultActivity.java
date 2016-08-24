package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.appevents.AppEventsLogger;

public class VersusResultActivity extends Activity {
	private static final String TAG = "VersusResultActivity";
	private static final int REQUEST_CODE_ENDING_BATTLE = 1;
	private int score, battleId;
	private boolean lose, toSave;
	private ProgressDialog dialog;
	private TextView waitTextView, scoreTextView, finishedTextView;
	private ImageView exitImageView;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus_result);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);


		waitTextView = (TextView) findViewById(R.id.waitTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		finishedTextView = (TextView) findViewById(R.id.finishedTextView);
		exitImageView = (ImageView) findViewById(R.id.exitImageView );

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		scoreTextView.setTypeface(typeface);
		finishedTextView.setTypeface(typeface);
		waitTextView.setTypeface(typeface);

		lose = getIntent().getBooleanExtra("lose", false);
		battleId = getIntent().getIntExtra("battleId", 0);
		score = getIntent().getIntExtra("score", -1);
		int uniqueId = getIntent().getIntExtra("uniqueId", 0);
		toSave = (uniqueId == DataHolder.getDataHolder().lastUniqueId);
		DataHolder.getDataHolder().getNextUniqueId();

		exitImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onExitButtonClick();
			}
		});

		scoreTextView.setText(String.valueOf(score));
		saveBattle();
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		onExitButtonClick();
	}

	private void onExitButtonClick() {
		setResult(RESULT_OK);
		finish();
	}

	private void saveBattle() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Нәтежие сақталуда");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		if (toSave) {
			String userId = Backendless.UserService.loggedInUser();
			Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
				@Override
				public void handleResponse(BackendlessUser user) {
					Backendless.UserService.setCurrentUser(user);
					updateUser(user);
				}

				@Override
				public void handleFault(BackendlessFault fault) {
					Log.e(TAG, "Failed to retrieve user:" + fault.getMessage());
				}
			});
		}
	}

	private void uploadBattle() {
		final Battles battles = DataHolder.getDataHolder().getList().get(battleId);
		if (battles.getColor() == 0)
			battles.getBattle().setScore1(score);
		else {
			battles.getBattle().setScore2(score);
			battles.getBattle().setSecondPlayerPlayed(true);
		}
		Backendless.Persistence.of(Battle.class).save(battles.getBattle(), new AsyncCallback<Battle>() {
			@Override
			public void handleResponse(Battle response) {
				Log.d(TAG, "Successfully saved battle");
				if (dialog.isShowing()) dialog.dismiss();
				if (battles.getColor() == 2) {
					onBattleEnded();
				}
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to save Battle: " + fault.getMessage());
				if (dialog.isShowing()) dialog.dismiss();
			}
		});
	}

	private void onBattleEnded() {
		Intent intent = new Intent(VersusResultActivity.this, VersusBattleEndedActivity.class);
		intent.putExtra("battleId", battleId);
		startActivityForResult(intent, REQUEST_CODE_ENDING_BATTLE);
	}

	private void updateGame(BackendlessUser user) {
		Game game = new Game();
		game.setUser(user);
		game.setScore(score);
		Backendless.Persistence.of(Game.class).save(game, new AsyncCallback<Game>() {
			@Override
			public void handleResponse(Game response) {
				uploadBattle();
				Log.d(TAG, "Game successfully saved!");
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to save game: " + fault.getMessage() + ", code: " + fault.getCode());
			}
		});
	}

	private void updateUser(BackendlessUser user) {
		int newScore = score;
		if (user.getProperty("score") != null)
			newScore += (int) user.getProperty("score");
		user.setProperty("score", newScore);
		Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				updateGame(response);
			}
			@Override
			public void handleFault(BackendlessFault fault) {}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		onExitButtonClick();
	}
}
