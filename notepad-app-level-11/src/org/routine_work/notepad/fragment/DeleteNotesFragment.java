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
package org.routine_work.notepad.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class DeleteNotesFragment extends ListFragment
	implements NotepadConstants,
	LoaderManager.LoaderCallbacks<Cursor>
{

	private static final String LOG_TAG = "simple-notepad";
	private NoteCursorAdapter listAdapter;

	public DeleteNotesFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.note_list_fragment, container, false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// Init list adapter
		listAdapter = new NoteCursorAdapter(getActivity(), null, true);
		setListAdapter(listAdapter);

		// Init LoaderManager
		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(NOTE_LOADER_ID, null, this);

		// Init ListView
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.delete_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.delete_note_menuitem:
				Log.d(LOG_TAG, "delete_note_menuitem");
				deleteCheckedNotes();
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");

		String where = NoteStore.Note.Columns.ENABLED + " = ?";
		String[] whereArgs =
		{
			"1"
		};
		String sortOrder = NotepadPreferenceUtils.getNoteListSortOrder(getActivity());
		Log.d(LOG_TAG, String.format("where => %s, whereArgs => %s, sortOrder => %s", where, whereArgs, sortOrder));
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
			NoteStore.Note.CONTENT_URI, null, where, whereArgs, sortOrder);

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

	private void deleteCheckedNotes()
	{
		Log.v(LOG_TAG, "Hello");

		long[] checkItemIds = getListView().getCheckItemIds();
		ContentResolver cr = getActivity().getContentResolver();
		NoteStore.deleteNotes(cr, checkItemIds);

		Log.v(LOG_TAG, "Bye");
	}
}
