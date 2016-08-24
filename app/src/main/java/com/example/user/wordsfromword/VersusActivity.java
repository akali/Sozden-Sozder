package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VersusActivity extends Activity {
	private static final String TAG = "VersusActivity";
	private static final int REQUEST_CODE_START = 1;
	private static final int REQUEST_CODE = 2;
	private ArrayList<BackendlessUser> data;
	private ProgressBar progressBar;
	private ListView listView;
	private ArrayList<Battles> list;
	private Set<String> battling;
	private int lastId;
	private ProgressDialog dialog;
	private ImageView backButton;
	private ImageView refreshImageView;
	private boolean isGetting;
	private TextView leadersTextView;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus);

		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		listView = (ListView) findViewById(R.id.listView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		refreshImageView = (ImageView) findViewById(R.id.refreshImageView);
		refreshImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getUsersList();
			}
		});

		leadersTextView = (TextView) findViewById(R.id.leadersTextView);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		leadersTextView.setTypeface(typeface);

		new AlertDialog.Builder(this)
				.setTitle("Invitation")
				.setMessage("You can invite your friends for battling!")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						sendInvitation();
					}
				})
				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				})
				.show();

		backButton = (ImageView) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	private void sendInvitation() {
		String text = "bit.ly/getSozdenSozder"; // TODO: Change text
		Date now = new Date();
		android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

		try {
			String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
			File imageFile;
			imageFile = new File(mPath);

			Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("logo.png"));

			FileOutputStream outputStream = new FileOutputStream(imageFile);
			int quality = 100;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			outputStream.flush();
			outputStream.close();

			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/*");
			Uri uri = Uri.fromFile(imageFile);
			share.putExtra(Intent.EXTRA_STREAM, uri);
			share.putExtra(Intent.EXTRA_TEXT, text);
			share(new ArrayList<>(Arrays.asList(new String[]{
					"whatsapp", "vk", "facebook", "insta", "twitter", "gm",
					"snap", "imo", "viber", "skype", "line"})),
					imageFile, text);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private boolean contains(ArrayList <String> arr, String s) {
		for (String t : arr) {
			if (s.contains(t))
				return true;
		}
		return false;
	}

	public void share(ArrayList <String> apps, File imagePath, String message) {
		try {
			List<Intent> targetedShareIntents = new ArrayList<>();

			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("image/*");
			List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);

			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					if (contains(apps, info.activityInfo.packageName.toLowerCase()) ||
							contains(apps, info.activityInfo.name.toLowerCase())) {

						Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
						targetedShare.setType("image/*");
						targetedShare.putExtra(Intent.EXTRA_TEXT, message);
						targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imagePath));
						targetedShare.setPackage(info.activityInfo.packageName);
						targetedShareIntents.add(targetedShare);
					}
				}
				Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
				startActivityForResult(chooserIntent, REQUEST_CODE);
			}
		} catch (Exception e) {
//			Log.v("VM", "Exception while sending image on" + apps + " " + e.getMessage());
		}
	}

	private void getUsersList() {
		if (isGetting) return;
		isGetting = true;

		if (!DataHolder.getDataHolder().getList().isEmpty()) {
			displayLeaderboard();
			return;
		}

		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		Log.d(TAG, "Loading UsersList");
		final BackendlessDataQuery query = new BackendlessDataQuery();
		query.setPageSize(100);
		query.setWhereClause("objectId <> '" + Backendless.UserService.loggedInUser() + "'");
		this.data = new ArrayList<>();
		this.battling = new HashSet<>();
		this.list = new ArrayList<>();

		lastId = 1;

		Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
			@Override
			public void handleResponse(BackendlessCollection<BackendlessUser> response) {
				if (response.getCurrentPage().size() > 0) {
					for (BackendlessUser user : response.getCurrentPage())
						data.add(user);
					response.nextPage(this);
				} else {
					displayLeaderboard();
				}
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				Log.e(TAG, "failed to load leaderboard: " + fault.getMessage());
			}
		});
	}

	// blue: String whereClause = String.format("user2.objectId = '%s' AND secondPlayerPlayed = false", userId);
	// yellow: String whereClause = String.format("user1.objectId = '%s' AND secondPlayerPlayed = false", userId);
	// greenRed: String whereClause = String.format("user1.objectId = '%s' AND secondPlayerPlayed = true AND gameFinished = false", userId);
	// all: String.format("(user2.objectId = '%s' AND secondPlayerPlayed = false) OR
	//                     (user1.objectId = '%s' AND
	//                          (secondPlayerPlayed = false OR (secondPlayerPlayed = true AND gameFinished = false)))", userId)

	private void load() {
		String userId = Backendless.UserService.loggedInUser();
		BackendlessDataQuery query = new BackendlessDataQuery();
		query.setPageSize(100);
		String whereClause =
				String.format(
						"(user2.objectId = '%s' AND secondPlayerPlayed = false) " +
								"OR (user1.objectId = '%s' " +
									"AND (secondPlayerPlayed = false " +
										  "OR (secondPlayerPlayed = true AND gameFinished = false)))", userId, userId);
		query.setWhereClause(whereClause);
		Backendless.Persistence.of(Battle.class).find(query, new AsyncCallback<BackendlessCollection<Battle>>() {
			@Override
			public void handleResponse(BackendlessCollection<Battle> response) {
				if (response.getCurrentPage().size() > 0) {
					for (Battle battle : response.getCurrentPage()) {
						Battles battles = new Battles(battle);
						int color = 0;
						BackendlessUser opponent = battle.getUser1();
						if (itsYellow(battle)) {
							color = 1;
							opponent = battle.getUser2();
						} else {
							if (itsBlue(battle)) {
								color = 2;
							} else {
								if (itsGreenRed(battle)) {
									opponent = battle.getUser2();
									color = 3;
									int score1 = battles.getBattle().getScore1(), score2 = battles.getBattle().getScore2();
									if (score1 < score2) {
										color = 4;
									}
								}
							}
						}
						battles.setColor(color);
						battles.setOpponent(opponent);
						battles.setId(++lastId);
						list.add(battles);
						battling.add(battles.getOpponent().getObjectId());
					}
					response.nextPage(this);
				} else
					displayLeaderboard();
			}

			@Override
			public void handleFault(BackendlessFault fault) {}
		});
	}

	private boolean itsBlue(Battle battle) {
		String objectId = battle.getUser2().getObjectId();
		boolean secondPlayerPlayed = battle.isSecondPlayerPlayed();
		return objectId.equals(Backendless.UserService.loggedInUser()) && !secondPlayerPlayed;
	}

	private boolean itsYellow(Battle battle) {
		String objectId = battle.getUser1().getObjectId();
		boolean secondPlayerPlayed = battle.isSecondPlayerPlayed();
		return objectId.equals(Backendless.UserService.loggedInUser()) && !secondPlayerPlayed;
	}

	private boolean itsGreenRed(Battle battle) {
		String objectId = battle.getUser1().getObjectId();
		boolean secondPlayerPlayed = battle.isSecondPlayerPlayed(), gameFinished = battle.isGameFinished();
		return objectId.equals(Backendless.UserService.loggedInUser()) && secondPlayerPlayed && !gameFinished;
	}

	private void displayLeaderboard() {
		Log.d(TAG, "Successfully loaded leaderboard");
		Collections.sort(data, new Comparator<BackendlessUser>() {
			@Override
			public int compare(BackendlessUser backendlessUser, BackendlessUser t1) {
				int score1 = 0, score2 = 0;
				if (backendlessUser.getProperty("score") != null)
					score1 = (int) backendlessUser.getProperty("score");
				if (t1.getProperty("score") != null)
					score2 = (int) t1.getProperty("score");
				return -(Integer.valueOf(score1)).compareTo(score2);
			}
		});
		for (BackendlessUser user : data) {
			if (!user.getObjectId().equals(Backendless.UserService.loggedInUser())) {
				if (!battling.contains(user.getObjectId())) {
					Battle battle = new Battle();
					battle.setUser1(Backendless.UserService.CurrentUser());
					battle.setFirstPlayerId(Backendless.UserService.loggedInUser());
					battle.setUser2(user);
					Battles battles = new Battles(battle);
					battles.setId(++lastId);
					list.add(battles);
				}
			}
		}
		Collections.sort(list, new Comparator<Battles>() {
			@Override
			public int compare(Battles battles, Battles t1) {
				return Integer.valueOf(battles.getId()).compareTo(t1.getId());
			}
		});
		DataHolder.getDataHolder().setList(list);
		progressBar.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		listView.setAdapter(new VersusAdapter(this, this.data));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Log.e(TAG, "Clicking on list");
				onListViewItemClick(i);
			}
		});
		isGetting = false;
	}

	private void onListViewItemClick(final int i) {
		if (list.get(i).getColor() >= 3)
			fightWith(i);
		else {
			if (list.get(i).getColor() == 1) {
				displayToast("Қарсыласыңыздың ойынын күтіңіз!");
			} else {
				new AlertDialog.Builder(this)
						.setTitle("Қарсыласты таңдау")
						.setMessage("Сенімдісіз бе?")
						.setPositiveButton("Иә", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int j) {
								fightWith(i);
							}
						})
						.setNegativeButton("Жоқ", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

							}
						})
						.setCancelable(false)
						.show();
			}
		}
	}

	private void fightWith(int i) {
		if (list.get(i).getColor() == 0 || list.get(i).getColor() == 2) { // Gray or Blue
			startBattle(i); return;
		}
		onBattleEnded(list.get(i), i); // Green or Red
	}

	private void startBattle(int i) { // Gray or Blue
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("battleId", i);
		startActivity(intent);
		getUsersList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getUsersList();
	}

	private void onBattleEnded(Battles battles, final int i) { // Green or Red
		dialog = new ProgressDialog(this);
		dialog.setTitle("Нәтежие сақталуда");
		dialog.setMessage("Күте тұрыңыз");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		battles.getBattle().setGameFinished(true);
		Backendless.Persistence.of(Battle.class).save(battles.getBattle(), new AsyncCallback<Battle>() {
			@Override
			public void handleResponse(Battle response) {
				Log.d(TAG, "Successfully saved battle");
				if (dialog.isShowing()) dialog.dismiss();
				battleEndActivityStart(i);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				if (dialog.isShowing()) dialog.dismiss();
			}
		});
	}

	private void battleEndActivityStart(int i) {
		Intent intent = new Intent(VersusActivity.this, VersusBattleEndedActivity.class);
		intent.putExtra("battleId", i);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		getUsersList();
	}

	private void displayToast(String s) {
		Toast.makeText(VersusActivity.this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataHolder.getDataHolder().getList().clear();
	}
}
