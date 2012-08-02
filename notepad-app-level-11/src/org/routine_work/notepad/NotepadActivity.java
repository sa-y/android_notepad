
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

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import org.routine_work.notepad.fragment.NoteControlCallback;
import org.routine_work.notepad.fragment.NoteDetailFragment;
import org.routine_work.notepad.fragment.NoteListFragment;
import org.routine_work.notepad.fragment.ViewNoteFragment;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteSearchQueryParser;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

public class NotepadActivity extends Activity implements NotepadConstants,
	NoteControlCallback, NoteDetailFragment.NoteDetailEventCallback,
	SearchView.OnCloseListener, SearchView.OnQueryTextListener
{

	public static final String ACTION_QUIT = NotepadActivity.class.getPackage().getName() + ".ACTION_QUIT";
	public static final String NOTE_DETAIL_TAG = "NoteDetail";
	private static final String LOG_TAG = "simple-notepad";
	private String layout;
	private String layoutSinglePaneValue;
	private String layoutWideTwoPaneValue;
	private SearchView searchView;
	private String initialQueryString = null;

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
		Log.setIndentMode(true);
	}

	public static void quitApplication(Context context)
	{
		Log.v(LOG_TAG, "Hello");

		// Start this activity with ACTION_QUIT
		Intent quitIntent = new Intent(context, NotepadActivity.class);
		quitIntent.setAction(ACTION_QUIT);
		quitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(quitIntent);

		Log.v(LOG_TAG, "Bye");
	}

	public static void goHomeActivity(Context context)
	{
		Log.v(LOG_TAG, "Hello");

		// Start this activity with CLEAR_TOP flag.
		Intent intent = new Intent(context, NotepadActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_activity);

		Resources resources = getResources();
		layoutSinglePaneValue = resources.getString(R.string.note_list_layout_single_value);
		layoutWideTwoPaneValue = resources.getString(R.string.note_list_layout_wide_two_value);
		layout = NotepadPreferenceUtils.getNoteListLayout(this);

		// setup layout transition
		LayoutTransition layoutTransition = new LayoutTransition();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat((Object) null, "alpha", 0f, 1f);
		layoutTransition.setAnimator(LayoutTransition.APPEARING, fadeIn);
		layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
		layoutTransition.setDuration(LayoutTransition.APPEARING, 300);
		LinearLayout noteDetailContainer = (LinearLayout) findViewById(R.id.note_detail_container);
		noteDetailContainer.setLayoutTransition(layoutTransition);


		initializeWithIntent(getIntent());

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
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		super.onPause();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();
		reloadNoteList();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreateOptionsMenu(menu);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.search_notes_option_menu, menu);
		menuInflater.inflate(R.menu.add_note_option_menu, menu);
		menuInflater.inflate(R.menu.delete_notes_option_menu, menu);
		menuInflater.inflate(R.menu.preferences_option_menu, menu);
		menuInflater.inflate(R.menu.quit_option_menu, menu);

		// setup SearchView 
		MenuItem searchMenuItem = menu.findItem(R.id.search_notes_menuitem);
		searchView = (SearchView) searchMenuItem.getActionView();
		searchView.setIconifiedByDefault(true);
		searchView.setSubmitButtonEnabled(false);

		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);
		searchView.setQuery(initialQueryString, true);
		if (!TextUtils.isEmpty(initialQueryString))
		{
			searchView.setIconified(false);
		}
		initialQueryString = null;

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case android.R.id.home:
				if (searchView != null)
				{
					searchView.setQuery(null, false);
					searchView.setIconified(true);
				}
				closeNoteDetailFragment();
				break;
			case R.id.add_new_note_menuitem:
				startAddNote();
				break;
			case R.id.delete_notes_menuitem:
				startDeleteNotes();
				break;
			case R.id.search_notes_menuitem:
				onSearchRequested();
				break;
			case R.id.preferences_menuitem:
				startPreferencesActivity();
				break;
			case R.id.quit_menuitem:
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
				break;
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "requestCode => " + requestCode);
		Log.d(LOG_TAG, "resultCode => " + resultCode);


		if ((requestCode == REQUEST_CODE_ADD_NEW_NOTE)
			|| (requestCode == REQUEST_CODE_EDIT_NOTE))
		{
			reloadNoteList();
			closeNoteDetailFragment();
		}
		else if ((requestCode == REQUEST_CODE_DELETE_NOTE)
			|| (requestCode == REQUEST_CODE_DELETE_NOTES))
		{
			if (resultCode == RESULT_OK)
			{
				reloadNoteList();
				closeNoteDetailFragment();
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean result;
		Log.v(LOG_TAG, "Hello");

		if ((keyCode == KeyEvent.KEYCODE_BACK)
			&& searchView.isIconified() == false)
		{
			searchView.setQuery(null, false);
			searchView.setIconified(true);
			result = true;

		}
		else
		{
			result = super.onKeyDown(keyCode, event);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	// BEGIN ---------- SeachView.OnCloseListener ----------
	public boolean onClose()
	{
		Log.v(LOG_TAG, "Hello");

		if (searchView != null)
		{
			searchView.setQuery(null, true);
		}

		Log.v(LOG_TAG, "Bye");
		return false;
	}
	// END ---------- SeachView.OnCloseListener ----------

	// BEGIN ---------- SearchView.OnQueryTextListener ---------- 
	public boolean onQueryTextSubmit(String queryText)
	{
		Log.v(LOG_TAG, "Hello");

		IMEUtils.hideSoftKeyboardWindow(this, searchView);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	public boolean onQueryTextChange(String queryText)
	{
		Log.v(LOG_TAG, "Hello");

		doSeachWithQueryText(queryText);

		Log.v(LOG_TAG, "Bye");
		return true;
	}
	// END ---------- SearchView.OnQueryTextListener ---------- 

	@Override
	public void onNoteDetailStarted()
	{
		Log.v(LOG_TAG, "Hello");

		setEnableDetailView(true);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onNoteDetailStopped()
	{
		Log.v(LOG_TAG, "Hello");

		setEnableDetailView(false);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void startViewNote(Uri uri)
	{
		Log.v(LOG_TAG, "Hello");

		if (uri != null)
		{
			openNoteDetailFragment(uri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void startEditNote(Uri uri)
	{
		Log.v(LOG_TAG, "Hello");

		if (uri != null)
		{
			Intent editNoteIntent = new Intent(Intent.ACTION_EDIT, uri);
			startActivityForResult(editNoteIntent, REQUEST_CODE_EDIT_NOTE);
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void startDeleteNote(Uri uri)
	{
		Log.v(LOG_TAG, "Hello");

		if (uri != null)
		{
			Intent editNoteIntent = new Intent(Intent.ACTION_DELETE, uri);
			startActivityForResult(editNoteIntent, REQUEST_CODE_DELETE_NOTE);
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void startAddNote()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_ADD_NEW_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void startDeleteNotes()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(this, DeleteNotesActivity.class);
		intent.setAction(Intent.ACTION_DELETE);
		startActivityForResult(intent, REQUEST_CODE_DELETE_NOTES);

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

		closeNoteDetailFragment();

		String action = intent.getAction();
		if (ACTION_QUIT.equals(action))
		{
			finish();
		}
		else if (Intent.ACTION_SEARCH.equals(action))
		{
			setSearchQueryString(intent.getStringExtra(SearchManager.QUERY));
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
						String noteIdString = data.getLastPathSegment();
						Log.d(LOG_TAG, "open note : noteIdString => " + noteIdString);
						setSearchQueryString("id:" + noteIdString);
						processed = true;
					}
				}

				if (!processed)
				{
					setSearchQueryString(intent.getStringExtra(SearchManager.QUERY));
				}
			}
		}
		else
		{
			setSearchQueryString(null);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void setSearchQueryString(String queryString)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "queryString => " + queryString);
		if (searchView != null)
		{
			Log.d(LOG_TAG, "set query string to searchView");
			searchView.setQuery(queryString, true);
			if (!TextUtils.isEmpty(queryString))
			{
				searchView.setIconified(false);
			}
		}
		else
		{
			Log.d(LOG_TAG, "set query string to initialQueryString");
			initialQueryString = queryString;
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void doSeachWithQueryText(String queryString)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "queryString => " + queryString);

		Uri contentUri = NoteSearchQueryParser.parseQuery(queryString);

		FragmentManager fm = getFragmentManager();
		NoteListFragment noteListFragment = (NoteListFragment) fm.findFragmentById(R.id.note_list_fragment);
		if (noteListFragment != null)
		{
			Log.d(LOG_TAG, "noteListFragment => " + noteListFragment);
			noteListFragment.setContentUri(contentUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void setEnableDetailView(boolean detailView)
	{
		int listVisibility = View.VISIBLE;
		int detailVisibility = View.GONE;
		if (detailView)
		{
			detailVisibility = View.VISIBLE;
			if (layoutSinglePaneValue.equals(layout))
			{
				listVisibility = View.GONE;
			}
		}

		View noteDetailContainer = findViewById(R.id.note_detail_container);
		Log.v(LOG_TAG, "noteDetailContainer => " + noteDetailContainer);
		if (noteDetailContainer != null)
		{
			noteDetailContainer.setVisibility(detailVisibility);
		}
		else
		{
			Log.e(LOG_TAG, "noteDetailContainer is not found.");
		}

		View noteListFragment = findViewById(R.id.note_list_fragment);
		if (noteListFragment != null)
		{
			noteListFragment.setVisibility(listVisibility);
		}
		else
		{
			Log.e(LOG_TAG, "noteListFragment is not found.");
		}
	}

	private void reloadNoteList()
	{
		Log.v(LOG_TAG, "Hello");

		FragmentManager fm = getFragmentManager();
		NoteListFragment noteListFragment = (NoteListFragment) fm.findFragmentById(R.id.note_list_fragment);
		if (noteListFragment != null)
		{
			noteListFragment.reload();
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void openNoteDetailFragment(Uri noteUri)
	{
		Log.v(LOG_TAG, "Hello");

		if (noteUri != null)
		{
			FragmentManager fm = getFragmentManager();
			ViewNoteFragment viewNoteFragment = (ViewNoteFragment) fm.findFragmentByTag(NOTE_DETAIL_TAG);
			Log.d(LOG_TAG, "viewFragment => " + viewNoteFragment);
			if (viewNoteFragment == null)
			{
				viewNoteFragment = new ViewNoteFragment();
				Log.d(LOG_TAG, "ViewNoteFragment is created now. viewFragment => " + viewNoteFragment);

				// add the Fragment to the container
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.note_detail_container, viewNoteFragment, NOTE_DETAIL_TAG);
				ft.addToBackStack(null);
				ft.commit();

				viewNoteFragment.setNoteUri(noteUri);
			}
			else
			{
				viewNoteFragment.setNoteUri(noteUri);
				viewNoteFragment.loadNote();
			}

			searchView.clearFocus();
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void closeNoteDetailFragment()
	{
		Log.v(LOG_TAG, "Hello");

		FragmentManager fm = getFragmentManager();
		Fragment noteDetailFragment = fm.findFragmentByTag(NOTE_DETAIL_TAG);
		Log.d(LOG_TAG, "noteDetailFragment => " + noteDetailFragment);
		if (noteDetailFragment instanceof ViewNoteFragment)
		{
			if (noteDetailFragment.isAdded())
			{
				Log.d(LOG_TAG, "popBackStack()");
				fm.popBackStack();
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void startPreferencesActivity()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(this, NotepadPreferenceActivity.class);
		startActivity(intent);

		Log.v(LOG_TAG, "Bye");
	}
}
