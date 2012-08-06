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
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import org.routine_work.notepad.prefs.NotepadPreferenceActivity;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteSearchQueryParser;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NotepadActivity extends ListActivity
	implements View.OnClickListener, OnItemClickListener,
	TextView.OnEditorActionListener, OnFocusChangeListener,
	NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final int ACTION_MODE_NORMAL = 0;
	private static final int ACTION_MODE_SEARCH = 1;
	private static final int[][][] ACTION_ITEM_VISIBILITY =
	{
		{ // ACTION_MODE_NORMAL
			{ // GONE
				R.id.search_edittext,
				R.id.cancel_search_button,
			},
			{ // VISIBLE
				R.id.title_textview,
				R.id.add_new_note_button,
				R.id.search_button,
			},
		},
		{ // ACTION_MODE_SEARCH
			{ // GONE
				R.id.title_textview,
				R.id.add_new_note_button,
				R.id.search_button,
			},
			{ // VISIBLE
				R.id.search_edittext,
				R.id.cancel_search_button,
			},
		},
	};
	// instances
	private int actionMode = -1;
	private SimpleCursorAdapter listAdapter;
	private Cursor cursor;
	private EditText searchEditText;

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
		Log.setIndentMode(true);
	}

	public static void goHomeActivity(Context context)
	{
		Intent homeIntent = new Intent(Intent.ACTION_VIEW, NoteStore.CONTENT_URI);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(homeIntent);
	}

	public static void quitApplication(Context context)
	{
		Intent quitIntent = new Intent(context, NotepadActivity.class);
		quitIntent.setAction(ACTION_QUIT);
		quitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(quitIntent);
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_notes_activity);

		searchEditText = (EditText) findViewById(R.id.search_edittext);
		searchEditText.addTextChangedListener(new SearchEditTextWatcher());
		searchEditText.setOnFocusChangeListener(this);
		searchEditText.setOnEditorActionListener(this);

		setActionMode(ACTION_MODE_NORMAL);

		// Init ListAdapter
		listAdapter = new SimpleCursorAdapter(this,
			R.layout.note_list_item, cursor,
			MAPPING_FROM, MAPPING_TO);
		listAdapter.setViewBinder(new NoteListItemViewBinder(this));
		setListAdapter(listAdapter);

		// Init ListView
		ListView listView = getListView();
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);

		initializeWithIntent(getIntent());

		if (isFinishing() == false)
		{
			ImageButton homeImageButton = (ImageButton) findViewById(R.id.home_button);
			homeImageButton.setOnClickListener(this);
			ImageButton addImageButton = (ImageButton) findViewById(R.id.add_new_note_button);
			addImageButton.setOnClickListener(this);
			ImageButton searchImageButton = (ImageButton) findViewById(R.id.search_button);
			searchImageButton.setOnClickListener(this);
			ImageButton cancelSearchImageButton = (ImageButton) findViewById(R.id.cancel_search_button);
			cancelSearchImageButton.setOnClickListener(this);

		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		super.onNewIntent(intent);
		initializeWithIntent(intent);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");
		super.onResume();
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");
		super.onPause();
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
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "requestCode => " + requestCode);
		Log.d(LOG_TAG, "resultCode => " + resultCode);

		switch (requestCode)
		{
			case REQUEST_CODE_ADD_NOTE:
				Log.d(LOG_TAG, "Add new note is done.");
				if (resultCode == RESULT_OK)
				{
					updateContentWithUri(NoteStore.CONTENT_URI);
				}
				break;
			case REQUEST_CODE_EDIT_NOTE:
				Log.d(LOG_TAG, "Edit note is done.");
				if (resultCode == RESULT_OK)
				{
					updateContentWithUri(NoteStore.CONTENT_URI);
				}
				break;
			case REQUEST_CODE_DELETE_NOTE:
				Log.d(LOG_TAG, "Edit note is done.");
				if (resultCode == RESULT_OK)
				{
					updateContentWithUri(NoteStore.CONTENT_URI);
				}
				break;
			case REQUEST_CODE_DELETE_NOTES:
				Log.d(LOG_TAG, "Delete notes is done.");
				if (resultCode == RESULT_OK)
				{
					updateContentWithUri(NoteStore.CONTENT_URI);
				}
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.note_list_context_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		boolean result = true;

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		Log.v(LOG_TAG, "list item position => " + menuInfo.position);
		Log.v(LOG_TAG, "list item id => " + menuInfo.id);
		Log.v(LOG_TAG, "list item targetView => " + menuInfo.targetView);

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.view_note_menuitem:
				startViewNoteActivityById(menuInfo.id);
				break;
			case R.id.edit_note_menuitem:
				startEditNoteActivityById(menuInfo.id);
				break;
			case R.id.delete_note_menuitem:
				startDeleteNoteActivityById(menuInfo.id);
				break;
			case R.id.share_note_menuitem:
				NoteUtils.shareNote(this, menuInfo.id);
				break;
			default:
				result = super.onContextItemSelected(item);
		}

		return result;
	}

	public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "clicked item position => " + position);
		Log.d(LOG_TAG, "clicked item id => " + id);
