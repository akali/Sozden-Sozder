package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class AddNumberActivity extends Activity {
	private static final String TAG = "AddNumberActivity";
	private EditText emailEditText, passwordEditText, numberEditText;
	private Button loginButton, backButton;
	private ProgressDialog dialog;
	private TextView resetPasswordTextView;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_number);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("AddNumber Activity Launches");

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		numberEditText = (EditText) findViewById(R.id.numberEditText);

		loginButton = (Button) findViewById(R.id.loginButton);
		backButton = (Button) findViewById(R.id.backButton);
		resetPasswordTextView = (TextView) findViewById(R.id.resetPasswordTextView);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		((TextView) findViewById(R.id.enterTextTextView)).setTypeface(typeface);

		loginButton.setTypeface(typeface);
		backButton.setTypeface(typeface);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLoginButtonClick();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onResetClick();
			}
		});
	}

	private void onResetClick() {
		String email = emailEditText.getText().toString();
		if(email.isEmpty()) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Email толтырылмаған")
					.setMessage("Құпия сөзді өзгерту үшін Email толтыру керек")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					})
					.setCancelable(false)
					.show();
			return;
		}
		Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
			@Override
			public void handleResponse(Void response) {
				Log.d(TAG, "Successfully sending email!");
				displayToast("Сіздің email-ға жаңа құпия сөз жіберілді!");
			}
			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to sent email: " + fault.getMessage());
				displayToast("Қате Email");
			}
		});
	}

	private void onLoginButtonClick() {
		if (!isInternetAvailable(this)) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Интернет қосылмаған!")
					.setMessage("Ойынды жалғастыру үшін интернет қосылуы керек")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setCancelable(false)
					.show();
			return;
		}
		loginUser();
	}

	private void loginUser() {
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		final String number = numberEditText.getText().toString();

		if (email.isEmpty()) {
			displayToast("Email толтырылмаған"); return;
		}

		if (password.isEmpty()) {
			displayToast("Құпия сөз толтырылмаған"); return;
		}

		if (number.isEmpty() || !number.matches("^[0-9\\-\\+]{9,15}$")) {
			displayToast("Телефон нөмері дұрыс толтырылмаған");
		}

		dialog = new ProgressDialog(this);
		dialog.setTitle("Сақтау");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Backendless.UserService.setCurrentUser(response);
				response.setProperty("number", number);
				Backendless.UserService.update(response, new AsyncCallback<BackendlessUser>() {
					@Override
					public void handleResponse(BackendlessUser response) {
						onNumberAdded();
						if (dialog.isShowing()) dialog.dismiss();
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						displayToast("Сақталу орындалмады");
						if (dialog.isShowing()) dialog.dismiss();
					}
				});
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to login: " + fault.getMessage());
				if (dialog.isShowing()) dialog.dismiss();
				displayToast("Email немесе құпия сөз қате");
			}
		}, true);
	}

	private void onNumberAdded() {
		displayToast("Сіздің телефон нөмеріңіз сәтті сақталды");
		if (dialog.isShowing()) dialog.dismiss();
		finish();
	}

	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	public boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}
}
