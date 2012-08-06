/*
 * The MIT License
 *
 * Copyright 2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NotepadPreferenceUtils
{

	private static final String LOG_TAG = "simple-notepad";

	public static int getTheme(Context context)
	{
		int themeId = R.style.Theme_Notepad_Black;

		String preferenceName = context.getPackageName() + "_preferences";
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		String key = context.getString(R.string.notepad_theme_key);
		String defaultValue = context.getString(R.string.notepad_theme_default_value);
		String themeValue = sharedPreferences.getString(key, defaultValue);

		final String themeBlack = context.getString(R.string.notepad_theme_black_value);
		final String themeLight = context.getString(R.string.notepad_theme_light_value);
		if (themeBlack.equals(themeValue))
		{
			themeId = R.style.Theme_Notepad_Black;
		}
		else if (themeLight.equals(themeValue))
		{
			themeId = R.style.Theme_Notepad_Light;
		}

		return themeId;
	}

	public static void reset(Context context)
	{
		Log.v(LOG_TAG, "Hello");

		String preferenceName = context.getPackageName() + "_preferences";
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.clear();
		edit.commit();

		Log.v(LOG_TAG, "Bye");
	}
}
