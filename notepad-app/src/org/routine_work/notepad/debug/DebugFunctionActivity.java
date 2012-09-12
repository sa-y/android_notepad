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
package org.routine_work.notepad.debug;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class DebugFunctionActivity extends ListActivity
	implements AdapterView.OnItemClickListener
{

	private static final String LOG_TAG = "simple-notepad";
	private static final int ITEM_ID_CREATE_NOTES = 0;
	private static final int ITEM_ID_DELETE_ALL_NOTES = 1;
	private static final String[] FUNCTION_NAMES =
	{
		"Create Test Notes",
		"Delete All Notes",
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_1,
			android.R.id.text1,
			FUNCTION_NAMES);
		setListAdapter(arrayAdapter);

		getListView().setOnItemClickListener(this);

		Log.v(LOG_TAG, "Bye");
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		switch (position)
		{
			case ITEM_ID_CREATE_NOTES:
				Log.d(LOG_TAG, "ITEM_ID_CREATE_NOTES is clicked.");
				DebugFunctionActivity.CreateTestNoteTask createTestNoteTask = new DebugFunctionActivity.CreateTestNoteTask();
				createTestNoteTask.execute();
				break;
			case ITEM_ID_DELETE_ALL_NOTES:
				Log.d(LOG_TAG, "ITEM_ID_DELETE_ALL_NOTES is clicked.");
				DebugFunctionActivity.DeleteAllNotesTask deleteAllNotesTask = new DebugFunctionActivity.DeleteAllNotesTask();
				deleteAllNotesTask.execute();
				break;
		}
	}

	class CreateTestNoteTask extends AsyncTask<Void, Void, Boolean>
	{

		private static final int DATA_COUNT = 100;

		@Override
		protected Boolean doInBackground(Void... arg0)
		{
			Boolean result = Boolean.FALSE;

			try
			{
				ContentResolver contentResolver = getContentResolver();
				ContentValues values = new ContentValues();

				for (int i = 0; i < DATA_COUNT; i++)
				{
					String title = String.format("Title : %08d", i);
					String content = String.format("Text : %08d", i);
					boolean titleLocked = (i % 3) == 0;
					long now = System.currentTimeMillis();
					values.clear();
					values.put(NoteStore.Note.Columns.TITLE, title);
					values.put(NoteStore.Note.Columns.CONTENT, content);
					values.put(NoteStore.Note.Columns.TITLE_LOCKED, titleLocked);
					values.put(NoteStore.Note.Columns.DATE_ADDED, now);
					values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);
					contentResolver.insert(NoteStore.Note.CONTENT_URI, values);
				}
				result = Boolean.TRUE;
			}
			catch (Exception e)
			{
				Log.e(LOG_TAG, "The test note data creation failed.", e);
			}

			return result;
		}

		@Override
		protected void onPreExecute()
		{
			Toast.makeText(DebugFunctionActivity.this, "The test data creation was started.", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			String message;
			if (result.booleanValue())
			{
				message = "The test note data were created.";
			}
			else
			{
				message = "The test data creation failed.";
			}
			Toast.makeText(DebugFunctionActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	}

	class DeleteAllNotesTask extends AsyncTask<Void, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(Void... arg0)
		{
			Boolean result = Boolean.FALSE;

			try
			{
				ContentResolver contentResolver = getContentResolver();
				contentResolver.delete(NoteStore.Note.CONTENT_URI, null, null);
				result = Boolean.TRUE;
			}
			catch (Exception e)
			{
				Log.e(LOG_TAG, "The deletion of data failed.", e);
			}

			return result;
		}

		@Override
		protected void onPreExecute()
		{
			Toast.makeText(DebugFunctionActivity.this, "The deletion of data was started.", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			String message;
			if (result.booleanValue())
			{
				message = "All data were deleted.";
			}
			else
			{
				message = "The deletion of data failed.";
			}
			Toast.makeText(DebugFunctionActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	}
}
