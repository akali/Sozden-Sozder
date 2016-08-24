package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.bumptech.glide.Glide;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ConfigureSettingsActivity extends Activity {
	private static final String TAG = "ConfigureSettingsActivity";
	private static final int REQUEST_CODE = 1;
	private static final int PICK_IMAGE = 2;
	private String[] CITIES = {"Қалаңызды таңдаңыз", "Абай", "Ақкөл", "Ақсай", "Ақсу", "Ақтау", "Ақтөбе",
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
	private ImageView nextImageView, avatarImageView, backButton;
	private EditText nameEditText;
	private Spinner citySpinner;
	private Bitmap bm;
	private BackendlessFile backendlessFile;

	private ProgressDialog dialog;

	private String number, kitId;
	private String city, avatarUrl = "https://api.backendless.com/BF5F12CB-50AC-03C1-FF3D-0FFF4CAA8F00/v1/files/avatars/avatar.png",
			       name;
	private boolean cityNotChosen = true, avatarNotChosen = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_settings);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		boolean skip = getIntent().getBooleanExtra("skip", false);
		if (skip) {
			dialog = new ProgressDialog(this);
			dialog.setTitle("Login");
			dialog.setMessage("Wait please");
			dialog.setCancelable(false);
			dialog.show();
			String userId = Backendless.UserService.loggedInUser();
			Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
				@Override
				public void handleResponse(BackendlessUser response) {
					Backendless.UserService.login(response.getEmail(), response.getPassword(), new AsyncCallback<BackendlessUser>() {
						@Override
						public void handleResponse(BackendlessUser response) {
							Log.e(TAG, response.getObjectId());
							if (dialog.isShowing()) dialog.dismiss();
							startGame();
						}

						@Override
						public void handleFault(BackendlessFault fault) {}
					});
				}

				@Override
				public void handleFault(BackendlessFault fault) {
					if (dialog.isShowing()) dialog.dismiss();
				}
			});
		}

		nextImageView = (ImageView) findViewById(R.id.nextImageView);
		avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		citySpinner = (Spinner) findViewById(R.id.cityEditText);
		backButton = (ImageView) findViewById(R.id.backButton);

		avatarImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onAvatarImageViewClick();
			}
		});

		Backendless.UserService.findById("B73FE5DD-2B17-466B-FF4D-9054616AA400", new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Backendless.UserService.setCurrentUser(response);
				Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
					@Override
					public void handleResponse(BackendlessCollection<BackendlessUser> response) {
						Log.e(TAG, String.valueOf(response.getCurrentPage().size()));
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						Log.e(TAG, "FAiled :(");
					}
				});
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "FAiled to get User :( " + fault.getMessage());
			}
		});

		AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
			@Override
			public void onSuccess(final Account account) {
				kitId = account.getId();
				number = account.getPhoneNumber().toString();

				Log.d(TAG, "Successfully got account data: kitId = " + kitId + "; number = " + number);

				String whereClause = "number = '" + number + "'";

				BackendlessDataQuery query = new BackendlessDataQuery();
				query.setWhereClause(whereClause);
				Log.d(TAG, whereClause);

				dialog = new ProgressDialog(ConfigureSettingsActivity.this);
				dialog.setTitle("Кіру");
				dialog.setMessage("Күте тұрыңыз");
				dialog.setCancelable(false);
				dialog.show();

				Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
					@Override
					public void handleResponse(BackendlessCollection<BackendlessUser> response) {
						if (response.getCurrentPage().size() > 0) {
							if (response.getCurrentPage().size() == 1) { // Login existing user
								Log.d(TAG, "Login existing user");
								loginUser(response.getCurrentPage().get(0));
							} else {
								Log.d(TAG, "Login w\\ existing user and delete clone");
								loginAndDelete(response.getCurrentPage());
							}
						} else { // First registration
							Log.d(TAG, "First login(REGISTRATION)");
							registerUser();
						}
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						Log.e(TAG, "Failed to retrieve data: " + fault.getMessage() + "; Error code = " + fault.getCode() + "; Detail = " + fault.getDetail());
					}
				});
			}

			@Override
			public void onError(final AccountKitError error) {
				Log.e(TAG, "Failed to getCurrentAccount :" +
						" error.getUserFacingMessage() = " + error.getUserFacingMessage()
						+ "; error.getErrorType() = " + error.getErrorType()
						+ "; error.getDetailErrorCode() = " + error.getDetailErrorCode());
			}
		});

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CITIES);
		citySpinner.setAdapter(adapter);
		citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				onCitySelected(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				onCitySelected(0);
			}
		});

		nextImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onNextImageViewClick();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	private void loginAndDelete(List <BackendlessUser> response) {
		int id = 0, fake = 1;
		final ArrayList<BackendlessUser> users = new ArrayList<>();
		int score = 0;
		for (BackendlessUser user : response) {
			users.add(user);
			if (user.getProperty("score") != null)
				score += (int) user.getProperty("score");
		}
		if ((boolean) users.get(id).getProperty("registerUsingNumber")) {
			id = 1; fake = 0;
		}

		users.get(id).setProperty("score", score);
		users.get(fake).setProperty("score", 0);

		final int finalFake = fake;
		Backendless.UserService.update(users.get(id), new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Backendless.UserService.setCurrentUser(response);
				Log.d(TAG, "Successfully updater user data!");
				Backendless.UserService.login(response.getEmail(), response.getPassword(), new AsyncCallback<BackendlessUser>() {
					@Override
					public void handleResponse(BackendlessUser response) {
						Log.d(TAG, "Successfully logged in!");
						deleteUser(users.get(finalFake));
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						Log.e(TAG, "Failed to login user: " + fault.getMessage());
					}
				}, true);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to update user: " + fault.getMessage());
			}
		});
	}

	private void deleteUser(BackendlessUser user) {
		Backendless.Data.of(BackendlessUser.class).findById(user.getObjectId(), new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Log.e(TAG, "Successfully retrieved clone user: " + response.getEmail());
				Backendless.Data.of(BackendlessUser.class).remove(response, new AsyncCallback<Long>() {
					@Override
					public void handleResponse(Long response) {
						Log.d(TAG, "Clone user was successfully removed from database");
						if (dialog.isShowing()) dialog.dismiss();
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						Log.e(TAG, "Failed to remove clone user: " + fault.getMessage());
						if (dialog.isShowing()) dialog.dismiss();
					}
				});
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to retrieve clone user: " + fault.getMessage());
			}
		});
	}

	private void loginUser(BackendlessUser user) { // Logging in existing user
		Log.d(TAG, "user email: " + user.getEmail());
		Log.d(TAG, "user password: " + user.getPassword());
		Log.d(TAG, "user objectId: " + user.getObjectId());

		Backendless.UserService.findById(user.getObjectId(), new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				Log.d(TAG, "Successfully got user data: " + response.toString());
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to load user: " + fault.getMessage());
			}
		});

		Backendless.UserService.login(user.getEmail(), user.getPassword(), new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) { // Successful login, ending registration
				Backendless.UserService.setCurrentUser(response);
				if (dialog.isShowing()) dialog.dismiss();
				setLocks(response);
				Log.d(TAG, "Successful login!");
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to login: " + fault.getMessage());
				if (dialog.isShowing()) dialog.dismiss();
			}
		}, true);
	}

	private void registerUser() {
		final BackendlessUser user = new BackendlessUser();
		user.setEmail(number.substring(1) + "@bmail.com"); // Fake email
		user.setPassword("123"); // Fake password
		user.setProperty("number", String.valueOf(number)); // Real phone number
		user.setProperty("registerUsingNumber", true); // Setting flag
		user.setProperty("avatarUrl", avatarUrl); // Setting default avatar

		Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) { // Successful registration
				Log.e(TAG, "Successful registration");
				loginUser(response);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to register: " + fault.getMessage());
				if (dialog.isShowing()) dialog.dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		logout();
	}

	private void logout() {
		Backendless.UserService.logout(new AsyncCallback<Void>() {
			@Override
			public void handleResponse(Void response) {
				Log.d(TAG, "Successful logout!");
				AccountKit.logOut();
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to logout: " + fault.getMessage());
			}
		});
	}

	private void onAvatarImageViewClick() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE);
	}

	private void onNextImageViewClick() {
		name = nameEditText.getText().toString();
		if (name.isEmpty()) {
			displayToast("Name is empty"); return;
		}
		if (cityNotChosen) {
			displayToast("City not chosen"); return;
		}
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("Wait please");
		dialog.setTitle("Uploading");
		dialog.show();
		if (bm != null) {
			String userId = Backendless.UserService.loggedInUser();
			Backendless.Files.Android.upload(bm, Bitmap.CompressFormat.JPEG, 100,
					"avatar " + userId + System.currentTimeMillis() + ".jpg", "avatars", new AsyncCallback<BackendlessFile>() {
						@Override
						public void handleResponse(BackendlessFile response) {
							backendlessFile = response;
							avatarUrl = backendlessFile.getFileURL();
							saveUser();
						}

						@Override
						public void handleFault(BackendlessFault fault) {
							Log.e(TAG, "Failed to upload file: " + fault.getMessage());
						}
					});
		} else {
			saveUser();
		}
	}

	private void saveUser() {
		String userId = Backendless.UserService.loggedInUser();
		Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser response) {
				BackendlessUser user = Backendless.UserService.CurrentUser();
				user.setProperty("avatarUrl", avatarUrl);
				user.setProperty("name", name);
				user.setProperty("city", city);
				Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
					@Override
					public void handleResponse(BackendlessUser response) {
						Log.d(TAG, "Successful update");
						if (dialog.isShowing()) dialog.isShowing();
						startGame();
					}

					@Override
					public void handleFault(BackendlessFault fault) {
						Log.e(TAG, "Failed to update: " + fault.getMessage());
						if (dialog.isShowing()) dialog.isShowing();
						displayToast("Error caused, please continue sooner");
					}
				});
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "Failed to find by id: " + fault.getMessage());
				if (dialog.isShowing()) dialog.isShowing();
			}
		});
	}

	private void startGame() {
		if (dialog.isShowing()) dialog.isShowing();
		Intent intent = new Intent(ConfigureSettingsActivity.this, StartActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		finish();
	}

	private void onCitySelected(int cityId) {
		if (cityId > 0) {
			cityNotChosen = false;
			city = CITIES[cityId];
		} else {
			cityNotChosen = true;
		}
	}

	private void setLocks(BackendlessUser user) {
		if (user.getProperty("city") != null) {
			city = (String) user.getProperty("city");
			CITIES = new String[]{city};

			ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CITIES);
			citySpinner.setAdapter(adapter);
			citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
					cityNotChosen = false;
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {
					cityNotChosen = false;
				}
			});
		}
		if (user.getProperty("avatarUrl") != null) {
			avatarUrl = (String) user.getProperty("avatarUrl");
		}
		Log.e(TAG, avatarUrl);
		// TODO: add placeholder for Glide
		Glide
				.with(this)
				.load(avatarUrl)
				.centerCrop()
				.into(avatarImageView);
		if (user.getProperty("name") != null) {
			name = (String) user.getProperty("name");
			nameEditText.setText(name);
		}
	}

	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && data.getBooleanExtra("EXIT", false)) {
			Intent intent = new Intent();
			if (!data.getBooleanExtra("toLogin", false))
				intent.putExtra("EXIT", true);
			setResult(RESULT_OK, intent);
			finish();
		}
		if (requestCode == PICK_IMAGE) {
			if (resultCode == RESULT_OK) {
				if (data == null) return;
				try {
					InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
					bm = BitmapFactory.decodeStream(inputStream);
					bm = Bitmap.createScaledBitmap(bm, 200, 200, false);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
					bm = BitmapFactory.decodeStream(new ByteArrayInputStream(outStream.toByteArray()));
					avatarImageView.setImageBitmap(bm);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
