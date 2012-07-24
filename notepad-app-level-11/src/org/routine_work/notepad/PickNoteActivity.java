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
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import org.routine_work.notepad.fragment.NoteListItemViewBinder;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class PickNoteActivity extends ListActivity
	implements NotepadConstants,
	AdapterView.OnItemClickListener,
	LoaderManager.LoaderCallbacks<Cursor>
{

	// class variables
	private static final String LOG_TAG = "simple-notepad";
	// instance variables
	private SimpleCursorAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_note_activity);

		Intent intent = getIntent();
		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		setTitle(title);

		listAdapter = new SimpleCursorAdapter(this,
			R.layout.note_list_item, null,
			MAPPING_FROM, MAPPING_TO);
		listAdapter.setViewBinder(new NoteListItemViewBinder(this));
		setListAdapter(listAdapter);

		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(NOTE_LOADER_ID, null, this);

		ListView listView = getListView();
		listView.setOnItemClickListener(this);

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

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");

		String sortOrder = NoteStore.NoteColumns.DATE_MODIFIED + " DESC";
		CursorLoader cursorLoader = new CursorLoader(this,
			NoteStore.CONTENT_URI, null, null, null, sortOrder);

		Log.v(LOG_TAG, "Bye");
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		Log.v(LOG_TAG, "Hello");
		listAdapter.swapCursor(cursor);
		Log.v(LOG_TAG, "Bye");
	}

	public void onLoaderReset(Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");
		listAdapter.swapCursor(null);
		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
}
