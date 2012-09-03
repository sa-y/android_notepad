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
package org.routine_work.notepad.template;

import org.routine_work.notepad.utils.NoteTemplateConstants;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplateListActivity extends ListActivity
	implements AdapterView.OnItemClickListener, NoteTemplateConstants,
	LoaderManager.LoaderCallbacks<Cursor>
{

	private static final int NOTE_TEMPLATELOADER_ID = 0;
	private static final String LOG_TAG = "simple-notepad";
	// instances
	private String currentAction;
	private SimpleCursorAdapter listAdapter;
	private Cursor cursor;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_option_menu, menu);

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
	public void onItemClick(AdapterView<?> av, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");

		if (Intent.ACTION_VIEW.equals(currentAction))
		{
			startEditNoteTemplateActivityById(id);
		}
		else if (Intent.ACTION_PICK.equals(currentAction))
		{
			Uri noteTemplateUri = ContentUris.withAppendedId(NoteStore.NoteTemplate.CONTENT_URI, id);
			Intent resultIntent = new Intent();
			resultIntent.setData(noteTemplateUri);
			setResult(RESULT_OK, resultIntent);
			finish();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.note_template_list_context_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		boolean result = true;

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		Log.v(LOG_TAG, "list item position => " + menuInfo.position);
		Log.v(LOG_TAG, "list item id => " + menuInfo.id);

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.edit_note_template_menuitem:
				startEditNoteTemplateActivityById(menuInfo.id);
				break;
			case R.id.delete_note_template_menuitem:
				deleteNoteTemplateById(menuInfo.id);
				break;
			default:
				result = super.onContextItemSelected(item);
		}

		return result;
	}

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");

		String sortOrder = NoteStore.NoteTemplate.Columns._ID + " ASC";
		CursorLoader cursorLoader = new CursorLoader(this,
			NoteStore.NoteTemplate.CONTENT_URI, null, null, null, sortOrder);

		Log.v(LOG_TAG, "Bye");
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor d)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "cursor => " + cursor);
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_template_list_activity);

		initializeListData();

		ListView listView = getListView();
		listView.setOnItemClickListener(this);

		Intent intent = getIntent();
		currentAction = intent.getAction();
		if (currentAction == null || Intent.ACTION_EDIT.equals(currentAction))
		{
			currentAction = Intent.ACTION_VIEW;
		}

		if (Intent.ACTION_VIEW.equals(currentAction))
		{
			registerForContextMenu(listView);
		}
		else if (Intent.ACTION_PICK.equals(currentAction))
		{
			String title = intent.getStringExtra(Intent.EXTRA_TITLE);
			if (title == null)
			{
				title = getString(R.string.select_note_template_title);
			}
			setTitle(title);
		}


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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			listAdapter.notifyDataSetChanged();
		}
	}

	private void initializeListData()
	{
		Log.v(LOG_TAG, "Hello");
		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(NOTE_TEMPLATELOADER_ID, null, this);

		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(NoteStore.NoteTemplate.CONTENT_URI, null, null, null,
			NoteStore.NoteTemplate.Columns._ID + " ASC");
		if (c != null && c.moveToFirst())
		{
			cursor = c;
		}

		listAdapter = new SimpleCursorAdapter(this,
			android.R.layout.simple_list_item_1, cursor,
			NOTE_TEMPLATE_LIST_MAPPING_FROM, NOTE_TEMPLATE_LIST_MAPPING_TO);
		setListAdapter(listAdapter);

		Log.v(LOG_TAG, "Bye");
	}

	private void startAddNewNoteTemplateActivity()
	{
		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.NoteTemplate.CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE_TEMPLATE);
	}

	private void startEditNoteTemplateActivityById(long id)
	{
		Uri noteTemplateUri = ContentUris.withAppendedId(NoteStore.NoteTemplate.CONTENT_URI, id);
		Intent editNoteTemplateIntent = new Intent(Intent.ACTION_EDIT, noteTemplateUri);
		startActivityForResult(editNoteTemplateIntent, REQUEST_CODE_EDIT_NOTE_TEMPLATE);
	}

	private void deleteNoteTemplateById(long id)
	{
		Uri noteTemplateUri = ContentUris.withAppendedId(NoteStore.NoteTemplate.CONTENT_URI, id);
		ContentResolver contentResolver = getContentResolver();
		contentResolver.delete(noteTemplateUri, null, null);
		listAdapter.notifyDataSetChanged();
	}
}
