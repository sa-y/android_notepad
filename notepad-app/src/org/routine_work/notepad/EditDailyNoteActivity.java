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
import android.text.format.DateFormat;
import java.util.Date;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 * Edit daily note.
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class EditDailyNoteActivity extends Activity
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(LOG_TAG, "Hello");

		java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
		String dateString = dateFormat.format(new Date());
		Log.d(LOG_TAG, "dateString => " + dateString);

		Uri noteUri = searchNoteByTitle(dateString);
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		Intent intent;
		if (noteUri != null)
		{
			intent = new Intent(Intent.ACTION_EDIT, noteUri);
		}
		else
		{
			intent = new Intent(Intent.ACTION_INSERT, NoteStore.CONTENT_URI);
			intent.putExtra(Intent.EXTRA_TITLE, dateString);
		}
		Log.d(LOG_TAG, "intent => " + intent);
		startActivity(intent);
		finish();

		Log.v(LOG_TAG, "Bye");
	}

	private Uri searchNoteByTitle(String title)
	{
		Uri result = null;

		ContentResolver contentResolver = getContentResolver();
		String selection = NoteStore.NoteColumns.TITLE + " = ? ";
		String[] selectionArgs = new String[]
		{
			title
		};
		Cursor cursor = contentResolver.query(NoteStore.CONTENT_URI, null,
			selection, selectionArgs, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idColumnIndex = cursor.getColumnIndex(NoteStore.NoteColumns._ID);
			long noteId = cursor.getLong(idColumnIndex);
			result = ContentUris.withAppendedId(NoteStore.CONTENT_URI, noteId);
		}

		return result;
	}
}
