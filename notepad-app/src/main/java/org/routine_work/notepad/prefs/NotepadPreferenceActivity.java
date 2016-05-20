/*
 *  The MIT License
 *
 *  Copyright 2011-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.routine_work.notepad.prefs;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.ReceiveTextActivity;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
public class NotepadPreferenceActivity extends PreferenceActivity
	implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private static final String LOG_TAG = "simple-notepad";
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

//		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notepad_preference);

		sharedPreferences = getPreferenceManager().getSharedPreferences();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		updateSummary();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.quit_menuitem:
				NotepadActivity.quitApplication(this);
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		Log.v(LOG_TAG, "Hello");
		Log.i(LOG_TAG, "shared preference " + key + " is changed.");

		String receiveTextKey = getString(R.string.receive_text_key);
		if (key.equals(receiveTextKey))
		{
			// update component enable/disable
			boolean defaultValue = getResources().getBoolean(R.bool.receive_text_default_value);
			boolean receiveTextEnabled = prefs.getBoolean(key, defaultValue);
			int newState;

			if (receiveTextEnabled)
			{
				newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
				Log.d(LOG_TAG, "Enable ReceiveTextActivity component");
			}
			else
			{
				newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
				Log.d(LOG_TAG, "Disable ReceiveTextActivity component");
			}

			PackageManager packageManager = getPackageManager();
			ComponentName componentName = new ComponentName(this, ReceiveTextActivity.class);
			packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP); // dont stop
		}
		else
		{
			updateSummary();
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void updateSummary()
	{
		String prefKey;
		String prefValueString;
		CharSequence summary;
		ListPreference listPreference;

		// Theme 
		prefKey = getString(R.string.notepad_theme_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		summary = listPreference.getEntry();
		listPreference.setSummary(summary);

		// Text Lines in Portrait
		prefKey = getString(R.string.note_list_item_content_lines_port_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		prefValueString = listPreference.getEntry().toString();
		summary = getString(R.string.note_list_item_content_lines_summary, prefValueString);
		listPreference.setSummary(summary);

		// Text Lines in Landscape
		prefKey = getString(R.string.note_list_item_content_lines_land_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		prefValueString = listPreference.getEntry().toString();
		summary = getString(R.string.note_list_item_content_lines_summary, prefValueString);
		listPreference.setSummary(summary);

		// Sort Order
		prefKey = getString(R.string.note_list_sort_order_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		summary = listPreference.getEntry();
		listPreference.setSummary(summary);

		// Note Detail : Font Size in Portrait
		prefKey = getString(R.string.note_detail_font_size_port_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		if (listPreference != null)
		{
			summary = listPreference.getEntry();
			listPreference.setSummary(summary);
		}

		// Note Detail : Font Size in Landscape
		prefKey = getString(R.string.note_detail_font_size_land_key);
		listPreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
		if (listPreference != null)
		{
			summary = listPreference.getEntry();
			listPreference.setSummary(summary);
		}
	}
}
