/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.routine_work.notepad.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.format.DateFormat;

import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sawai
 */
public class NoteUtils implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	public static void shareNote(Context context, long noteId)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteId");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, noteId);
		shareNote(context, noteUri);

		Log.v(LOG_TAG, "Bye");
	}

	public static void shareNote(Context context, Uri noteUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		Note note = loadNoteData(context, noteUri);
		if (note != null)
		{
			shareNote(context, note.getTitle(), note.getContent());
		}

		Log.v(LOG_TAG, "Bye");
	}

	public static void shareNote(Context context, String noteTitle, String noteContent)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "titile => " + noteTitle);
		Log.d(LOG_TAG, "content => " + noteContent);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TITLE, noteTitle);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, noteTitle);
		shareIntent.putExtra(Intent.EXTRA_TEXT, noteContent);
		context.startActivity(shareIntent);

		Log.v(LOG_TAG, "Bye");
	}

	public static void copyNoteTitleToClipboard(Context context, long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		copyNoteTitleToClipboard(context, noteUri);

		Log.v(LOG_TAG, "Bye");
	}

	public static void copyNoteTitleToClipboard(Context context, Uri noteUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		Note note = loadNoteData(context, noteUri);
		if (note != null)
		{
			ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
			clipboardManager.setText(note.getTitle());
		}

		Log.v(LOG_TAG, "Bye");
	}

	public static void copyNoteContentToClipboard(Context context, long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		copyNoteContentToClipboard(context, noteUri);

		Log.v(LOG_TAG, "Bye");
	}

	public static void copyNoteContentToClipboard(Context context, Uri noteUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		Note note = loadNoteData(context, noteUri);
		if (note != null)
		{
			ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
			clipboardManager.setText(note.getContent());
		}

		Log.v(LOG_TAG, "Bye");
	}

	/**
	 * Start NoteDetailActivity without note template
	 * <p>
	 * Start NoteDetailActivity for editing new blank note.
	 *
	 * @param activity
	 */
	public static void startActivityToAddNewBlankNote(Activity activity)
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	public static void startActivityToAddNewNoteWithTemplate(Activity activity, long noteTemplateId)
	{
		Uri noteTemplateUri = ContentUris.withAppendedId(NoteStore.NoteTemplate.CONTENT_URI, noteTemplateId);
		NoteUtils.startActivityToAddNewNoteWithTemplate(activity, noteTemplateUri);
	}

	public static void startActivityToAddNewNoteWithTemplate(Activity activity, Uri noteTemplateUri)
	{
		Log.v(LOG_TAG, "Hello");

		String where = null;
		String[] whereArgs = null;
		if (NoteStore.NoteTemplate.CONTENT_URI.equals(noteTemplateUri))
		{
			where = NoteStore.Note.Columns.ENABLED + " = ?";
			whereArgs = new String[]{
					"1"
			};
		}

		Cursor cursor = activity.getContentResolver().query(noteTemplateUri,
				null, where, whereArgs, null);
		try
		{
			if (cursor != null && cursor.moveToFirst())
			{
				int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
				int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
				int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);
				int editSameTitleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE);

				String titleTemplate = cursor.getString(titleIndex);
				String contentTemplate = cursor.getString(contentIndex);
				boolean titleLocked = (cursor.getInt(titleLockedIndex) != 0);
				boolean editSameTitle = (cursor.getInt(editSameTitleIndex) != 0);

				Map<String, String> templateContextMap = new HashMap<String, String>();
				Date now = new Date();
				templateContextMap.put("date", DateFormat.getDateFormat(activity).format(now));
				templateContextMap.put("time", DateFormat.getTimeFormat(activity).format(now));

				String title = expandTemplate(titleTemplate, templateContextMap);
				String content = expandTemplate(contentTemplate, templateContextMap);

				Uri noteUri = null;
				if (editSameTitle)
				{
					noteUri = searchNoteByTitle(activity, title);
				}
				Log.d(LOG_TAG, "noteUri => " + noteUri);

				if (noteUri != null)
				{
					Log.d(LOG_TAG, "note is already exist.");
					// if note is already exist
					Intent intent = new Intent(Intent.ACTION_EDIT, noteUri);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(EXTRA_TEXT, content);
					activity.startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
				}
				else
				{
					Log.d(LOG_TAG, "note is not found.");
					// if not found, insert new note
					Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(EXTRA_TITLE, title);
					intent.putExtra(EXTRA_TEXT, content);
					intent.putExtra(EXTRA_TITLE_LOCKED, titleLocked);
					activity.startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
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

	public static void startActivityToAddNewNoteWithFirstTemplate(Activity activity)
	{
		NoteUtils.startActivityToAddNewNoteWithTemplate(activity, NoteStore.NoteTemplate.CONTENT_URI);
	}

	public static Uri searchNoteByTitle(Context context, String title)
	{
		Uri result = null;
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "title => " + title);

		if (!TextUtils.isEmpty(title))
		{
			ContentResolver contentResolver = context.getContentResolver();
			final String selection = NoteStore.Note.Columns.TITLE + " = ? "
					+ " AND "
					+ NoteStore.Note.Columns.ENABLED + " = ? ";
			final String[] selectionArgs = new String[]{
					title,
					"1"
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

	public static String expandTemplate(String template, Map<String, String> templateContextMap)
	{
		String text = template;

		for (String key : templateContextMap.keySet())
		{
			String value = templateContextMap.get(key);
			Log.d(LOG_TAG, "key => " + key);
			Log.d(LOG_TAG, "value => " + value);
			String regexp = "(?i)#\\{" + key + "\\}";
			Log.d(LOG_TAG, "regexp => " + regexp);
			text = text.replaceAll(regexp, value);
		}

		return text;
	}

	private static Note loadNoteData(Context context, Uri noteUri)
	{
		Note note = null;

		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		if (NoteStore.isNoteItemUri(context, noteUri))
		{
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(noteUri, null, null, null, null);
			if (cursor != null)
			{
				try
				{
					if (cursor.moveToFirst())
					{
						int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
						int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
						String noteTitle = cursor.getString(titleIndex);
						String noteContent = cursor.getString(contentIndex);
						note = new Note();
						note.setTitle(noteTitle);
						note.setContent(noteContent);
					}
				}
				finally
				{
					cursor.close();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
		return note;
	}
}
