package com.example.user.wordsfromword;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ShareActivity extends Activity {
	private static final String TAG = "ShareActivity";
	private static final int REQUEST_CODE = 1;
	private static final int REQUEST_STORAGE = 0;
	private String SHARING_TEXT = "";
	private TextView scoreTextView, nameTextView, cityTextView, scoreTextTextView1, scoreTextTextView2, sozdenSozderTextView;
	private ImageView shareImageView;
	private LinearLayout rootLinearLayout;
	private com.facebook.appevents.AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		cityTextView = (TextView) findViewById(R.id.cityTextView);
		scoreTextTextView1 = (TextView) findViewById(R.id.scoreTextTextView1);
		scoreTextTextView2 = (TextView) findViewById(R.id.scoreTextTextView2);
		sozdenSozderTextView = (TextView) findViewById(R.id.sozdensozderTextView);

		shareImageView = (ImageView) findViewById(R.id.shareImageView);

		rootLinearLayout = (LinearLayout) findViewById(R.id.rootLinearLayout);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		nameTextView.setTypeface(typeface);
		cityTextView.setTypeface(typeface);
		scoreTextView.setTypeface(typeface);
		scoreTextTextView1.setTypeface(typeface);
		scoreTextTextView2.setTypeface(typeface);
		sozdenSozderTextView.setTypeface(typeface);

		String name = getIntent().getStringExtra("name");
		String city = getIntent().getStringExtra("city");
		int score = getIntent().getIntExtra("score", 0);

		SHARING_TEXT = String.format("Мен 1 сөзден %d сөз таптым, ал сен? bit.ly/getSozdenSozder", score);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
				requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
			}
		}

		scoreTextView.setText(String.valueOf(score));
		nameTextView.setText(name);
		cityTextView.setText(city + " қаласы");

		shareImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenshot(SHARING_TEXT);
			}
		});
		rootLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenshot(SHARING_TEXT);
			}
		});
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	private void takeScreenshot(String text) {
		Date now = new Date();
		android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

		try {
			String mPath = now + ".jpg";
			File imageFile;
			if (isExternalStorageWritable()) {
				mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
				imageFile = new File(mPath);
				Log.e(TAG, "External writable");
			} else {
				Log.e(TAG, "External is not writable");
				ContextWrapper cw = new ContextWrapper(this);
				imageFile = cw.getDir(mPath, Context.MODE_PRIVATE);
				imageFile.createNewFile();
			}
//			String mPath = getCacheDir().toString() + "/" + now + ".jpg";
			View v1 = getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			if (v1 == null)
				Log.e(TAG, "v1 is null WTF??");
			if (v1.getDrawingCache() == null)
				Log.e(TAG, "v1.getDrawingCache() is null -o-");
			if (!v1.isDrawingCacheEnabled())
				Log.e(TAG, "v1.isDrawingCacheEnabled() is false /-\\");
			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);

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
//			share.setPackage("com.instagram.android");
			ArrayList<String> arr = new ArrayList<>(Arrays.asList(new String[]{"whatsapp", "vk", "facebook", "insta", "twitter", "gm", "snap", "imo", "viber", "skype", "line"}));
//			startActivityForResult(share, REQUEST_CODE);
			share(arr, imageFile, text);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		setResult(RESULT_OK);
		finish();
	}
}
