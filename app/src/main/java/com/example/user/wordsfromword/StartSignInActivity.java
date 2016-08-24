package com.example.user.wordsfromword;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.example.user.wordsfromword.R;
import com.facebook.BuildConfig;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.io.IOException;

public class StartSignInActivity extends Activity {
	private static final String TAG = "StartSignInActivity";
	private static final int REQEST_CODE = 1;
	private Button numberLoginButton, addNumberButton;
	private static int APP_REQUEST_CODE = 99;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_sign_in);

		if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
			Log.e(TAG, "Exit");
			finish();
		}

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		if (BuildConfig.DEBUG) {
			FacebookSdk.setIsDebugEnabled(true);
			FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		}
		AccountKit.initialize(getApplicationContext());

		AccessToken accessToken = AccountKit.getCurrentAccessToken();

		if (accessToken != null) {
			startGame(true);
		}

		numberLoginButton = (Button) findViewById(R.id.numberLoginButton);
		addNumberButton = (Button) findViewById(R.id.addNumberButton);

		numberLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onNumberLoginClick();
			}
		});

		addNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onAddNumberClick();
			}
		});
	}

	private void onAddNumberClick() {
		Intent intent = new Intent(this, AddNumberActivity.class);
		startActivity(intent);
	}

	private void onNumberLoginClick() {
		final Intent intent = new Intent(this, AccountKitActivity.class);

		AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
				new AccountKitConfiguration.AccountKitConfigurationBuilder(
						LoginType.PHONE,
						AccountKitActivity.ResponseType.TOKEN)
				.setDefaultCountryCode("7")
				.setTitleType(AccountKitActivity.TitleType.APP_NAME)
				.setFacebookNotificationsEnabled(false)
				.setReadPhoneStateEnabled(false);

		intent.putExtra(
				AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
				configurationBuilder.build());
		startActivityForResult(intent, APP_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
			AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
			String toastMessage;
			if (loginResult.getError() != null) {
//				toastMessage = loginResult.getError().getErrorType().getMessage();
				toastMessage = "Кіру сәтсіз өтті, қателік: " + (loginResult.getError());
			} else if (loginResult.wasCancelled()) {
				toastMessage = "Кіру сәтсіз өтті";
			} else {
				if (loginResult.getAccessToken() != null) {
					toastMessage = "Кіру сәтті өтті!";
					AccessToken token = loginResult.getAccessToken();
					Log.e(TAG, "Token = " + token.toString());
				} else {
					toastMessage = "Кіру сәтті өтті!";
					// String.format("Success:%s...", loginResult.getAuthorizationCode().substring(0,10));
				}

				// If you have an authorization code, retrieve it from
				// loginResult.getAuthorizationCode()
				// and pass it to your server and exchange it for an access token.

				// Success! Start your next activity...
				startGame(false);
			}

			// Surface the result to your user in an appropriate way.
			Toast.makeText(
					this,
					toastMessage,
					Toast.LENGTH_LONG)
					.show();
		} else {
			if (requestCode == REQEST_CODE) {
				if (data != null && data.getBooleanExtra("EXIT", false)) {
					finish();
				}
			}
		}
	}
	
	private void startGame(boolean skip) { // TODO: Configure user
		Intent intent = new Intent(StartSignInActivity.this, ConfigureSettingsActivity.class);
		intent.putExtra("skip", skip);
		startActivityForResult(intent, REQEST_CODE);
	}
}
