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

		Uri noteUri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, noteId);
		shareNote(context, noteUri);

		Log.v(LOG_TAG, "Bye");
	}

	public static void shareNote(Context context, Uri noteUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(noteUri, null, null, null, null);
		if (cursor != null)
		{
			try
			{
				if (cursor.moveToFirst())
				{
					int titleIndex = cursor.getColumnIndex(NoteStore.NoteColumns.TITLE);
					int contentIndex = cursor.getColumnIndex(NoteStore.NoteColumns.CONTENT);
					String noteTitle = cursor.getString(titleIndex);
					String noteContent = cursor.getString(contentIndex);
					Log.d(LOG_TAG, "noteTitle => " + noteTitle);
					Log.d(LOG_TAG, "noteContent => " + noteContent);

					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_TITLE, noteTitle);
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, noteTitle);
					shareIntent.putExtra(Intent.EXTRA_TEXT, noteContent);
					context.startActivity(shareIntent);
				}
			}
			finally
			{
				cursor.close();
			}
		}

		Log.v(LOG_TAG, "Bye");
	}
}
