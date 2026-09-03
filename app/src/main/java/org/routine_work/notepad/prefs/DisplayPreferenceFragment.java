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

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

public class DisplayPreferenceFragment extends PreferenceFragmentCompat
		implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private static final String LOG_TAG = "simple-notepad";

	private SharedPreferences sharedPreferences;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		setPreferencesFromResource(R.xml.notepad_preference_display, rootKey); // 対応するXMLをロード
		sharedPreferences = getPreferenceManager().getSharedPreferences();
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
		updateSummary();

		Log.v(LOG_TAG, "Bye");
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		Log.v(LOG_TAG, "Hello");
		Log.i(LOG_TAG, "shared preference " + key + " is changed.");

		updateSummary();

		Log.v(LOG_TAG, "Bye");
	}

	private void updateSummary()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "isAdded() => " + isAdded());
		Log.v(LOG_TAG, "isVisible() => " + isVisible());

		if (isAdded())
		{
			// Theme
			updateListPreferenceSummary(R.string.notepad_theme_key);

			// Layout
			final String noteListLayoutPortKey = getString(R.string.note_list_layout_port_key);
			final String noteListLayoutLandKey = getString(R.string.note_list_layout_land_key);
			CharSequence summary;

			ListPreference noteListLayoutPortPreference = findPreference(noteListLayoutPortKey);
			Log.v(LOG_TAG, "noteListLayoutPortPreference => " + noteListLayoutPortPreference);
			if (noteListLayoutPortPreference != null)
			{
				final String noteListLayoutPortDefaultValue = getString(R.string.note_list_layout_port_default_value);
				String noteListLayoutPortValue = sharedPreferences.getString(noteListLayoutPortKey, noteListLayoutPortDefaultValue);
				summary = getLayoutName(noteListLayoutPortValue);
				noteListLayoutPortPreference.setSummary(summary);
			}

			ListPreference noteListLayoutLandPreference = findPreference(noteListLayoutLandKey);
			if (noteListLayoutLandPreference != null)
			{
				final String noteListLayoutLandDefaultValue = getString(R.string.note_list_layout_land_default_value);
				String noteListLayoutLandValue = sharedPreferences.getString(noteListLayoutLandKey, noteListLayoutLandDefaultValue);
				summary = getLayoutName(noteListLayoutLandValue);
				noteListLayoutLandPreference.setSummary(summary);
			}

			// Text Lines in Portrait
			updateListPreferenceSummaryWithFormat(R.string.note_list_item_content_lines_port_key, R.string.note_list_item_content_lines_summary);

			// Text Lines in Landscape
			updateListPreferenceSummaryWithFormat(R.string.note_list_item_content_lines_land_key, R.string.note_list_item_content_lines_summary);

			// Sort Order
			updateListPreferenceSummary(R.string.note_list_sort_order_key);

			// Font Size in Portrait
			updateListPreferenceSummary(R.string.note_detail_font_size_port_key);

			// Font Size in Landscape
			updateListPreferenceSummary(R.string.note_detail_font_size_land_key);
		}
		Log.v(LOG_TAG, "Hello");
	}

	/**
	 * ListPreferenceのサマリーをEntryの値で更新するヘルパーメソッド
	 * A helper method to update the summary of a ListPreference using a entry value.
	 *
	 * @param keyResId Preferenceのキーの文字列リソースID
	 */
	private void updateListPreferenceSummary(int keyResId)
	{
		String prefKey = getString(keyResId);
		ListPreference preference = findPreference(prefKey);
		if (preference != null)
		{
			preference.setSummary(preference.getEntry());
		}
	}

	/**
	 * A helper method to update the summary of a ListPreference using a format string.
	 *
	 * @param keyResId           Preference Key Resource ID
	 * @param summaryFormatResId Summary Format Resource ID
	 */
	private void updateListPreferenceSummaryWithFormat(int keyResId, int summaryFormatResId)
	{
		String prefKey = getString(keyResId);
		ListPreference preference = findPreference(prefKey);
		if (preference != null)
		{
			String entry = preference.getEntry().toString();
			String summary = getString(summaryFormatResId, entry);
			preference.setSummary(summary);
		}
	}

	private String getLayoutName(String layoutValue)
	{
		final String noteListLayoutSingle = getString(R.string.note_list_layout_single_value);
		final String noteListLayoutWideTwo = getString(R.string.note_list_layout_wide_two_value);
		String name = null;

		if (noteListLayoutSingle.equals(layoutValue))
		{
			name = getString(R.string.single_pane);
		}
		else if (noteListLayoutWideTwo.equals(layoutValue))
		{
			name = getString(R.string.wide_two_pane);
		}

		return name;
	}
}
