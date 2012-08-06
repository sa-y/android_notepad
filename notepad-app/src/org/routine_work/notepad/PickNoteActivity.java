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

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class PickNoteActivity extends ListActivity
	implements AdapterView.OnItemClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	// instances
	private SimpleCursorAdapter listAdapter;
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
		setContentView(R.layout.pick_note_activity);

		Intent intent = getIntent();
		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		if (title != null)
		{
			TextView titleTextView = (TextView) findViewById(R.id.title_textview);
			titleTextView.setText(title);
		}

		initializeListData();

		ListView listView = getListView();
		listView.setOnItemClickListener(this);

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
	public void onItemClick(AdapterView<?> av, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, id);
		Intent resultIntent = new Intent();
		resultIntent.setData(noteUri);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();

		Log.v(LOG_TAG, "Bye");
	}

	private void initializeListData()
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(NoteStore.CONTENT_URI, null, null, null,
			NoteStore.NoteColumns.DATE_MODIFIED + " DESC");
		if (c != null && c.moveToFirst())
		{
			cursor = c;
		}

		listAdapter = new SimpleCursorAdapter(this,
			R.layout.note_list_item, cursor,
			MAPPING_FROM, MAPPING_TO);
		listAdapter.setViewBinder(new NoteListItemViewBinder(this));
		setListAdapter(listAdapter);

		Log.v(LOG_TAG, "Bye");
	}
}
