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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.routine_work.notepad.provider.NoteStore.NoteColumns;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
class NoteDBHelper extends SQLiteOpenHelper
	implements NoteDBConstants
{

	private String LOG_TAG = "simple-notepad";
	private Context context;

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
		Log.setIndentMode(true);
	}

	NoteDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.w(LOG_TAG, "Create database.");
		Log.w(LOG_TAG, "Notes.CREATE_TABLE_SQL => " + Notes.CREATE_TABLE_SQL);
		db.execSQL(Notes.CREATE_TABLE_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_TITLE_INDEX_SQL => " + Notes.CREATE_TITLE_INDEX_SQL);
		db.execSQL(Notes.CREATE_TITLE_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_CONTENT_INDEX_SQL => " + Notes.CREATE_CONTENT_INDEX_SQL);
		db.execSQL(Notes.CREATE_CONTENT_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_DATE_ADDED_INDEX_SQL => " + Notes.CREATE_DATE_ADDED_INDEX_SQL);
		db.execSQL(Notes.CREATE_DATE_ADDED_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_DATE_MODIFIED_INDEX_SQL => " + Notes.CREATE_DATE_MODIFIED_INDEX_SQL);
		db.execSQL(Notes.CREATE_DATE_MODIFIED_INDEX_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
			+ newVersion + ".");

		// backup data
		File backupDir = context.getDir("note_db_backup", Context.MODE_PRIVATE);
		if (oldVersion == 4)
		{
			backupV4To(db, backupDir);
		}
		// upgrade table
		db.execSQL(Notes.DROP_TABLE_SQL);

		// restore data
		onCreate(db);

		restoreFrom(db, backupDir);
	}

	public void reindex(SQLiteDatabase db)
	{
		db.execSQL(Notes.REINDEX_SQL);
	}

	public void vacuum(SQLiteDatabase db)
	{
		db.execSQL(Notes.VACCUM_SQL);
	}

	public void backupV4To(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(Notes.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteColumns._ID);
			int titleIndex = cursor.getColumnIndex(NoteColumns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteColumns.CONTENT);
			int dateAddedIndex = cursor.getColumnIndex(NoteColumns.DATE_ADDED);
			int dateModifiedIndex = cursor.getColumnIndex(NoteColumns.DATE_MODIFIED);

			int index = 0;
			do
			{
				Note note = new Note();
				note.id = cursor.getLong(idIndex);
				note.title = cursor.getString(titleIndex);
				note.content = cursor.getString(contentIndex);
				note.titleLocked = false;
				note.contentLocked = false;
				note.added = cursor.getLong(dateAddedIndex);
				note.modified = cursor.getLong(dateModifiedIndex);

				File noteFile = new File(backupDirectory, String.format("%08d", index++));
				Log.d(LOG_TAG, "backup : noteFile => " + noteFile);
				try
				{
					Note.writeNoteTo(note, noteFile);
				}
				catch (FileNotFoundException ex)
				{
					Log.e(LOG_TAG, "Note.writeNoteTo() Failed.", ex);
				}
				catch (IOException ex)
				{
					Log.e(LOG_TAG, "Note.writeNoteTo() Failed.", ex);
				}
			}
			while (cursor.moveToNext());

		}

		Log.v(LOG_TAG, "Bye");
	}

	public void restoreFrom(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);
		File[] listFiles = backupDirectory.listFiles();

		for (File noteFile : listFiles)
		{
			try
			{
				Log.d(LOG_TAG, "restore : noteFile => " + noteFile);
				Note note = Note.readNoteFrom(noteFile);
				ContentValues values = new ContentValues();
				values.put(NoteColumns._ID, note.id);
				values.put(NoteColumns.TITLE, note.title);
				values.put(NoteColumns.CONTENT, note.content);
				values.put(NoteColumns.TITLE_LOCKED, note.titleLocked);
				values.put(NoteColumns.CONTENT_LOCKED, note.contentLocked);
				values.put(NoteColumns.DATE_ADDED, note.added);
				values.put(NoteColumns.DATE_MODIFIED, note.modified);
				db.insert(Notes.TABLE_NAME, null, values);
			}
			catch (FileNotFoundException ex)
			{
				Log.e(LOG_TAG, "Note.readNoteFrom() Failed.", ex);
			}
			catch (IOException ex)
			{
				Log.e(LOG_TAG, "Note.readNoteFrom() Failed.", ex);
			}
			catch (ClassNotFoundException ex)
			{
				Log.e(LOG_TAG, "Note.readNoteFrom() Failed.", ex);
			}
		}


		Log.v(LOG_TAG, "Bye");
	}

	public void clearBackupFiles(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");

		File[] listFiles = backupDirectory.listFiles();
		for (File noteFile : listFiles)
		{
			Log.d(LOG_TAG, "Delete : noteFile => " + noteFile);
			noteFile.delete();
		}

		Log.v(LOG_TAG, "Bye");
	}
}