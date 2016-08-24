package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.facebook.accountkit.AccountKit;
import com.facebook.appevents.AppEventsLogger;

public class StartActivity extends Activity {
	private static final String TAG = "StartActivity";
	private Button startButton, logoutButton, leadersButton;
	private ProgressBar progressBar;
	private ProgressDialog dialog;
	private LinearLayout buttonLinearLayout;
	private Button versusButton, historyButton;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);
		DataHolder.loadUserData(Backendless.UserService.loggedInUser());

		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		BackendlessUser user = Backendless.UserService.CurrentUser();
		Log.e(TAG, "CurrentUserName = " + user.getProperty("name"));
		Log.e(TAG, "CurrentUserNumber = " + user.getProperty("number"));
		Log.e(TAG, "CurrentUserEmail = " + user.getProperty("email"));
		Log.e(TAG, "CurrentUserPassword = " + user.getPassword());

		versusButton = (Button) findViewById(R.id.versusButton);
		versusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onVersusButtonClick();
			}
		});
		buttonLinearLayout = (LinearLayout) findViewById(R.id.buttonLinearLayout);

		startButton = (Button) findViewById(R.id.startButton);
		logoutButton = (Button) findViewById(R.id.logoutButton);
		leadersButton = (Button) findViewById(R.id.leadersButton);
		historyButton = (Button) findViewById(R.id.historyButton);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");

		startButton.setTypeface(typeface);
		logoutButton.setTypeface(typeface);
		leadersButton.setTypeface(typeface);
		versusButton.setTypeface(typeface);
		historyButton.setTypeface(typeface);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressBar.setVisibility(View.VISIBLE);
				onStartClick();
			}
		});
		historyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onHistoryClick();
			}
		});

		leadersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLeadersButtonClick();
			}
		});

		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLogoutButtonClick();
			}
		});

//		leadersButton.setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View view) {
//				Toast.makeText(StartActivity.this, "Long Press", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(StartActivity.this, ResultActivity.class);
//				intent.putExtra("score", 19);
//				startActivity(intent);
//				return false;
//			}
//		});
	}

	private void onVersusButtonClick() {
		Intent intent = new Intent(StartActivity.this, VersusActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		new AlertDialog.Builder(this)
				.setTitle("Ойыннан шығу")
				.setMessage("Сенімдісіз бе?")
				.setPositiveButton("Иә", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						onQuit();
					}
				})
				.setNegativeButton("Жоқ", null)
				.setCancelable(false)
				.show();
	}

	private void onQuit() {
		Intent intent = new Intent();
		intent.putExtra("EXIT", true);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void onLeadersButtonClick() {
		Intent intent = new Intent(StartActivity.this, LeaderboardActivity.class);
		startActivity(intent);
	}

	private void onHistoryClick() {
		Intent intent = new Intent(StartActivity.this, BattlesListActivity.class);
		startActivity(intent);
	}

	private void onLogoutButtonClick() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Шығу")
				.setMessage("Сенімдісіз бе?")
				.setPositiveButton("Иә", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						logout();
						finish();
					}
				})
				.setNegativeButton("Жоқ", null)
				.setCancelable(false)
				.show();
	}

	private void logout() {
		Backendless.UserService.logout(new AsyncCallback<Void>() {
			@Override
			public void handleResponse(Void response) {
				Log.d(TAG, "Successful logout!");
				AccountKit.logOut();
				Intent intent = new Intent();
				intent.putExtra("toLogin", true);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to logout: " + fault.getMessage());
			}
		});
	}

	private void onStartClick() {
		Log.e(TAG, "Starting MainActivity");
		buttonLinearLayout.setVisibility(View.GONE);
		Intent intent = new Intent(this, MainActivity.class);
		startActivityForResult(intent, Konst.REQUEST_CODE_PLAY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		progressBar.setVisibility(View.GONE);
		buttonLinearLayout.setVisibility(View.VISIBLE);
	}
}
