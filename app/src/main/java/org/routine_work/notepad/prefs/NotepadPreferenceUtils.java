/*
 * The MIT License
 *
 * Copyright 2012-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NotepadPreferenceUtils
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String TEMPLATE_DATA_INITIALIZED_KEY = "TEMPLATE_DATA_INITIALIZED";

	public static SharedPreferences getSharedPreferences(Context context)
	{
		String preferenceName = context.getPackageName() + "_preferences";
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	public static int getTheme(Context context)
	{
		int themeId = R.style.Theme_Notepad_Dark;

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		String key = context.getString(R.string.notepad_theme_key);
		String defaultValue = context.getString(R.string.notepad_theme_default_value);
		String themeValue = sharedPreferences.getString(key, defaultValue);

		final String themeDark = context.getString(R.string.notepad_theme_dark_value);
		final String themeLight = context.getString(R.string.notepad_theme_light_value);
		if (themeDark.equals(themeValue))
		{
			themeId = R.style.Theme_Notepad_Dark;
		}
		else if (themeLight.equals(themeValue))
		{
			themeId = R.style.Theme_Notepad_Light;
		}

		return themeId;
	}

	public static String getNoteListLayout(Context context)
	{
		String noteListLayout;
		Log.v(LOG_TAG, "Hello");

		int keyId;
		int defaultValueId;
		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				keyId = R.string.note_list_layout_land_key;
				defaultValueId = R.string.note_list_layout_land_default_value;
				break;
			default:
				keyId = R.string.note_list_layout_port_key;
				defaultValueId = R.string.note_list_layout_port_default_value;
				break;
		}

		SharedPreferences sharedPreferences = getSharedPreferences(context);

		String key = resources.getString(keyId);
		String defaultValue = resources.getString(defaultValueId);
		noteListLayout = sharedPreferences.getString(key, defaultValue);

		Log.v(LOG_TAG, "noteListLayout => " + noteListLayout);
		Log.v(LOG_TAG, "Bye");
		return noteListLayout;
	}

	public static boolean isNoteListItemModifiedTimeVisible(Context context)
	{
		boolean noteListItemModifiedTimeVisible;
		int keyId;
		int defaultValueId;
		Log.v(LOG_TAG, "Hello");

		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				keyId = R.string.note_list_item_modified_time_visibility_land_key;
				defaultValueId = R.bool.note_list_item_modified_time_visibility_land_default_value;
				break;
			default:
				keyId = R.string.note_list_item_modified_time_visibility_port_key;
				defaultValueId = R.bool.note_list_item_modified_time_visibility_port_default_value;
				break;
		}

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		String key = resources.getString(keyId);
		Log.d(LOG_TAG, "key => " + key);
		boolean defaultValue = resources.getBoolean(defaultValueId);
		noteListItemModifiedTimeVisible = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "noteListItemModifiedTimeVisible => " + noteListItemModifiedTimeVisible);
		Log.v(LOG_TAG, "Bye");
		return noteListItemModifiedTimeVisible;
	}

	public static int getNoteListItemContentLines(Context context)
	{
		int noteListItemContentLines = 1;
		int keyId;
		int defaultValueId;
		Log.v(LOG_TAG, "Hello");

		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				keyId = R.string.note_list_item_content_lines_land_key;
				defaultValueId = R.string.note_list_item_content_lines_land_default_value;
				break;
			default:
				keyId = R.string.note_list_item_content_lines_port_key;
				defaultValueId = R.string.note_list_item_content_lines_port_default_value;
				break;
		}

		SharedPreferences sharedPreferences = getSharedPreferences(context);

		String key = resources.getString(keyId);
		String defaultValue = resources.getString(defaultValueId);
		String value = sharedPreferences.getString(key, defaultValue);
		Log.d(LOG_TAG, "value => " + value);
		try
		{
			noteListItemContentLines = Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			Log.e(LOG_TAG, "Integer.parseInt(" + value + ") is failed.");
		}

		Log.d(LOG_TAG, "noteListItemContentLines => " + noteListItemContentLines);
		Log.v(LOG_TAG, "Bye");
		return noteListItemContentLines;
	}

	public static String getNoteListSortOrder(Context context)
	{
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		String key = context.getString(R.string.note_list_sort_order_key);
		String defaultValue = context.getString(R.string.note_list_sort_order_default_value);
		String noteListSortOrder = sharedPreferences.getString(key, defaultValue);

		Log.d(LOG_TAG, "noteListSortOrder => " + noteListSortOrder);
		Log.v(LOG_TAG, "Bye");
		return noteListSortOrder;
	}

	public static boolean getActionBarAutoHide(Context context)
	{
		boolean actionBarAutoHide;
		int keyId;
		int defaultValueId;
		Log.v(LOG_TAG, "Hello");

		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				keyId = R.string.actionbar_auto_hide_land_key;
				defaultValueId = R.bool.actionbar_auto_hide_land_default_value;
				break;
			default:
				keyId = R.string.actionbar_auto_hide_port_key;
				defaultValueId = R.bool.actionbar_auto_hide_port_default_value;
				break;
		}

		SharedPreferences sharedPreferences = getSharedPreferences(context);

		String key = resources.getString(keyId);
		boolean defaultValue = resources.getBoolean(defaultValueId);
		actionBarAutoHide = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "actionBarAutoHide => " + actionBarAutoHide);
		Log.v(LOG_TAG, "Bye");
		return actionBarAutoHide;
	}

	public static boolean getNoteTitleAutoLink(Context context)
	{
		boolean noteTitleAutoLink;
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Resources resources = context.getResources();
		String key = resources.getString(R.string.note_title_autolink_key);
		boolean defaultValue = resources.getBoolean(R.bool.note_title_autolink_default_value);
		noteTitleAutoLink = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "noteTitleAutoLink => " + noteTitleAutoLink);
		Log.v(LOG_TAG, "Bye");
		return noteTitleAutoLink;
	}

	public static boolean getNoteContentAutoLink(Context context)
	{
		boolean noteContentAutoLink;
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Resources resources = context.getResources();
		String key = resources.getString(R.string.note_content_autolink_key);
		boolean defaultValue = resources.getBoolean(R.bool.note_content_autolink_default_value);
		noteContentAutoLink = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "noteContentAutoLink => " + noteContentAutoLink);
		Log.v(LOG_TAG, "Bye");
		return noteContentAutoLink;
	}

	public static int getNoteDetailFontSize(Context context)
	{
		int fontSize = 18;
		int keyId;
		int defaultValueId;
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				keyId = R.string.note_detail_font_size_land_key;
				defaultValueId = R.string.note_detail_font_size_land_default_value;
				break;
			default:
				keyId = R.string.note_detail_font_size_port_key;
				defaultValueId = R.string.note_detail_font_size_port_default_value;
				break;
		}

		String key = resources.getString(keyId);
		String defaultValue = resources.getString(defaultValueId);
		String value = sharedPreferences.getString(key, defaultValue);
		Log.d(LOG_TAG, "value => " + value);
		try
		{
			fontSize = Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			Log.e(LOG_TAG, "Integer.parseInt(" + value + ") is failed.");
		}

		Log.d(LOG_TAG, "fontSize => " + fontSize);
		Log.v(LOG_TAG, "Bye");
		return fontSize;
	}

	public static int getNoteDetailFontSizeDefault(Context context)
	{
		int noteDefailFontSizeDefault = 18;
		int defaultValueId;
		Log.v(LOG_TAG, "Hello");

		Resources resources = context.getResources();

		Configuration configuration = resources.getConfiguration();
		switch (configuration.orientation)
		{
			case Configuration.ORIENTATION_LANDSCAPE:
				defaultValueId = R.string.note_detail_font_size_land_default_value;
				break;
			default:
				defaultValueId = R.string.note_detail_font_size_port_default_value;
				break;
		}

		String defaultValue = resources.getString(defaultValueId);
		Log.d(LOG_TAG, "defaultValue => " + defaultValue);
		try
		{
			noteDefailFontSizeDefault = Integer.parseInt(defaultValue);
		}
		catch (NumberFormatException e)
		{
			Log.e(LOG_TAG, "Integer.parseInt(" + defaultValue + ") is failed.");
		}

		Log.d(LOG_TAG, "fontSize => " + noteDefailFontSizeDefault);
		Log.v(LOG_TAG, "Bye");
		return noteDefailFontSizeDefault;
	}

	public static boolean getNoteTitleCapitalization(Context context)
	{
		boolean noteTitleCapitalization;
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Resources resources = context.getResources();
		String key = resources.getString(R.string.capitalization_title_key);
		boolean defaultValue = resources.getBoolean(R.bool.capitalization_title_default_value);
		noteTitleCapitalization = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "noteTitleCapitalization => " + noteTitleCapitalization);
		Log.v(LOG_TAG, "Bye");
		return noteTitleCapitalization;
	}

	public static boolean getNoteContentCapitalization(Context context)
	{
		boolean noteContentCapitalization;
		Log.v(LOG_TAG, "Hello");

		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Resources resources = context.getResources();
		String key = resources.getString(R.string.capitalization_content_key);
		boolean defaultValue = resources.getBoolean(R.bool.capitalization_content_default_value);
		noteContentCapitalization = sharedPreferences.getBoolean(key, defaultValue);

		Log.d(LOG_TAG, "noteContentCapitalization => " + noteContentCapitalization);
		Log.v(LOG_TAG, "Bye");
		return noteContentCapitalization;
	}

	public static int incrementQuitCount(Context context)
	{
		final String QUIT_COUNT_KEY = "QUIT_COUNT";
		int quitCount;
		Log.v(LOG_TAG, "Hello");

		String preferenceName = context.getPackageName();
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

		// read
		quitCount = sharedPreferences.getInt(QUIT_COUNT_KEY, 0);

		// increment
		quitCount++;

		// update
		Editor edit = sharedPreferences.edit();
		edit.putInt(QUIT_COUNT_KEY, quitCount);
		edit.commit();

		Log.d(LOG_TAG, "quitCount => " + quitCount);
		Log.v(LOG_TAG, "Bye");
		return quitCount;
	}

	public static boolean isTemplateDataInitialized(Context context)
	{
		boolean initialized;
		Log.v(LOG_TAG, "Hello");

		String preferenceName = context.getPackageName();
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

		initialized = sharedPreferences.getBoolean(TEMPLATE_DATA_INITIALIZED_KEY, false);

		Log.d(LOG_TAG, "initialized => " + initialized);
		Log.v(LOG_TAG, "Bye");
		return initialized;
	}

	public static void setTemplateDataInitialized(Context context, boolean initialized)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "initialized => " + initialized);

		String preferenceName = context.getPackageName();
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

		Editor edit = sharedPreferences.edit();
		edit.putBoolean(TEMPLATE_DATA_INITIALIZED_KEY, initialized);
		edit.commit();

		Log.v(LOG_TAG, "Bye");
	}

	public static void reset(Context context)
	{
		String preferenceName = context.getPackageName() + "_preferences";
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.clear();
		edit.commit();
	}
}
