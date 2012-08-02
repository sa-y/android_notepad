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
package org.routine_work.notepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NotepadPreferenceUtils
{
	private static final String LOG_TAG = "simple-notepad";

	public static String getNoteListLayout(Context context)
	{
		String preferenceName = context.getPackageName() + "_preferences";
		SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		Resources resources = context.getResources();

		String key = resources.getString(R.string.note_list_layout_key);
		String value = sharedPreferences.getString(key, null);

		if (value == null)
		{
			// Get Default value
			Configuration configuration = resources.getConfiguration();
			int screenSize = configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
			switch (screenSize)
			{
				case Configuration.SCREENLAYOUT_SIZE_SMALL:
				case Configuration.SCREENLAYOUT_SIZE_NORMAL:
					value = resources.getString(R.string.note_list_layout_single_value);
					break;
				default:
					value = resources.getString(R.string.note_list_layout_wide_two_value);
			}

			// Write Default Value
			Editor edit = sharedPreferences.edit();
			edit.putString(key, value);
			edit.commit();
		}


		Log.v(LOG_TAG, "noteListLayout => " + value);
		return value;
	}
}