//		startEditNoteActivityById(id);
		startViewNoteActivityById(id);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.add_new_note_option_menu, menu);
		menuInflater.inflate(R.menu.delete_notes_option_menu, menu);
		menuInflater.inflate(R.menu.search_note_option_menu, menu);
		menuInflater.inflate(R.menu.preferences_menu, menu);
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
			case R.id.add_new_note_menuitem:
				Log.d(LOG_TAG, "add_new_note_menuitem is selected.");
				startAddNewNoteActivity();
				break;
			case R.id.delete_notes_menuitem:
				Log.d(LOG_TAG, "delete_notes_menuitem is selected.");
				startDeleteNotesActivity();
				break;
			case R.id.search_note_menuitem:
				Log.d(LOG_TAG, "search_note_menuitem is selected..");
				enterSearchMode();
				break;
			case R.id.preferences_menuitem:
				Log.d(LOG_TAG, "preferences_menuitem is selected.");
				startPreferencesActivity();
				break;
			case R.id.quit_menuitem:
				Log.d(LOG_TAG, "quit_menuitem is selected.");
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	public void onClick(View view)
	{
		Log.v(LOG_TAG, "Hello");

		int viewId = view.getId();
		switch (viewId)
		{
			case R.id.home_button:
				Log.d(LOG_TAG, "home_button is clicked.");
				cancelSearchMode();
				break;
			case R.id.add_new_note_button:
				Log.d(LOG_TAG, "add_new_note_buttonis clicked.");
				startAddNewNoteActivity();
				break;
			case R.id.search_button:
				Log.d(LOG_TAG, "search_button is clicked.");
				enterSearchMode();
				break;
			case R.id.cancel_search_button:
				Log.d(LOG_TAG, "cancel_search_button is clicked.");
				cancelSearchMode();
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean result;
		Log.v(LOG_TAG, "Hello");

		if ((keyCode == KeyEvent.KEYCODE_BACK)
			&& (actionMode != ACTION_MODE_NORMAL))
		{
			cancelSearchMode();
			result = true;
		}
		else
		{
			result = super.onKeyDown(keyCode, event);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	// TextView.OnEditorActionListener
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (actionId == EditorInfo.IME_ACTION_SEARCH)
		{
			IMEUtils.hideSoftKeyboardWindow(this, v);
		}
		return true;
	}

	public void onFocusChange(View v, boolean hasFocus)
	{
		if (v.getId() == R.id.search_edittext && hasFocus)
		{
			IMEUtils.showSoftKeyboardWindow(this, v);
		}
	}

	private void initializeWithIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "------------------------------");
		Log.d(LOG_TAG, "intent.action => " + intent.getAction());
		Log.d(LOG_TAG, "intent.data => " + intent.getData());
		Log.d(LOG_TAG, "intent.type => " + intent.getType());
		Log.d(LOG_TAG, "intent.scheme => " + intent.getScheme());
		Log.d(LOG_TAG, "------------------------------");

		String action = intent.getAction();
		if (ACTION_QUIT.equals(action))
		{
			finish();
		}
		else if (Intent.ACTION_SEARCH.equals(action))
		{
			String queryString = intent.getStringExtra(SearchManager.QUERY);
			searchEditText.setText(queryString);
		}
		else if (Intent.ACTION_VIEW.equals(action))
		{
			int flags = intent.getFlags();
			Log.v(LOG_TAG, "flags => 0x" + Integer.toHexString(flags));

			if ((flags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0)
			{
				Log.d(LOG_TAG, "flags includes FLAG_ACTIVITY_NEW_TASK");

				boolean processed = false;
				Uri data = intent.getData();
				if (data != null)
				{
					ContentResolver contentResolver = getContentResolver();
					String type = contentResolver.getType(data);
					if (NoteStore.NOTE_ITEM_CONTENT_TYPE.equals(type))
					{
						Log.d(LOG_TAG, "open note : data => " + data);
						String idString = data.getLastPathSegment();
						searchEditText.setText("id:" + idString);
						processed = true;
					}
				}

				if (!processed)
				{
					String queryString = intent.getStringExtra(SearchManager.QUERY);
					searchEditText.setText(queryString);
				}
			}
		}

		if (TextUtils.isEmpty(searchEditText.getText()))
		{
			searchEditText.setText(null);
			setActionMode(ACTION_MODE_NORMAL);
		}
		else
		{
			setActionMode(ACTION_MODE_SEARCH);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startAddNewNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.CONTENT_URI);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra(Intent.EXTRA_TITLE, "New Note");
//		intent.putExtra(Intent.EXTRA_TEXT, "This is a text of new note.");
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startViewNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, id);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivityForResult(intent, REQUEST_CODE_VIEW_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startEditNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, id);
		Intent intent = new Intent(Intent.ACTION_EDIT, uri);
		startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, id);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		startActivityForResult(intent, REQUEST_CODE_DELETE_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNotesActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(this, DeleteNotesActivity.class);
		startActivityForResult(intent, REQUEST_CODE_DELETE_NOTES);

		Log.v(LOG_TAG, "Bye");
	}

	private void startPreferencesActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(this, NotepadPreferenceActivity.class);
		startActivity(intent);

		Log.v(LOG_TAG, "Bye");
	}

	private void setActionMode(int newActionMode)
	{
		Log.v(LOG_TAG, "Hello");

		if (actionMode != newActionMode)
		{
			actionMode = newActionMode;
			Log.d(LOG_TAG, "update actionMode => " + actionMode);
			int[] goneViewIds = ACTION_ITEM_VISIBILITY[actionMode][0];
			for (int i = 0; i < goneViewIds.length; i++)
			{
				View view = findViewById(goneViewIds[i]);
				if (view != null)
				{
					view.setVisibility(View.GONE);
				}
			}
			int[] visibleViewIds = ACTION_ITEM_VISIBILITY[actionMode][1];
			for (int i = 0; i < visibleViewIds.length; i++)
			{
				View view = findViewById(visibleViewIds[i]);
				if (view != null)
				{
					view.setVisibility(View.VISIBLE);
				}
			}

		}

		Log.v(LOG_TAG, "Bye");
	}

	private void updateContentWithQuery(String queryString)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "queryString => " + queryString);

		Uri contentUri = NoteSearchQueryParser.parseQuery(queryString);
		Log.v(LOG_TAG, "contentUri => " + contentUri);

		if (contentUri != null)
		{
			updateContentWithUri(contentUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void updateContentWithUri(Uri contentUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "contentUri => " + contentUri);

		String sortOrder = NoteStore.NoteColumns.DATE_MODIFIED + " DESC";
		Cursor newCursor = getContentResolver().query(contentUri, null, null, null, sortOrder);
		listAdapter.changeCursor(newCursor);

		if (cursor != null)
		{
			cursor.close();
		}
		cursor = newCursor;

		Log.v(LOG_TAG, "Bye");
	}

	private void cancelSearchMode()
	{
		if (actionMode != ACTION_MODE_NORMAL)
		{
			searchEditText.setText(null);
			getListView().requestFocus();
			setActionMode(ACTION_MODE_NORMAL);
			IMEUtils.hideSoftKeyboardWindow(this, searchEditText);
		}
	}

	private void enterSearchMode()
	{
		if (actionMode != ACTION_MODE_SEARCH)
		{
			setActionMode(ACTION_MODE_SEARCH);
			searchEditText.requestFocus();
			IMEUtils.showSoftKeyboardWindow(this, searchEditText);
		}
	}

	class SearchEditTextWatcher implements TextWatcher
	{

		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			Log.v(LOG_TAG, "Hello");
			Log.v(LOG_TAG, "Bye");
		}

		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			Log.v(LOG_TAG, "Hello");

			updateContentWithQuery(s.toString());

			Log.v(LOG_TAG, "Bye");
		}

		public void afterTextChanged(Editable s)
		{
			Log.v(LOG_TAG, "Hello");
			Log.v(LOG_TAG, "Bye");
		}
	}
}
