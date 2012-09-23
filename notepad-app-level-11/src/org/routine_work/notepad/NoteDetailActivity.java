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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import org.routine_work.notepad.fragment.DeleteNoteFragment;
import org.routine_work.notepad.fragment.EditNoteFragment;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class NoteDetailActivity extends Activity
	implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String FT_NOTE_EDIT = "FT_NOTE_EDIT";
	private static final String FT_NOTE_DELETE = "FT_NOTE_DELETE";
	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private String currentAction;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_detail_activity);

		// When software keyboard was displayed, the window is adjust resize.
//		getWindow().setSoftInputMode(
//			WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//			|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//		getWindow().setSoftInputMode(
//			WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//			| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		Log.v(LOG_TAG,
			"Hi!");
		Intent intent = getIntent();

		initWithIntent(intent, savedInstanceState);

		Log.v(LOG_TAG,
			"Bye");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		super.onNewIntent(intent);
		Log.d(LOG_TAG, "Hi!");
		initWithIntent(intent, null);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();

		FragmentManager fm = getFragmentManager();
		Fragment noteDetailFragment = fm.findFragmentByTag(FT_NOTE_EDIT);
		if (noteDetailFragment instanceof EditNoteFragment)
		{
			Log.d(LOG_TAG, "EditNoteFragment is already exist.");
			EditNoteFragment editNoteFragment = (EditNoteFragment) noteDetailFragment;
			editNoteFragment.loadNoteFromContentProvider();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		saveNote();
		super.onPause();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onSaveInstanceState(outState);
		outState.putString(SAVE_KEY_CURRENT_ACTION, currentAction);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onDestroy()
	{
		Log.v(LOG_TAG, "Hello");

		super.onDestroy();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
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

		switch (item.getItemId())
		{
			case android.R.id.home:
				Log.d(LOG_TAG, "home is clicked.");
				NotepadActivity.goHomeActivity(this);
				finish();
				break;
			case R.id.quit_menuitem:
				Log.d(LOG_TAG, "quit_menuitem is clicked.");
				NotepadActivity.quitApplication(this);
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void initWithIntent(Intent intent, Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		FragmentManager fm = getFragmentManager();

		Uri newNoteUri = null;
		String nextAction = null;
		if (savedInstanceState != null)
		{
			nextAction = savedInstanceState.getString(SAVE_KEY_CURRENT_ACTION);
			Log.d(LOG_TAG, "init action from savedInstanceState => " + nextAction);
		}

		if (nextAction == null)
		{
			nextAction = intent.getAction();
			Log.d(LOG_TAG, "init action from Intent => " + nextAction);
		}

		if (newNoteUri == null)
		{
			newNoteUri = intent.getData();
			Log.d(LOG_TAG, "init newNoteUri from Intent => " + newNoteUri);
		}

		Log.d(LOG_TAG, "nextAction => " + nextAction);
		Log.d(LOG_TAG, "newNoteUri => " + newNoteUri);

		if (Intent.ACTION_INSERT.equals(nextAction)
			|| Intent.ACTION_EDIT.equals(nextAction)
			|| Intent.ACTION_VIEW.equals(nextAction))
		{
			EditNoteFragment editNoteFragment;
			Fragment noteDetailFragment = fm.findFragmentByTag(FT_NOTE_EDIT);
			Log.d(LOG_TAG, "noteDetailFragment=> " + noteDetailFragment);
			if (noteDetailFragment instanceof EditNoteFragment)
			{
				Log.d(LOG_TAG, "EditNoteFragment is already exist.");
				editNoteFragment = (EditNoteFragment) noteDetailFragment;
			}
			else
			{
				Log.d(LOG_TAG, "EditNoteFragment is created now.");
				editNoteFragment = new EditNoteFragment();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.note_detail_container, editNoteFragment, FT_NOTE_EDIT);
				ft.commit();
			}
			Log.d(LOG_TAG, "editNoteFragment => " + editNoteFragment);
			Log.d(LOG_TAG, "editNoteFragment.noteUri => " + editNoteFragment.getNoteUri());

			if (Intent.ACTION_INSERT.equals(nextAction))
			{
				Log.d(LOG_TAG, "Insert note.");
				editNoteFragment.setNoteUri(NoteStore.Note.CONTENT_URI);

				String noteTitle = intent.getStringExtra(EXTRA_TITLE);
				String noteContent = intent.getStringExtra(EXTRA_TEXT);
				boolean noteTitleLocked = intent.getBooleanExtra(EXTRA_TITLE_LOCKED, false);
				Log.d(LOG_TAG, "noteTitle => " + noteTitle);
				Log.d(LOG_TAG, "noteContent => " + noteContent);
				Log.d(LOG_TAG, "noteTitleLocked => " + noteTitleLocked);
				editNoteFragment.setNoteContents(noteTitle, noteContent, noteTitleLocked);

				setTitle(R.string.add_new_note_title);
			}
			else if (Intent.ACTION_EDIT.equals(nextAction)
				|| Intent.ACTION_VIEW.equals(nextAction))
			{
				Log.d(LOG_TAG, "Edit note.");
				Log.d(LOG_TAG, "Edit newNoteUri => " + newNoteUri);
				Uri editNoteUri = editNoteFragment.getNoteUri();
				if (editNoteUri == null)
				{
					editNoteUri = newNoteUri;
				}

				if (NoteStore.isNoteItemUri(this, editNoteUri))
				{
					if (NoteStore.exist(getContentResolver(), editNoteUri))
					{
						editNoteFragment.setNoteUri(editNoteUri);
					}
					else
					{
						Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
						NotepadActivity.goHomeActivity(this);
						finish();
						return;
					}
				}

				String appendText = intent.getStringExtra(EXTRA_TEXT);
				if (TextUtils.isEmpty(appendText) == false)
				{
					editNoteFragment.setAppendText(appendText);
				}

				setTitle(R.string.edit_note_title);
			}

			nextAction = Intent.ACTION_EDIT;
		}
		else if (Intent.ACTION_DELETE.equals(nextAction))
		{
			DeleteNoteFragment deleteNoteFragment;
			Fragment noteDetailFragment = fm.findFragmentByTag(FT_NOTE_DELETE);
			Log.d(LOG_TAG, "noteDetailFragment => " + noteDetailFragment);
			if (noteDetailFragment instanceof DeleteNoteFragment)
			{
				Log.d(LOG_TAG, "DeleteNoteFragment is already exist.");
				deleteNoteFragment = (DeleteNoteFragment) noteDetailFragment;
			}
			else
			{
				Log.d(LOG_TAG, "DeleteNoteFragment is created now.");
				deleteNoteFragment = new DeleteNoteFragment();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.note_detail_container, deleteNoteFragment, FT_NOTE_DELETE);
				ft.commit();
			}

			Log.d(LOG_TAG, "Delete newNoteUri => " + newNoteUri);

			if (NoteStore.isNoteItemUri(this, newNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_specified, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			if (NoteStore.exist(getContentResolver(), newNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			deleteNoteFragment.setNoteUri(newNoteUri);
			setTitle(R.string.delete_note_title);
		}
		currentAction = nextAction;
		Log.d(LOG_TAG, "currentAction => " + currentAction);

		Log.v(LOG_TAG, "Bye");
	}

	private void saveNote()
	{
		if (Intent.ACTION_EDIT.equals(currentAction))
		{
			FragmentManager fm = getFragmentManager();
			Fragment noteDetailFragment = fm.findFragmentByTag(FT_NOTE_EDIT);
			if (noteDetailFragment instanceof EditNoteFragment)
			{
				Log.d(LOG_TAG, "EditNoteFragment is already exist.");
				EditNoteFragment editNoteFragment = (EditNoteFragment) noteDetailFragment;
				editNoteFragment.saveNote();
			}
		}
	}
}