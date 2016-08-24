package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SignUpActivity extends Activity {
	private static final String[] CITIES = {"Қалаңызды таңдаңыз", "Абай", "Ақкөл", "Ақсай", "Ақсу", "Ақтау", "Ақтөбе",
											"Алға", "Алматы", "Арал", "Арқалық", "Арыс", "Астана",
											"Атбасар", "Атырау", "Аягөз", "Байқоңыр", "Балқаш",
											"Булаев", "Державин", "Ерейментау", "Есік", "Есіл",
											"Жаңаөзен", "Жаңатас", "Жаркент", "Жезқазған", "Жем",
											"Жетісай", "Жетіқара", "Зайсан", "Зыряновск", "Қазалы",
											"Қандыағаш", "Қапшағай", "Қарағанды", "Қаражал",
											"Қаратау", "Қарқаралы", "Қаскелең", "Кентау",
											"Көкшетау", "Қостанай", "Құлсары", "Курчатов",
											"Қызылорда", "Ленгер", "Лисаковск", "Макинск", "Мамлют",
											"Павлодар", "Петропавл", "Приозер", "Риддер", "Рудный",
											"Саран", "Сарқант", "Сарыағаш", "Сәтбаев", "Семей",
											"Сергеев", "Серебрянск", "Степногор", "Степняк",
											"Тайынша", "Талғар", "Талдықорған", "Тараз", "Текелі",
											"Темір", "Теміртау", "Түркістан", "Орал", "Өскемен",
											"Үшарал", "Үштөбе", "Форт-Шевченко", "Хромтау",
											"Шардара", "Шалқар", "Шар", "Шахтинск", "Шемонаиха",
											"Шу", "Шымкент", "Щучинск", "Екібастұз", "Эмбі"};
	private static final String TAG = "SignUpActivity";
	private EditText emailEditText, nameEditText, passwordEditText;
	private Button registerButton, backButton;
	private Spinner cityEditText;
	private String currentCity;
	private ProgressDialog dialog;
	private boolean cityNotChosen = false;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		registerButton = (Button) findViewById(R.id.registerButton);
		backButton = (Button) findViewById(R.id.backButton);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		((TextView) findViewById(R.id.registerTextTextView)).setTypeface(typeface);
		registerButton.setTypeface(typeface);
		backButton.setTypeface(typeface);

		emailEditText.setText("");
		nameEditText.setText("");
		passwordEditText.setText("");

		cityEditText = (Spinner) findViewById(R.id.cityEditText);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CITIES);
		cityEditText.setAdapter(adapter);
		
		cityEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				onCitySelected(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				onCitySelected(0);
			}
		});
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onRegisterButtonClick();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	private void onCitySelected(int cityId) {
		if (cityId > 0) {
			cityNotChosen = false;
			currentCity = CITIES[cityId];
		} else {
			cityNotChosen = true;
		}
	}

	private void onRegisterButtonClick() {
		String name = nameEditText.getText().toString();
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (email.isEmpty()) {
			displayToast("Email толтырылмаған"); return;
		}

		if (password.isEmpty()) {
			displayToast("Құпия сөз толтырылмаған"); return;
		}

		if (name.isEmpty()) {
			displayToast("Лақап толтырылмаған"); return;
		}

		if (cityNotChosen || currentCity == null || currentCity.isEmpty()) {
			displayToast("Қала таңдалмаған"); return;
		}

		BackendlessUser user = new BackendlessUser();
		user.setEmail(email);
		user.setPassword(password);
		user.setProperty("name", name);
		user.setProperty("city", currentCity);

		Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Log.d(TAG, "Successful registration");

				displayToast("Сәтті тіркелу");
				loginAndStartGame();
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to register: " + fault.getMessage());
				displayToast("Failed to register");
			}
		});
	}

	private void loginAndStartGame() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Кіру");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Backendless.UserService.setCurrentUser(response);
				if (dialog.isShowing())
					dialog.dismiss();
				Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
				startActivity(intent);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to login after register: " + fault.getMessage());
				if (dialog.isShowing())
					dialog.dismiss();
				displayToast("Кіру сәтсіз өтті");
			}
		}, true);
	}

	private void displayToast(String s) {
		Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
}
