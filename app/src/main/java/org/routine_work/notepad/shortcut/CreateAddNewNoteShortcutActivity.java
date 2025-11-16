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
package org.routine_work.notepad.shortcut;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import org.routine_work.notepad.AddNewNoteActivity;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

public class CreateAddNewNoteShortcutActivity extends Activity
{

	private static final String LOG_TAG = "simple-notepad";

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreate(savedInstanceState);
		createAddNewNoteShortcut();

		Log.v(LOG_TAG, "Bye");
	}

	private void createAddNewNoteShortcut()
	{
		// Android 8.0 (APIレベル 26) 以降のショートカット作成方法
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

			if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported())
			{
				String shortcutName = getString(R.string.add_new_note_title);
				Intent addNewNoteIntent = new Intent(this, AddNewNoteActivity.class);
				addNewNoteIntent.setAction(Intent.ACTION_MAIN);

				String shortcutId = "add_new_note_" + System.currentTimeMillis();
				ShortcutInfo.Builder builder = new ShortcutInfo.Builder(this, shortcutId);
				builder.setShortLabel(shortcutName);
				builder.setLongLabel(shortcutName);
				builder.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_notepad_add));
				builder.setIntent(addNewNoteIntent);
				ShortcutInfo shortcutInfo = builder.build();

				shortcutManager.requestPinShortcut(shortcutInfo, null);

				setResult(Activity.RESULT_OK);
				finish();
			}
			else
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		}
		else
		{
			String shortcutName = getString(R.string.add_new_note_title);
			Intent addNewNoteIntent = new Intent(this, AddNewNoteActivity.class);
			Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_notepad_add);

			Intent resultIntent = new Intent();
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, addNewNoteIntent);
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);

			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
	}
}
