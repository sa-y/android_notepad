/*
 * The MIT License
 *
 * Copyright 2011-2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
package org.routine_work.notepad.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
public class NoteStore
{

	private static final String LOG_TAG = "simple-notepad";
	public static final String AUTHORITY = "org.routine_work.notepad.noteprovider";
	public static final String PARAM_KEY_QUERY = "q";

	public interface Note
	{

		public interface Columns extends BaseColumns
		{

			String DATE_ADDED = "date_added";
			String DATE_MODIFIED = "date_modified";
			String TITLE = "title";
			String CONTENT = "content";
			String TITLE_LOCKED = "title_locked";
			String CONTENT_LOCKED = "content_locked";
		}
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");
		public static final String NOTE_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.routine_work.note";
		public static final String NOTE_ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd.routine_work.note";
	}

	public interface NoteTemplate
	{

		public interface Columns extends BaseColumns
		{

			String NAME = "name";
			String TITLE = "title";
			String CONTENT = "content";
			String TITLE_LOCKED = "title_locked";
			String CONTENT_LOCKED = "content_locked";
		}
		Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notetemplates");
		String NOTE_TEMPLATE_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.routine_work.notetemplate";
		String NOTE_TEMPLATE_ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd.routine_work.notetemplate";
	}

	public static boolean checkNote(ContentResolver cr, Uri uri)
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		if (uri != null)
		{
			Cursor cursor = cr.query(uri, null, null, null, null);
			if (cursor != null)
			{
				try
				{
					if (cursor.moveToFirst())
					{
						result = true;
					}
				}
				finally
				{
					cursor.close();
				}
			}
		}

		Log.v(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	public static Uri insertNote(ContentResolver cr, String title, String content)
	{
		Log.v(LOG_TAG, "Hello");

		long now = System.currentTimeMillis();
		ContentValues initialValues = new ContentValues();
		initialValues.put(Note.Columns.TITLE, title);
		initialValues.put(Note.Columns.CONTENT, content);
		initialValues.put(Note.Columns.TITLE_LOCKED, false);
		initialValues.put(Note.Columns.CONTENT_LOCKED, false);
		initialValues.put(Note.Columns.DATE_ADDED, now);
		initialValues.put(Note.Columns.DATE_MODIFIED, now);

		Uri newUri = cr.insert(Note.CONTENT_URI, initialValues);
		Log.d(LOG_TAG, "newUri => " + newUri);

		Log.v(LOG_TAG, "Bye");
		return newUri;
	}

	public static int updateNote(ContentResolver cr, Uri uri, String title, String content)
	{
		Log.v(LOG_TAG, "Hello");

		long now = System.currentTimeMillis();
		ContentValues values = new ContentValues();
		values.put(Note.Columns.TITLE, title);
		values.put(Note.Columns.CONTENT, content);
		values.put(Note.Columns.DATE_MODIFIED, now);

		int updatedCount = cr.update(uri, values, null, null);
		Log.d(LOG_TAG, "updatedCount => " + updatedCount);

		Log.v(LOG_TAG, "Bye");
		return updatedCount;
	}

	/**
	 * Get the number of notes in NoteProvider.
	 *
	 * @param cr ContentResolver
	 * @return the number of notes in NoteProvider.
	 */
	public static int getNoteCount(ContentResolver cr)
	{
		int noteCount = 0;
		Log.v(LOG_TAG, "Hello");

		String[] projection = new String[]
		{
			"count(*) AS " + Note.Columns._COUNT,
		};
		Cursor cursor = cr.query(Note.CONTENT_URI, projection, null, null, null);
		try
		{
			if (cursor.moveToFirst())
			{
				int index = cursor.getColumnIndex(Note.Columns._COUNT);
				noteCount = cursor.getInt(index);
			}
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}

		Log.v(LOG_TAG, "noteCount => " + noteCount);
		Log.v(LOG_TAG, "Bye");
		return noteCount;
	}

	/**
	 * Get the number of notet emplates in NoteProvider.
	 *
	 * @param cr ContentResolver
	 * @return the number of note templates in NoteProvider.
	 */
	public static int getNoteTemplateCount(ContentResolver cr)
	{
		int noteTemplateCount = 0;
		Log.v(LOG_TAG, "Hello");

		String[] projection = new String[]
		{
			"count(*) AS " + NoteTemplate.Columns._COUNT,
		};
		Cursor cursor = cr.query(NoteTemplate.CONTENT_URI, projection, null, null, null);
		try
		{
			if (cursor.moveToFirst())
			{
				int index = cursor.getColumnIndex(NoteTemplate.Columns._COUNT);
				noteTemplateCount = cursor.getInt(index);
			}
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}

		Log.v(LOG_TAG, "noteTemplateCount => " + noteTemplateCount);
		Log.v(LOG_TAG, "Bye");
		return noteTemplateCount;
	}

	public static void reindex(Context context)
	{
		NoteDBHelper helper = new NoteDBHelper(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		if (writableDatabase != null)
		{
			try
			{
				helper.reindex(writableDatabase);
			}
			finally
			{
				writableDatabase.close();
			}
		}
	}

	public static void vacuum(Context context)
	{
		NoteDBHelper helper = new NoteDBHelper(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		if (writableDatabase != null)
		{
			try
			{
				helper.vacuum(writableDatabase);
			}
			finally
			{
				writableDatabase.close();
			}
		}
	}
}
