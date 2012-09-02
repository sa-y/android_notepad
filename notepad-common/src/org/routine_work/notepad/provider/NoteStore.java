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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Collection;
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
		}
		Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notetemplates");
		String NOTE_TEMPLATE_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.routine_work.notetemplate";
		String NOTE_TEMPLATE_ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd.routine_work.notetemplate";
	}

	public static boolean exist(ContentResolver cr, Uri uri)
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

	public static boolean isNoteItemUri(Context context, Uri uri)
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "uri => " + uri);

		if (uri != null)
		{
			ContentResolver contentResolver = context.getContentResolver();
			String type = contentResolver.getType(uri);
			Log.v(LOG_TAG, "uri.type => " + type);
			result = NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type);
		}

		Log.d(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
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

	public static int deleteNotes(ContentResolver cr, Collection<Long> idCollection)
	{
		int deletedCount = 0;
		Log.v(LOG_TAG, "Hello");

		if (idCollection != null && idCollection.size() > 0)
		{
			StringBuilder where = new StringBuilder();
			where.append(NoteStore.Note.Columns._ID);
			where.append(" in (");
			for (long id : idCollection)
			{
				where.append(id);
				where.append(", ");
			}
			where.delete(where.length() - 2, where.length());
			where.append(")");

			Log.d(LOG_TAG, "where => " + where);
			deletedCount = cr.delete(NoteStore.Note.CONTENT_URI, where.toString(), null);
		}

		Log.v(LOG_TAG, "Bye");
		return deletedCount;
	}

	public static int deleteNotes(ContentResolver cr, long[] ids)
	{
		int deletedCount = 0;
		Log.v(LOG_TAG, "Hello");

		if (ids != null && ids.length > 0)
		{
			StringBuilder where = new StringBuilder();
			where.append(NoteStore.Note.Columns._ID);
			where.append(" in (");
			for (int i = ids.length - 1; i >= 0; i--)
			{
				where.append(ids[i]);
				where.append(", ");
			}
			where.delete(where.length() - 2, where.length());
			where.append(")");

			Log.d(LOG_TAG, "where => " + where);
			deletedCount = cr.delete(NoteStore.Note.CONTENT_URI, where.toString(), null);
		}

		Log.v(LOG_TAG, "Bye");
		return deletedCount;
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
