/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.routine_work.notepad.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.util.Date;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
public class NoteUtils
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
						shareNote(context, noteTitle, noteContent);
					}
				}
				finally
				{
					cursor.close();
				}
			}
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

	private static void startNoteDetailActivityWithTemplate(Context context, Uri noteTemplateUri)
	{
		Log.v(LOG_TAG, "Hello");
		Cursor cursor = context.getContentResolver().query(noteTemplateUri,
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
				String dateText = DateFormat.getDateFormat(context).format(now);
				String timeText = DateFormat.getTimeFormat(context).format(now);
				String titleTemplate = cursor.getString(titleIndex);
				String contentTemplate = cursor.getString(contentIndex);
				String title = String.format(titleTemplate, dateText, timeText);
				String content = String.format(contentTemplate, dateText, timeText);

				Uri noteUri = searchNoteByTitle(context, title);
				if (noteUri != null)
				{
					// if note is already exist
					Intent intent = new Intent(Intent.ACTION_EDIT, noteUri);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
				}
				else
				{
					// if not found, insert new note
					Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(Intent.EXTRA_TITLE, title);
					intent.putExtra(Intent.EXTRA_TEXT, content);
//					intent.putExtra(EXTRA_TITLE_LOCKED, titleLocked);
					context.startActivity(intent);
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

	public static  Uri searchNoteByTitle(Context context, String title)
	{
		Uri result = null;
		Log.v(LOG_TAG, "Hello");

		if (!TextUtils.isEmpty(title))
		{
			ContentResolver contentResolver = context.getContentResolver();
			final String selection = NoteStore.Note.Columns.TITLE + " = ? ";
			final String[] selectionArgs = new String[]
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
