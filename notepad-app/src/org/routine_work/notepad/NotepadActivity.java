/*
 * The MIT License
 *
 * Copyright 2012-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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

import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Arrays;
import org.routine_work.notepad.prefs.NotepadPreferenceActivity;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteDBOptimizer;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.template.NoteTemplateInitializer;
import org.routine_work.notepad.template.NoteTemplatePickerDialog;
import org.routine_work.notepad.utils.NoteSearchQueryParser;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NotepadActivity extends ListActivity
	implements View.OnClickListener, OnItemClickListener,
	TextView.OnEditorActionListener, OnFocusChangeListener,
	SharedPreferences.OnSharedPreferenceChangeListener,
	NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String ACTION_QUIT = NotepadConstants.class.getPackage().getName() + ".QUIT";
	private static final String SAVE_KEY_ACTION_MODE = "ACTION_MODE";
	private static final int ACTION_MODE_NORMAL = 0;
	private static final int ACTION_MODE_SEARCH = 1;
	private static final int DIALOG_ID_NOTE_TEMPLATE_PICKER = 0;
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
	private NoteCursorAdapter listAdapter;
	private Cursor cursor;
	private EditText searchEditText;
	private NoteTemplatePickerDialog noteTemplatePickerDialog;

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
		Log.setIndentMode(true);
	}

	public static void goHomeActivity(Context context)
	{
		Intent homeIntent = new Intent(Intent.ACTION_VIEW, NoteStore.Note.CONTENT_URI);
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_activity);

		initializeNoteTemplateData();

		searchEditText = (EditText) findViewById(R.id.search_edittext);
		searchEditText.addTextChangedListener(new SearchEditTextWatcher());
		searchEditText.setOnFocusChangeListener(this);
		searchEditText.setOnEditorActionListener(this);

		setActionMode(ACTION_MODE_NORMAL);

		// Init ListAdapter
		listAdapter = new NoteCursorAdapter(this, cursor);
		setListAdapter(listAdapter);

		// Init ListView
		ListView listView = getListView();
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);

		if (savedInstanceState == null)
		{
			initializeWithIntent(getIntent());
		}
		else
		{
			initializeWithSavedInstance(savedInstanceState);
		}

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

			SharedPreferences sharedPreferences = NotepadPreferenceUtils.getSharedPreferences(this);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

		int quitCount = NotepadPreferenceUtils.incrementQuitCount(this);
		if (quitCount % 10 == 0)
		{
			Intent noteDBOptimizerIntent = new Intent(this, NoteDBOptimizer.class);
			startService(noteDBOptimizerIntent);
		}

		super.onDestroy();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onSaveInstanceState(outState);
		outState.putInt(SAVE_KEY_ACTION_MODE, actionMode);

		Log.v(LOG_TAG, "bye");
	}

	/*
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
	 updateContentWithUri(NoteStore.Note.CONTENT_URI);
	 }
	 break;
	 case REQUEST_CODE_EDIT_NOTE:
	 Log.d(LOG_TAG, "Edit note is done.");
	 if (resultCode == RESULT_OK)
	 {
	 updateContentWithUri(NoteStore.Note.CONTENT_URI);
	 }
	 break;
	 case REQUEST_CODE_DELETE_NOTE:
	 Log.d(LOG_TAG, "Delete note is done.");
	 if (resultCode == RESULT_OK)
	 {
	 updateContentWithUri(NoteStore.Note.CONTENT_URI);
	 }
	 break;
	 case REQUEST_CODE_DELETE_NOTES:
	 Log.d(LOG_TAG, "Delete notes is done.");
	 if (resultCode == RESULT_OK)
	 {
	 updateContentWithUri(NoteStore.Note.CONTENT_URI);
	 }
	 break;
	 }

	 Log.v(LOG_TAG, "Bye");
	 }
	 */
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
		menuInflater.inflate(R.menu.templates_option_menu, menu);
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
				addNewNote();
				break;
			case R.id.delete_notes_menuitem:
				Log.d(LOG_TAG, "delete_notes_menuitem is selected.");
				startDeleteNotesActivity();
				break;
			case R.id.search_note_menuitem:
				Log.d(LOG_TAG, "search_note_menuitem is selected..");
				enterSearchMode();
				break;
			case R.id.templates_menuitem:
				Log.d(LOG_TAG, "templates_menuitem is selected..");
				startTemplateListActivity();
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
				addNewNote();
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

	@Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog = null;
		Log.v(LOG_TAG, "Hello");

		switch (id)
		{
			case DIALOG_ID_NOTE_TEMPLATE_PICKER:
				dialog = getNoteTemplatePickerDialog();
				break;
		}

		Log.v(LOG_TAG, "dialog => " + dialog);
		Log.v(LOG_TAG, "Bye");
		return dialog;
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
		Log.v(LOG_TAG, "Hello");

		if (v.getId() == R.id.search_edittext)
		{
			if (hasFocus)
			{
				Log.d(LOG_TAG, "search_edittext has focus.");
				IMEUtils.showSoftKeyboardWindow(this, v);
			}
			else
			{
				Log.d(LOG_TAG, "search_edittext has focus.");
				IMEUtils.hideSoftKeyboardWindow(this, v);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "shared preference " + key + " is changed.");

		String noteListSortOrderKey = getString(R.string.note_list_sort_order_key);
		if (noteListSortOrderKey.equals(key))
		{
			Log.d(LOG_TAG, "sort order is changed, update note list with new sort order.");
			String queryString = searchEditText.getText().toString();
			updateContentWithQuery(queryString);
		}

		Log.v(LOG_TAG, "Bye");
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

		String queryString = null;
		String action = intent.getAction();
		if (ACTION_QUIT.equals(action))
		{
			finish();
		}
		else if (Intent.ACTION_SEARCH.equals(action))
		{
			queryString = intent.getStringExtra(SearchManager.QUERY);
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
					if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
					{
						Log.d(LOG_TAG, "open note : data => " + data);
						String idString = data.getLastPathSegment();
						queryString = "id:" + idString;
						processed = true;
					}
				}

				if (!processed)
				{
					queryString = intent.getStringExtra(SearchManager.QUERY);
				}
			}
		}

		if (queryString != null)
		{
			enterSearchMode();
			if (TextUtils.isEmpty(queryString.trim()))
			{
				searchEditText.setText(null);
			}
			else
			{
				searchEditText.setText(queryString);
			}
		}
		else
		{
			setActionMode(ACTION_MODE_NORMAL);
			searchEditText.setText(null);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void initializeWithSavedInstance(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		int savedActionMode = savedInstanceState.getInt(SAVE_KEY_ACTION_MODE);
		setActionMode(savedActionMode);

		Log.v(LOG_TAG, "Bye");
	}

	private void addNewNote()
	{
		Log.v(LOG_TAG, "Hello");

		int noteTemplateCount = NoteStore.getNoteTemplateCount(getContentResolver());
		if (noteTemplateCount >= 2)
		{
			showDialog(DIALOG_ID_NOTE_TEMPLATE_PICKER);
		}
		else if (noteTemplateCount == 1)
		{
			NoteUtils.startActivityForAddNewNoteWithFirstTemplate(this);
		}
		else if (noteTemplateCount == 0)
		{
			NoteUtils.startActivityForAddNewBlankNote(this);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startViewNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivityForResult(intent, REQUEST_CODE_VIEW_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startEditNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		Intent intent = new Intent(Intent.ACTION_EDIT, uri);
		startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivityById(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri uri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
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

	private void startTemplateListActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_VIEW, NoteStore.NoteTemplate.CONTENT_URI);
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

		String where = NoteStore.Note.Columns.ENABLED + " = ?";
		String[] whereArgs =
		{
			"1",
		};
		String sortOrder = NotepadPreferenceUtils.getNoteListSortOrder(this);
		Log.d(LOG_TAG, String.format("where => %s, whereArgs => %s, sortOrder => %s", where, Arrays.toString(whereArgs), sortOrder));
		ContentResolver cr = getContentResolver();
		Cursor newCursor = cr.query(contentUri, null, where, whereArgs, sortOrder);
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
		}
	}

	private void enterSearchMode()
	{
		if (actionMode != ACTION_MODE_SEARCH)
		{
			setActionMode(ACTION_MODE_SEARCH);
			IMEUtils.requestKeyboardFocus(searchEditText);
//			searchEditText.requestFocus();
		}
	}

	private Dialog getNoteTemplatePickerDialog()
	{
		if (noteTemplatePickerDialog == null)
		{
			noteTemplatePickerDialog = new NoteTemplatePickerDialog(this);
		}
		return noteTemplatePickerDialog;
	}

	private void initializeNoteTemplateData()
	{
		if (NotepadPreferenceUtils.isTemplateDataInitialized(this) == false)
		{
			NotepadPreferenceUtils.setTemplateDataInitialized(this, true);
			int noteTemplateCount = NoteStore.getNoteTemplateCount(getContentResolver());
			Log.d(LOG_TAG, "noteTemplateCount => " + noteTemplateCount);
			if (noteTemplateCount == 0)
			{
				Log.d(LOG_TAG, "start NoteTemplateInitializer");
				Intent noteTeplateInitializerIntent = new Intent(this, NoteTemplateInitializer.class);
				startService(noteTeplateInitializerIntent);
			}
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
