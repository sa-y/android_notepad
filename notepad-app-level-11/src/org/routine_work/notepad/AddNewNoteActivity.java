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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.util.Date;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AddNewNoteActivity extends Activity implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_note_activity);

		// initialize noteTemplateUri
		Uri noteTemplateUri = null;
		Intent intent = getIntent();
		if (Intent.ACTION_INSERT.equals(intent.getAction()))
		{
			noteTemplateUri = intent.getData();
		}
		Log.d(LOG_TAG, "noteTemplateUri => " + noteTemplateUri);

		// create note or select template
		if (noteTemplateUri != null)
		{
			startNoteDetailActivityWithTemplate(noteTemplateUri);
			finish();
		}
		else
		{
			int noteTemplateCount = NoteStore.getNoteTemplateCount(getContentResolver());
			if (noteTemplateCount >= 2)
			{
				Intent pickNoteTemplateIntent = new Intent(Intent.ACTION_PICK, NoteStore.NoteTemplate.CONTENT_URI);
				startActivityForResult(pickNoteTemplateIntent, REQUEST_CODE_PICK_NOTE_TEMPLATE);
			}
			else if (noteTemplateCount == 1)
			{
				startNoteDetailActivityWithTemplate(NoteStore.NoteTemplate.CONTENT_URI);
				finish();
			}
			else
			{
				startNoteDetailActivity();
				finish();
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE_PICK_NOTE_TEMPLATE)
		{
			if (resultCode == RESULT_OK)
			{
				Uri noteTemplateUri = data.getData();
				startNoteDetailActivityWithTemplate(noteTemplateUri);
			}
			setResult(resultCode);
			finish();
		}
	}

	private void startNoteDetailActivityWithTemplate(Uri noteTemplateUri)
	{
		Log.v(LOG_TAG, "Hello");
		Cursor cursor = getContentResolver().query(noteTemplateUri,
			null, null, null, null);
		try
		{
			if (cursor != null && cursor.moveToFirst())
			{
				int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
				int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
				int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);
				boolean titleLocked = (cursor.getInt(titleLockedIndex) != 0);

				Date now = new Date();
				String dateText = DateFormat.getDateFormat(this).format(now);
				String timeText = DateFormat.getTimeFormat(this).format(now);
				String titleTemplate = cursor.getString(titleIndex);
				String contentTemplate = cursor.getString(contentIndex);
				String title = String.format(titleTemplate, dateText, timeText);
				String content = String.format(contentTemplate, dateText, timeText);

				Uri noteUri = searchNoteByTitle(title);
				if (noteUri != null)
				{
					// if note is already exist
					Intent intent = new Intent(Intent.ACTION_EDIT, noteUri);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else
				{
					// if not found,  insert new note
					Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(Intent.EXTRA_TITLE, title);
					intent.putExtra(Intent.EXTRA_TEXT, content);
					intent.putExtra(EXTRA_TITLE_LOCKED, titleLocked);
					startActivity(intent);
				}
			}
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void startNoteDetailActivity()
	{
		Log.v(LOG_TAG, "Hello");
		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
		Log.v(LOG_TAG, "Bye");
	}

	private Uri searchNoteByTitle(String title)
	{
		Uri result = null;
		Log.v(LOG_TAG, "Hello");

		if (!TextUtils.isEmpty(title))
		{
			ContentResolver contentResolver = getContentResolver();
			String selection = NoteStore.Note.Columns.TITLE + " = ? ";
			String[] selectionArgs = new String[]
			{
				title
			};
			Cursor cursor = contentResolver.query(NoteStore.Note.CONTENT_URI, null,
				selection, selectionArgs, null);
			if (cursor != null)
			{
				try
				{
					if (cursor.moveToFirst())
					{
						int idColumnIndex = cursor.getColumnIndex(NoteStore.Note.Columns._ID);
						long noteId = cursor.getLong(idColumnIndex);
						result = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, noteId);
					}
				}
				finally
				{
					cursor.close();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}
}
