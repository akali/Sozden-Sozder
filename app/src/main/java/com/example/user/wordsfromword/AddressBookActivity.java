package com.example.user.wordsfromword;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.TreeSet;

public class AddressBookActivity extends Activity {
	private static final String TAG = "AddressBookActivity";
	private ListView listView;
	private ImageView backButton, sendMessageImageView;
	private ProgressBar progressBar;
	private TreeSet<Integer> selected;
	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_book);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		selected = new TreeSet<>();

		listView = (ListView) findViewById(R.id.listView);
		backButton = (ImageView) findViewById(R.id.backButton);
		sendMessageImageView = (ImageView) findViewById(R.id.sendMessageImageView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		getContacts();

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackButtonClick();
			}
		});
		sendMessageImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onSendMessageClick();
			}
		});
	}

	private void onSendMessageClick() {
		
	}

	private void onBackButtonClick() {
		finish();
	}

	private void getContacts() {
		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);

		ArrayList<Contact> contactsArrayList = new ArrayList<>();

		Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			int number = Integer.valueOf(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			contactsArrayList.add(new Contact(name, number));
		}
		c.close();

		listView.setAdapter(new ContactsAdapter(this, contactsArrayList));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				onListViewItemClicked(view, i);
			}
		});
	}

	private void onListViewItemClicked(View view, int i) {
		if (selected.contains(i)) {
			((ImageView)(view.findViewById(R.id.checkBoxImageView))).setImageResource(android.R.drawable.checkbox_off_background);
			selected.remove(i);
		} else {
			((ImageView)(view.findViewById(R.id.checkBoxImageView))).setImageResource(android.R.drawable.checkbox_on_background);
			selected.add(i);
		}
	}
}
