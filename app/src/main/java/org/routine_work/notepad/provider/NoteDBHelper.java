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

import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.model.NoteTemplate;
import org.routine_work.utils.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sawai
 */
class NoteDBHelper extends SQLiteOpenHelper
		implements NoteDBConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String BACKUP_DIR_NOTES = "note_db_notes";
	private static final String BACKUP_DIR_NOTE_TEMPLATES = "note_db_notetemplates";
	private static final Map<Long, String> DEFAULT_NOTE_TEMPLATE_UUID_MAP = new HashMap<Long, String>();
	private Context context;

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
		Log.setIndentMode(true);
		// Fixed UUID for initial note template
		DEFAULT_NOTE_TEMPLATE_UUID_MAP.put(1L, "8547f52b-c15c-4a17-918a-a955607eaf64");
		DEFAULT_NOTE_TEMPLATE_UUID_MAP.put(2L, "f5b9524e-5f29-4b9b-80b0-a6ab2b1c3c4b");
		DEFAULT_NOTE_TEMPLATE_UUID_MAP.put(3L, "f0b82614-1426-4d97-b440-5b6bda615e48");
		DEFAULT_NOTE_TEMPLATE_UUID_MAP.put(4L, "938c8104-0ee8-459d-befe-565ffa8f1a8e");
		DEFAULT_NOTE_TEMPLATE_UUID_MAP.put(5L, "9e33835b-383a-4d14-a5d8-8536f784001f");
	}

	NoteDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.v(LOG_TAG, "Hello");
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ".");

		// backup data
		backupData(db, oldVersion);

		// upgrade table
		dropTables(db, oldVersion);
		createTables(db);

		// restore data
		restoreData(db);

		// clear backup data
		cleanupBackupData(db);
		Log.v(LOG_TAG, "bye");
	}

	public void reindex(SQLiteDatabase db)
	{
		db.execSQL(REINDEX_SQL);
	}

	public void vacuum(SQLiteDatabase db)
	{
		db.execSQL(VACCUM_SQL);
	}

	private void createTables(SQLiteDatabase db)
	{
		Log.v(LOG_TAG, "Hello");

		Log.w(LOG_TAG, "Create database.");
		// Notes Table
		Log.w(LOG_TAG, "Notes.CREATE_TABLE_SQL => " + Notes.CREATE_TABLE_SQL);
		db.execSQL(Notes.CREATE_TABLE_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_TITLE_INDEX_SQL => " + Notes.CREATE_TITLE_INDEX_SQL);
		db.execSQL(Notes.CREATE_TITLE_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_UUID_INDEX_SQL => " + Notes.CREATE_UUID_INDEX_SQL);
		db.execSQL(Notes.CREATE_UUID_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_ENABLED_INDEX_SQL => " + Notes.CREATE_ENABLED_INDEX_SQL);
		db.execSQL(Notes.CREATE_ENABLED_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_CONTENT_INDEX_SQL => " + Notes.CREATE_CONTENT_INDEX_SQL);
		db.execSQL(Notes.CREATE_CONTENT_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_DATE_ADDED_INDEX_SQL => " + Notes.CREATE_DATE_ADDED_INDEX_SQL);
		db.execSQL(Notes.CREATE_DATE_ADDED_INDEX_SQL);
		Log.w(LOG_TAG, "Notes.CREATE_DATE_MODIFIED_INDEX_SQL => " + Notes.CREATE_DATE_MODIFIED_INDEX_SQL);
		db.execSQL(Notes.CREATE_DATE_MODIFIED_INDEX_SQL);

		// Note Templates Table
		Log.w(LOG_TAG, "NoteTemplates.CREATE_TABLE_SQL => " + NoteTemplates.CREATE_TABLE_SQL);
		db.execSQL(NoteTemplates.CREATE_TABLE_SQL);
		Log.w(LOG_TAG, "NoteTemplates.CREATE_UUID_INDEX_SQL => " + NoteTemplates.CREATE_UUID_INDEX_SQL);
		db.execSQL(NoteTemplates.CREATE_UUID_INDEX_SQL);
		Log.w(LOG_TAG, "NoteTemplates.CREATE_NAME_INDEX_SQL => " + NoteTemplates.CREATE_NAME_INDEX_SQL);
		db.execSQL(NoteTemplates.CREATE_NAME_INDEX_SQL);
		Log.w(LOG_TAG, "NoteTemplates.CREATE_TITLE_INDEX_SQL => " + NoteTemplates.CREATE_TITLE_INDEX_SQL);
		db.execSQL(NoteTemplates.CREATE_TITLE_INDEX_SQL);
		Log.w(LOG_TAG, "NoteTemplates.CREATE_CONTENT_INDEX_SQL => " + NoteTemplates.CREATE_CONTENT_INDEX_SQL);
		db.execSQL(NoteTemplates.CREATE_CONTENT_INDEX_SQL);

		Log.v(LOG_TAG, "Bye");
	}

	private void dropTables(SQLiteDatabase db, int oldVersion)
	{
		Log.v(LOG_TAG, "Hello");

		db.execSQL(Notes.DROP_TABLE_SQL);
		if (oldVersion >= 6)
		{
			db.execSQL(NoteTemplates.DROP_TABLE_SQL);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void backupData(SQLiteDatabase db, int oldVersion)
	{
		Log.v(LOG_TAG, "Hello");

		File notesBackupDir = context.getDir(BACKUP_DIR_NOTES, Context.MODE_PRIVATE);
		if (oldVersion >= 12)
		{
			backupNotesVersion12(db, notesBackupDir);
		}
		else if (oldVersion >= 5)
		{
			backupNotesVersion5(db, notesBackupDir);
		}
		else if (oldVersion == 4)
		{
			backupNotesVersion4(db, notesBackupDir);
		}

		File noteTemplatesBackupDir = context.getDir(BACKUP_DIR_NOTE_TEMPLATES, Context.MODE_PRIVATE);
		if (oldVersion >= 12)
		{
			backupNoteTemplatesVersion12(db, noteTemplatesBackupDir);
		}
		else if (oldVersion >= 11)
		{
			backupNoteTemplatesVersion11(db, noteTemplatesBackupDir);
		}
		else if (oldVersion >= 8)
		{
			backupNoteTemplatesVersion8(db, noteTemplatesBackupDir);
		}
		else if (oldVersion == 7)
		{
			backupNoteTemplatesVersion7(db, noteTemplatesBackupDir);
		} // if version < 7, note template table is not exist.

		Log.v(LOG_TAG, "Bye");
	}

	private void restoreData(SQLiteDatabase db)
	{
		Log.v(LOG_TAG, "Hello");

		File notesBackupDir = context.getDir(BACKUP_DIR_NOTES, Context.MODE_PRIVATE);
		File noteTemplatesBackupDir = context.getDir(BACKUP_DIR_NOTE_TEMPLATES, Context.MODE_PRIVATE);
		restoreNotes(db, notesBackupDir);
		restoreNoteTemplates(db, noteTemplatesBackupDir);

		Log.v(LOG_TAG, "Bye");
	}

	private void cleanupBackupData(SQLiteDatabase db)
	{
		Log.v(LOG_TAG, "Hello");

		File notesBackupDir = context.getDir(BACKUP_DIR_NOTES, Context.MODE_PRIVATE);
		File noteTemplatesBackupDir = context.getDir(BACKUP_DIR_NOTE_TEMPLATES, Context.MODE_PRIVATE);
		clearBackupFiles(notesBackupDir);
		clearBackupFiles(noteTemplatesBackupDir);

		Log.v(LOG_TAG, "Bye");
	}

	private void backupNotesVersion4(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(Notes.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.Note.Columns._ID);
			int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
			int dateAddedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_ADDED);
			int dateModifiedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_MODIFIED);

			int index = 0;
			do
			{
				String noteUuid = UUID.randomUUID().toString();
				Note note = new Note();
				note.setId(cursor.getLong(idIndex));
				note.setUuid(noteUuid);
				note.setEnabled(true);
				note.setTitle(cursor.getString(titleIndex));
				note.setContent(cursor.getString(contentIndex));
				note.setTitleLocked(false);
				note.setAdded(cursor.getLong(dateAddedIndex));
				note.setModified(cursor.getLong(dateModifiedIndex));

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

	private void backupNotesVersion5(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(Notes.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.Note.Columns._ID);
			int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE_LOCKED);
			int dateAddedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_ADDED);
			int dateModifiedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_MODIFIED);

			int index = 0;
			do
			{

				String noteUuid = UUID.randomUUID().toString();
				Note note = new Note();
				note.setId(cursor.getLong(idIndex));
				note.setUuid(noteUuid);
				note.setEnabled(true);
				note.setTitle(cursor.getString(titleIndex));
				note.setContent(cursor.getString(contentIndex));
				note.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);
				note.setAdded(cursor.getLong(dateAddedIndex));
				note.setModified(cursor.getLong(dateModifiedIndex));

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

	/**
	 * The following columns were added.
	 * <ul>
	 * <li>uuid</li>
	 * <li>enabled</li>
	 * </ul>
	 *
	 * @param db
	 * @param backupDirectory
	 */
	private void backupNotesVersion12(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(Notes.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.Note.Columns._ID);
			int uuidIndex = cursor.getColumnIndex(NoteStore.Note.Columns.UUID);
			int enabledIndex = cursor.getColumnIndex(NoteStore.Note.Columns.ENABLED);
			int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE_LOCKED);
			int dateAddedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_ADDED);
			int dateModifiedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.DATE_MODIFIED);

			int index = 0;
			do
			{
				Note note = new Note();
				note.setId(cursor.getLong(idIndex));
				note.setUuid(cursor.getString(uuidIndex));
				note.setEnabled(cursor.getInt(enabledIndex) == 1);
				note.setTitle(cursor.getString(titleIndex));
				note.setContent(cursor.getString(contentIndex));
				note.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);
				note.setAdded(cursor.getLong(dateAddedIndex));
				note.setModified(cursor.getLong(dateModifiedIndex));

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

	private void backupNoteTemplatesVersion7(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(NoteTemplates.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns._ID);
			int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);

			int index = 0;
			do
			{
				long id = cursor.getLong(idIndex);
				String uuid = generateNoteTemplateUuid(id);

				NoteTemplate noteTemplate = new NoteTemplate();
				noteTemplate.setId(id);
				noteTemplate.setUuid(uuid);
				noteTemplate.setEnabled(true);
				noteTemplate.setName("");
				noteTemplate.setTitle(cursor.getString(titleIndex));
				noteTemplate.setContent(cursor.getString(contentIndex));
				noteTemplate.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);

				File noteFile = new File(backupDirectory, String.format("%08d", index++));
				Log.d(LOG_TAG, "backup : noteFile => " + noteFile);
				try
				{
					NoteTemplate.writeNoteTo(noteTemplate, noteFile);
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

	private void backupNoteTemplatesVersion8(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(NoteTemplates.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns._ID);
			int nameIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.NAME);
			int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);

			int index = 0;
			do
			{
				long id = cursor.getLong(idIndex);
				String uuid = generateNoteTemplateUuid(id);

				NoteTemplate noteTemplate = new NoteTemplate();
				noteTemplate.setId(id);
				noteTemplate.setUuid(uuid);
				noteTemplate.setEnabled(true);
				noteTemplate.setName(cursor.getString(nameIndex));
				noteTemplate.setTitle(cursor.getString(titleIndex));
				noteTemplate.setContent(cursor.getString(contentIndex));
				noteTemplate.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);
				noteTemplate.setEditSameTitle(true);

				File noteFile = new File(backupDirectory, String.format("%08d", index++));
				Log.d(LOG_TAG, "backup : noteFile => " + noteFile);
				try
				{
					NoteTemplate.writeNoteTo(noteTemplate, noteFile);
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

	private void backupNoteTemplatesVersion11(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(NoteTemplates.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns._ID);
			int nameIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.NAME);
			int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);
			int editSameTitleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE);

			int index = 0;
			do
			{
				long id = cursor.getLong(idIndex);
				String uuid = generateNoteTemplateUuid(id);

				NoteTemplate noteTemplate = new NoteTemplate();
				noteTemplate.setId(id);
				noteTemplate.setUuid(uuid);
				noteTemplate.setEnabled(true);
				noteTemplate.setName(cursor.getString(nameIndex));
				noteTemplate.setTitle(cursor.getString(titleIndex));
				noteTemplate.setContent(cursor.getString(contentIndex));
				noteTemplate.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);
				noteTemplate.setEditSameTitle(cursor.getInt(editSameTitleIndex) == 1);

				File noteFile = new File(backupDirectory, String.format("%08d", index++));
				Log.d(LOG_TAG, "backup : noteFile => " + noteFile);
				try
				{
					NoteTemplate.writeNoteTo(noteTemplate, noteFile);
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

	private void backupNoteTemplatesVersion12(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);

		Cursor cursor = db.query(NoteTemplates.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int idIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns._ID);
			int uuidIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.UUID);
			int enabledIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.ENABLED);
			int nameIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.NAME);
			int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
			int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
			int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);
			int editSameTitleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE);

			int index = 0;
			do
			{
				NoteTemplate noteTemplate = new NoteTemplate();
				noteTemplate.setId(cursor.getLong(idIndex));
				noteTemplate.setUuid(cursor.getString(uuidIndex));
				noteTemplate.setEnabled(cursor.getInt(enabledIndex) == 1);
				noteTemplate.setName(cursor.getString(nameIndex));
				noteTemplate.setTitle(cursor.getString(titleIndex));
				noteTemplate.setContent(cursor.getString(contentIndex));
				noteTemplate.setTitleLocked(cursor.getInt(titleLockedIndex) == 1);
				noteTemplate.setEditSameTitle(cursor.getInt(editSameTitleIndex) == 1);

				File noteFile = new File(backupDirectory, String.format("%08d", index++));
				Log.d(LOG_TAG, "backup : noteFile => " + noteFile);
				try
				{
					NoteTemplate.writeNoteTo(noteTemplate, noteFile);
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

	private void restoreNotes(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);
		if (backupDirectory.isDirectory() && backupDirectory.canRead())
		{
			File[] listFiles = backupDirectory.listFiles();
			if (listFiles != null)
			{
				for (File noteFile : listFiles)
				{
					try
					{
						Log.d(LOG_TAG, "restore : noteFile => " + noteFile);
						Note note = Note.readNoteFrom(noteFile);
						ContentValues values = new ContentValues();
						values.put(NoteStore.Note.Columns._ID, note.getId());
						values.put(NoteStore.Note.Columns.UUID, note.getUuid());
						values.put(NoteStore.Note.Columns.ENABLED, note.isEnabled());
						values.put(NoteStore.Note.Columns.TITLE, note.getTitle());
						values.put(NoteStore.Note.Columns.CONTENT, note.getContent());
						values.put(NoteStore.Note.Columns.TITLE_LOCKED, note.isTitleLocked());
						values.put(NoteStore.Note.Columns.DATE_ADDED, note.getAdded());
						values.put(NoteStore.Note.Columns.DATE_MODIFIED, note.getModified());
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
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void restoreNoteTemplates(SQLiteDatabase db, File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupDirectory => " + backupDirectory);
		if (backupDirectory.isDirectory() && backupDirectory.canRead())
		{
			File[] listFiles = backupDirectory.listFiles();
			if (listFiles != null)
			{
				for (File noteTemplateFile : listFiles)
				{
					try
					{
						Log.d(LOG_TAG, "restore : noteTemplateFile => " + noteTemplateFile);
						NoteTemplate noteTemplate = NoteTemplate.readNoteFrom(noteTemplateFile);
						ContentValues values = new ContentValues();
						values.put(NoteStore.NoteTemplate.Columns._ID, noteTemplate.getId());
						values.put(NoteStore.NoteTemplate.Columns.UUID, noteTemplate.getUuid());
						values.put(NoteStore.NoteTemplate.Columns.ENABLED, noteTemplate.isEnabled());
						values.put(NoteStore.NoteTemplate.Columns.NAME, noteTemplate.getName());
						values.put(NoteStore.NoteTemplate.Columns.TITLE, noteTemplate.getTitle());
						values.put(NoteStore.NoteTemplate.Columns.CONTENT, noteTemplate.getContent());
						values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, noteTemplate.isTitleLocked());
						values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, noteTemplate.isEditSameTitle());
						db.insert(NoteTemplates.TABLE_NAME, null, values);
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
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void clearBackupFiles(File backupDirectory)
	{
		Log.v(LOG_TAG, "Hello");

		if (backupDirectory != null && backupDirectory.isDirectory())
		{
			File[] listFiles = backupDirectory.listFiles();
			for (File backupFile : listFiles)
			{
				Log.d(LOG_TAG, "Delete : backupFile => " + backupFile);
				backupFile.delete();
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	static String generateNoteTemplateUuid(long id)
	{
		String uuid = DEFAULT_NOTE_TEMPLATE_UUID_MAP.get(id);
		Log.d(LOG_TAG, "note template : id => " + id);

		if (uuid == null)
		{
			uuid = UUID.randomUUID().toString();
		}

		Log.d(LOG_TAG, "uuid => " + uuid);
		return uuid;
	}
}
