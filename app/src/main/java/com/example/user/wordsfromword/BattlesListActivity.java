package com.example.user.wordsfromword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

public class BattlesListActivity extends Activity {

	private static final String TAG = "BattlesListActivity";
	private static final int PICK_CONTACT = 2;
	private ListView listView;
	private ImageView backButton;
	private TextView leadersTextView;
	private ProgressBar progressBar;

	private ProgressDialog dialog;

	ArrayList<Battles> list;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battles_list);
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		listView = (ListView) findViewById(R.id.listView);
		backButton = (ImageView) findViewById(R.id.backButton);
		leadersTextView = (TextView) findViewById(R.id.leadersTextView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		leadersTextView.setTypeface(typeface);

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackButtonClick();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				startManagingCursor(c);
				if (c.moveToFirst()) {
					String name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
					String number = c.getString(c.getColumnIndexOrThrow(Contacts.People.NUMBER));
					Toast.makeText(this, name + " has number " + number, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void loadList() {
		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);

		BackendlessDataQuery dataQuery = new BackendlessDataQuery();
		dataQuery.setPageSize(100);
		String userId = Backendless.UserService.loggedInUser();
		String whereClause = String.format("user1.objectId = '%s' OR user2.objectId = '%s'", userId, userId);
		dataQuery.setWhereClause(whereClause);

		if (!DataHolder.getDataHolder().getList().isEmpty()) {
			displayList();
			return;
		}

		list = new ArrayList<>();

		Backendless.Persistence.of(Battle.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Battle>>() {
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
								int score1 = battles.getBattle().getScore1(), score2 = battles.getBattle().getScore2();
								if (!battle.getUser2().getObjectId().equals(Backendless.UserService.loggedInUser())) {
									opponent = battle.getUser2();
									if (score1 >= score2)
										color = 3;
									else
										color = 4;
								} else {
									opponent = battle.getUser1();
									if (score1 <= score2)
										color = 3;
									else
										color = 4;
								}
							}
						}
						battles.setColor(color);
						battles.setOpponent(opponent);
						list.add(battles);
					}
					response.nextPage(this);
				} else
					displayList();
			}

			@Override
			public void handleFault(BackendlessFault fault) {}
		});
	}

	private void displayList() {
		DataHolder.getDataHolder().setList(list);
		listView.setAdapter(new BattlesListAdapter(this));
		progressBar.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				onListViewItemClick(i);
			}
		});
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
		loadList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadList();
	}

	private void onBattleEnded(Battles battles, final int i) { // Green or Red
		battleEndActivityStart(i);
	}

	private void battleEndActivityStart(int i) {
		Intent intent = new Intent(this, VersusBattleEndedActivity.class);
		intent.putExtra("battleId", i);
		startActivity(intent);
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
		return secondPlayerPlayed;
	}

	private void onBackButtonClick() {
		onBackPressed();
	}

	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataHolder.getDataHolder().getList().clear();
	}
}
