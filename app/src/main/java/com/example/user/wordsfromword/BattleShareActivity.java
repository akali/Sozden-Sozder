package com.example.user.wordsfromword;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class BattleShareActivity extends Activity {
	private static final String TAG = "BattleShareActivity";
	private static final int REQUEST_CODE = 1;
	private TextView firstNameTextView, firstCityTextView, firstScoreTextView;
	private TextView secondNameTextView, secondCityTextView, secondScoreTextView;
	private TextView titleTextView;
	private String loseText = "Мен қарсыласымнан жеңілдім.\nАл сен ұта аласың ба?";
	private String winText = "Мен қарсыласымды жеңдім, \nенді сенің кезегің!";
	private String equalText = "Сөзден сөздер ойынын ойна!";
	private LinearLayout rootLinearLayout;
	private ImageView shareImageView;

	private ArrayList<Integer> backgroundImages;

	private AppEventsLogger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle_share);
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);

		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent("Started activity: " + TAG);

		backgroundImages = new ArrayList<>();
		backgroundImages.add(R.drawable.a);
		backgroundImages.add(R.drawable.aa);
		backgroundImages.add(R.drawable.ab);
		backgroundImages.add(R.drawable.ac);
		backgroundImages.add(R.drawable.ad);
		backgroundImages.add(R.drawable.ae);
		backgroundImages.add(R.drawable.af);
		backgroundImages.add(R.drawable.ag);

		firstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
		firstCityTextView = (TextView) findViewById(R.id.firstCityTextView);
		firstScoreTextView = (TextView) findViewById(R.id.firstScoreTextView);

		secondNameTextView = (TextView) findViewById(R.id.secondNameTextView);
		secondCityTextView = (TextView) findViewById(R.id.secondCityTextView);
		secondScoreTextView = (TextView) findViewById(R.id.secondScoreTextView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);

		rootLinearLayout = (LinearLayout) findViewById(R.id.rootLinearLayout);
		shareImageView = (ImageView) findViewById(R.id.shareImageView);

		rootLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenshot("@" + "sozdensozder");
			}
		});
		shareImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenshot("@" + "sozdensozder");
			}
		});

		rootLinearLayout.setBackgroundResource((int) backgroundImages.get(Rand.getRand().getRnd().nextInt(backgroundImages.size())));

		int battleId = getIntent().getIntExtra("battleId", 0);
		Battle battle = DataHolder.getDataHolder().getList().get(battleId).getBattle();
		BackendlessUser user1 = battle.getUser1(), user2 = battle.getUser2();
		int score1 = battle.getScore1(), score2 = battle.getScore2();
		firstNameTextView.setText((CharSequence) user1.getProperty("name"));
		firstCityTextView.setText((CharSequence) user1.getProperty("city"));
		firstScoreTextView.setText(String.valueOf(score1));
		secondNameTextView.setText((CharSequence) user2.getProperty("name"));
		secondCityTextView.setText((CharSequence) user2.getProperty("city"));
		secondScoreTextView.setText(String.valueOf(score2));

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
		firstNameTextView.setTypeface(typeface);
		firstCityTextView.setTypeface(typeface);
		firstScoreTextView.setTypeface(typeface);
		secondNameTextView.setTypeface(typeface);
		secondCityTextView.setTypeface(typeface);
		secondScoreTextView.setTypeface(typeface);
		titleTextView.setTypeface(typeface);

		boolean win;

		int color = DataHolder.getDataHolder().getList().get(battleId).getColor();

		win = color == 3;

		if (!win) {
			titleTextView.setText(loseText);
		} else {
			titleTextView.setText(winText);
		}

		if (score1 == score2) {
			titleTextView.setText(equalText);
		}
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
			share.setPackage("com.instagram.android");
			startActivityForResult(share, REQUEST_CODE);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		setResult(RESULT_OK);
		finish();
	}
}
