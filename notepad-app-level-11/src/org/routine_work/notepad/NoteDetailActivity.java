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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import org.routine_work.notepad.fragment.DeleteNoteFragment;
import org.routine_work.notepad.fragment.EditNoteFragment;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class NoteDetailActivity extends Activity
	implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String NOTE_DETAIL_TAG = "NoteDetail";
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
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		Log.v(LOG_TAG, "Hi!");
		Intent intent = getIntent();
		initWithIntent(intent, savedInstanceState);
		Log.v(LOG_TAG, "Bye");
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
		Fragment noteDetailFragment = fm.findFragmentByTag(NOTE_DETAIL_TAG);
		if (noteDetailFragment instanceof EditNoteFragment)
		{
			Log.d(LOG_TAG, "EditNoteFragment is already exist.");
			EditNoteFragment editNoteFragment = (EditNoteFragment) noteDetailFragment;
			editNoteFragment.loadNote();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		FragmentManager fm = getFragmentManager();
		Fragment noteDetailFragment = fm.findFragmentByTag(NOTE_DETAIL_TAG);
		if (noteDetailFragment instanceof EditNoteFragment)
		{
			Log.d(LOG_TAG, "EditNoteFragment is already exist.");
			EditNoteFragment editNoteFragment = (EditNoteFragment) noteDetailFragment;
			editNoteFragment.saveNote();
		}
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
		Log.d(LOG_TAG, "nextAction => " + nextAction);

		Uri data = intent.getData();
		Log.d(LOG_TAG, "intent.data => " + data);

		if (Intent.ACTION_INSERT.equals(nextAction)
			|| Intent.ACTION_EDIT.equals(nextAction)
			|| Intent.ACTION_VIEW.equals(nextAction))
		{
			EditNoteFragment editNoteFragment;
			Fragment noteDetailFragment = fm.findFragmentByTag(NOTE_DETAIL_TAG);
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
				ft.replace(R.id.note_detail_container, editNoteFragment, NOTE_DETAIL_TAG);
				ft.commit();
			}
			Log.d(LOG_TAG, "editNoteFragment => " + editNoteFragment);
			Log.d(LOG_TAG, "editNoteFragment.noteUri => " + editNoteFragment.getNoteUri());

			if (Intent.ACTION_INSERT.equals(nextAction))
			{
				Log.d(LOG_TAG, "Insert note.");
				editNoteFragment.setNoteUri(NoteStore.Note.CONTENT_URI);
				editNoteFragment.setNoteTitle(null);
				editNoteFragment.setNoteContent(null);
				String noteTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
				String noteText = intent.getStringExtra(Intent.EXTRA_TEXT);
				editNoteFragment.setInitialNoteTitle(noteTitle);
				editNoteFragment.setInitialNoteContent(noteText);
				setTitle(R.string.add_new_note_title);

				nextAction = Intent.ACTION_EDIT;
			}
			else if (Intent.ACTION_EDIT.equals(nextAction)
				|| Intent.ACTION_VIEW.equals(nextAction))
			{
				Log.d(LOG_TAG, "Edit note.");
				Uri nextNoteUri = data;
				Log.d(LOG_TAG, "Edit nextNoteUri => " + nextNoteUri);

				if (NoteStore.isNoteItemUri(this, nextNoteUri))
				{
					if (NoteStore.exist(getContentResolver(), nextNoteUri))
					{
						editNoteFragment.setNoteUri(nextNoteUri);
					}
					else
					{
						Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
						NotepadActivity.goHomeActivity(this);
						finish();
						return;
					}
				}
				setTitle(R.string.edit_note_title);
			}
		}
		else if (Intent.ACTION_DELETE.equals(nextAction))
		{
			DeleteNoteFragment deleteNoteFragment;
			Fragment noteDetailFragment = fm.findFragmentByTag(NOTE_DETAIL_TAG);
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
				ft.replace(R.id.note_detail_container, deleteNoteFragment, NOTE_DETAIL_TAG);
				ft.commit();
			}

			Uri nextNoteUri = deleteNoteFragment.getNoteUri();
			if (nextNoteUri == null)
			{
				nextNoteUri = data;
			}
			Log.d(LOG_TAG, "Delete nextNoteUri => " + nextNoteUri);

			if (NoteStore.isNoteItemUri(this, nextNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_specified, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			if (NoteStore.exist(getContentResolver(), nextNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			deleteNoteFragment.setNoteUri(nextNoteUri);
			setTitle(R.string.delete_note_title);
		}
		currentAction = nextAction;
		Log.d(LOG_TAG, "currentAction => " + currentAction);

		Log.v(LOG_TAG, "Bye");
	}
}