/*
 * The MIT License
 *
 * Copyright 2014 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.routine_work.notepad.prefs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.routine_work.notepad.R;
import org.routine_work.notepad.ReceiveTextActivity;
import org.routine_work.utils.Log;

public class ShareDataPreferenceFragment extends PreferenceFragment
	implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private static final String LOG_TAG = "simple-notepad";

	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notepad_preference_share_data);
		sharedPreferences = getPreferenceManager().getSharedPreferences();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		Log.v(LOG_TAG, "Bye");
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		Log.v(LOG_TAG, "Hello");
		Log.i(LOG_TAG, "shared preference " + key + " is changed.");

		final String receiveTextKey = getString(R.string.receive_text_key);
		if (key.equals(receiveTextKey))
		{
			// update component enable/disable
			boolean defaultValue = getResources().getBoolean(R.bool.receive_text_default_value);
			boolean receiveTextEnabled = prefs.getBoolean(key, defaultValue);
			setReceiveTextComponentEnabled(receiveTextEnabled);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void setReceiveTextComponentEnabled(boolean enabled)
	{
		int newState;

		if (enabled)
		{
			newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
			Log.d(LOG_TAG, "Enable ReceiveTextActivity component");
		}
		else
		{
			newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
			Log.d(LOG_TAG, "Disable ReceiveTextActivity component");
		}

		Activity activity = getActivity();
		PackageManager packageManager = activity.getPackageManager();
		ComponentName componentName = new ComponentName(activity.getApplicationContext(), ReceiveTextActivity.class);
		packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP); // dont stop
	}
}
