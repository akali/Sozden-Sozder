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

public class SignInActivity extends Activity {
	private static final String TAG = "SignInActivity";
	private EditText emailEditText, passwordEditText;
	private Button registerButton, loginButton;
	private ProgressDialog dialog;
	private TextView resetPasswordTextView;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("SignIn Activity Launches");

		if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
			finish();
		}

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		registerButton = (Button) findViewById(R.id.registerButton);
		loginButton = (Button) findViewById(R.id.loginButton);
		resetPasswordTextView = (TextView) findViewById(R.id.resetPasswordTextView);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		((TextView) findViewById(R.id.enterTextTextView)).setTypeface(typeface);
		registerButton.setTypeface(typeface);
		loginButton.setTypeface(typeface);

		emailEditText.setText("");
		passwordEditText.setText("");

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onRegisterButtonClick();
			}
		});
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLoginButtonClick();
			}
		});
		resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onResetClick();
			}
		});
		String currentUserId = Backendless.UserService.loggedInUser();
		if (currentUserId != null && currentUserId != "") {
			loginLoggedInUser();
		}
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
				displayToast("Email сәтті жіберілді");
			}
			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to sent email: " + fault.getMessage());
				displayToast("Қате Email");
			}
		});
	}

	private void loginLoggedInUser() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Кіру");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		String currentUserId = Backendless.UserService.loggedInUser();

		Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Log.d(TAG, "Successful login!");
				Backendless.UserService.setCurrentUser(response);
				if (dialog != null && dialog.isShowing()) dialog.dismiss();
				Intent intent = new Intent(SignInActivity.this, StartActivity.class);
				startActivity(intent);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "failed to find such user " + fault.getMessage());
				if (dialog.isShowing())
					dialog.dismiss();
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

	private void onRegisterButtonClick() {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}

	private void loginUser() {
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (email.isEmpty()) {
			displayToast("Email толтырылмаған"); return;
		}

		if (password.isEmpty()) {
			displayToast("Құпия сөз толтырылмаған"); return;
		}

		dialog = new ProgressDialog(this);
		dialog.setTitle("Кіру");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Backendless.UserService.setCurrentUser(response);
				if (dialog.isShowing()) dialog.dismiss();
				Intent intent = new Intent(SignInActivity.this, StartActivity.class);
				startActivity(intent);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to login: " + fault.getMessage());
				if (dialog.isShowing()) dialog.dismiss();
				displayToast("Email немесе құпия сөз қате");
			}
		}, true);
	}

	private void displayToast(String s) {
		Toast.makeText(SignInActivity.this, s, Toast.LENGTH_SHORT).show();
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
