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
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class CreateNoteShortcutActivity extends Activity
		implements View.OnClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
	}

	private Uri noteUri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_note_shortcut_activity);
		setTitle(R.string.new_note_shortcut_title);

		// init views
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		Button okButton = (Button) findViewById(R.id.ok_button);
		cancelButton.setOnClickListener(this);
		okButton.setOnClickListener(this);

		// pick note
		startPickNoteActivity();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onClick(View view)
	{
		Log.v(LOG_TAG, "Hello");

		int viewId = view.getId();
		if (viewId == R.id.cancel_button)
		{
			Log.d(LOG_TAG, "Cancel Button is clicked.");
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		else if (viewId == R.id.ok_button)
		{
			Log.d(LOG_TAG, "OK Button is clicked.");
			createShortcut();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(LOG_TAG, "Hello");

		if (requestCode == REQUEST_CODE_PICK_NOTE)
		{
			if (resultCode == RESULT_OK)
			{
				noteUri = data.getData();
				loadShortcutName();
			}
			else
			{
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void loadShortcutName()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "noteUri => " + noteUri);
		if (noteUri != null)
		{
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(noteUri, null, null, null, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
					String noteTitle = cursor.getString(titleIndex);
					Log.d(LOG_TAG, "noteTitle => " + noteTitle);

					if (noteTitle != null)
					{
						EditText shortcutNameEditText = (EditText) findViewById(R.id.shortcut_name_edittext);
						shortcutNameEditText.setText(noteTitle);
					}
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startPickNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(NoteStore.Note.NOTE_ITEM_CONTENT_TYPE);
		startActivityForResult(intent, REQUEST_CODE_PICK_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void createShortcut()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteUri != null)
		{
			// Check shortcut name
			EditText shortcutNameEditText = (EditText) findViewById(R.id.shortcut_name_edittext);
			String shortcutName = shortcutNameEditText.getText().toString();
			if (TextUtils.isEmpty(shortcutName))
			{
				Toast.makeText(this, R.string.no_shortcut_name_message, Toast.LENGTH_LONG).show();
				return;
			}

			// Create shortcut
			Intent editNoteIntent = new Intent(Intent.ACTION_EDIT, noteUri);

			ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_notepad_edit);

			Intent resultIntent = new Intent();
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, editNoteIntent);
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
			resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}

		Log.v(LOG_TAG, "Bye");
	}
}
