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

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class DeleteNotesActivity extends ListActivity
	implements View.OnClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	// instances
	private NoteCursorAdapter listAdapter;
	private Cursor cursor;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_notes_activity);

		ImageButton homeButton = (ImageButton) findViewById(R.id.home_button);
		homeButton.setOnClickListener(this);
		ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_note_button);
		deleteButton.setOnClickListener(this);

		initializeListData();

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onDestroy()
	{
		Log.v(LOG_TAG, "Hello");

		if (cursor != null)
		{
			cursor.close();
			cursor = null;
		}
		super.onDestroy();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_menu, menu);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.quit_menuitem:
				Log.d(LOG_TAG, "quit_menuitem selected.");
				NotepadActivity.quitApplication(this);
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	public void onClick(View view)
	{
		Log.v(LOG_TAG, "Hello");

		int viewId = view.getId();
		switch (viewId)
		{
			case R.id.home_button:
				Log.d(LOG_TAG, "Home Button is clicked.");
				setResult(RESULT_CANCELED);
				finish();
				NotepadActivity.goHomeActivity(this);
				break;
			case R.id.delete_note_button:
				Log.d(LOG_TAG, "Delete Button is clicked.");
				deleteSelectedNotes();
				setResult(RESULT_OK);
				finish();
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void initializeListData()
	{
		Log.v(LOG_TAG, "Hello");

		String where = NoteStore.Note.Columns.ENABLED + " = ?";
		String[] whereArgs =
		{
			"1"
		};
		String sortOrder = NotepadPreferenceUtils.getNoteListSortOrder(this);
		Log.d(LOG_TAG, String.format("where => %s, whereArgs => %s, sortOrder => %s",
			where, Arrays.toString(whereArgs), sortOrder));
		ContentResolver cr = getContentResolver();
		Cursor newCursor = cr.query(NoteStore.Note.CONTENT_URI, null,
			where, whereArgs, sortOrder);
		if (newCursor != null && newCursor.moveToFirst())
		{
			cursor = newCursor;
		}

		listAdapter = new NoteCursorAdapter(this, cursor, true);
		setListAdapter(listAdapter);

		Log.v(LOG_TAG, "Bye");
	}

	private void deleteSelectedNotes()
	{
		Log.v(LOG_TAG, "Hello");

		List<Long> checkedIdList = new ArrayList<Long>();
		ListView listView = getListView();
		SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
		for (int i = checkedItemPositions.size() - 1; i >= 0; i--)
		{
			if (checkedItemPositions.valueAt(i))
			{
				int checkedPosition = checkedItemPositions.keyAt(i);
				long id = listView.getItemIdAtPosition(checkedPosition);
				checkedIdList.add(id);
			}
		}
		NoteStore.deleteNotes(getContentResolver(), checkedIdList);

		Log.v(LOG_TAG, "Bye");
	}
}
