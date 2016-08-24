package com.example.user.wordsfromword;

import android.app.Application;

import com.backendless.Backendless;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.accountkit.AccountKit;

/**
 * Created by aqali on 8/20/16.
 */
public class WordsFromWordApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FacebookSdk.sdkInitialize(getApplicationContext());
		if (BuildConfig.DEBUG) {
			FacebookSdk.setIsDebugEnabled(true);
			FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		}
		AccountKit.initialize(getApplicationContext());
		Backendless.initApp(this, Konst.APP_ID, Konst.ANDROID_KEY, Konst.VERSION);
	}
}
