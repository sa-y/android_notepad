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

		if (isNoteItemUri(context, noteUri))
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

	public static boolean isNoteItemUri(Context context, Uri uri)
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver contentResolver = context.getContentResolver();
		String type = contentResolver.getType(uri);
		Log.d(LOG_TAG, "noteUri => " + uri + ", type => " + type);
		boolean result = NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type);

		Log.v(LOG_TAG, "Bye");
		return result;
	}
}
