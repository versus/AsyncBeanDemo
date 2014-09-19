package info.android15.asyncbeandemo;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class UidGenerator {

	private static final String PREFERENCES_NAME = "uid_generator";
	private static final String UID_COUNTER_KEY = "uid_counter";

	private static int uid_counter;

	public static void init(Application app) {

		final SharedPreferences pref = app.getSharedPreferences(PREFERENCES_NAME, 0);

		uid_counter = pref.getInt(UID_COUNTER_KEY, 1);

		Log.v(UidGenerator.class.getSimpleName(), "counter restored (" + uid_counter + ")");

		app.registerActivityLifecycleCallbacks(new ActivityLifecycleAdapter() {

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

				// there is nothing bad in having infinitive looping counter but it is just easier to
				// control smaller numbers when debugging

				if (savedInstanceState == null && Intent.ACTION_MAIN.equals(activity.getIntent().getAction())) {

					uid_counter = 1;

					Log.v(UidGenerator.class.getSimpleName(), "counter reset (" + uid_counter + ")");
				}
			}

			@Override
			public void onActivityDestroyed(Activity activity) {

				pref.edit().putInt(UID_COUNTER_KEY, uid_counter).apply();

				Log.v(UidGenerator.class.getSimpleName(), "counter saved (" + uid_counter + ")");
			}
		});
	}

	public static int generate() {

		if (uid_counter == 0)
			throw new IllegalStateException(UidGenerator.class.getName() + ".init() should be called first");

		return uid_counter = uid_counter >= 0x00FFFFFF ? 1 : uid_counter + 1; // 0x00FFFFFF makes id compatible with View.setId() 
	}
}
