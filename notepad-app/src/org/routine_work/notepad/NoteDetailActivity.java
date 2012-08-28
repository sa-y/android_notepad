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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

/**
 * <ul> <li>View note</li> <li>Edit note</li> <li>Delete note</li> </ul>
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteDetailActivity extends Activity
	implements View.OnClickListener, OnFocusChangeListener,
	DialogInterface.OnClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_CURRENT_NOTE_URI = "currentNoteUri";
	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private static final String SAVE_KEY_CURRENT_NOTE = "currentNote";
	private static final int DIALOG_ID_LOCK = 0;
	private static final int DIALOG_ID_UNLOCK = 1;
	// ActionBar items
	private ViewGroup actionBarContainer;
	private TextView titleTextView;
	private ImageButton homeImageButton;
	private ImageButton addNewNoteImageButton;
	private ImageButton editNoteImageButton;
	private ImageButton deleteNoteImageButton;
	// Mode Edit
	private ViewGroup noteEditContainer;
	private EditText noteTitleEditText;
	private ImageButton noteTitleLockImageButton;
	private ImageButton noteTitleUnlockImageButton;
	private EditText noteContentEditText;
	// Mode View
	private ViewGroup noteViewContainer;
	private TextView noteTitleTextView;
	private TextView noteContentTextView;
	// Dialogs
	private Dialog lockDialog;
	private Dialog unlockDialog;
	// data
	private String currentAction;
	private Uri currentNoteUri;
	private final Note currentNote = new Note();
	private final Note originalNote = new Note();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		// When software keyboard was displayed, the window is adjust resize.
//		getWindow().setSoftInputMode(
//			WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//			| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		setContentView(R.layout.note_detail_activity);

		// ActionBar
		actionBarContainer = (ViewGroup) findViewById(R.id.actionbar_container);

		// ActionBar Items
		titleTextView = (TextView) findViewById(R.id.title_textview);
		homeImageButton = (ImageButton) findViewById(R.id.home_button);
		addNewNoteImageButton = (ImageButton) findViewById(R.id.add_new_note_button);
		editNoteImageButton = (ImageButton) findViewById(R.id.edit_note_button);
		deleteNoteImageButton = (ImageButton) findViewById(R.id.delete_note_button);
		homeImageButton.setOnClickListener(this);
		addNewNoteImageButton.setOnClickListener(this);
		editNoteImageButton.setOnClickListener(this);
		deleteNoteImageButton.setOnClickListener(this);

		// Edit Mode
		noteEditContainer = (ViewGroup) findViewById(R.id.note_edit_container);
		noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
		noteTitleLockImageButton = (ImageButton) findViewById(R.id.note_title_lock_button);
		noteTitleUnlockImageButton = (ImageButton) findViewById(R.id.note_title_unlock_button);
		noteContentEditText = (EditText) findViewById(R.id.note_content_edittext);

		noteTitleEditText.setOnFocusChangeListener(this);
		noteTitleEditText.setOnClickListener(this);
		noteTitleLockImageButton.setOnClickListener(this);
		noteTitleUnlockImageButton.setOnClickListener(this);
		noteContentEditText.setOnFocusChangeListener(this);

		// View Mode
		noteViewContainer = (ViewGroup) findViewById(R.id.note_view_container);
		noteTitleTextView = (TextView) findViewById(R.id.note_title_textview);
		noteContentTextView = (TextView) findViewById(R.id.note_content_textview);

		// process intent
		initWithIntent(savedInstanceState, getIntent());

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onSaveInstanceState(outState);
		saveNote();
		outState.putString(SAVE_KEY_CURRENT_ACTION, currentAction);
		outState.putParcelable(SAVE_KEY_CURRENT_NOTE_URI, currentNoteUri);
		outState.putSerializable(SAVE_KEY_CURRENT_NOTE, currentNote);
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		super.onNewIntent(intent);
		initWithIntent(null, intent);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		super.onPause();
		saveNote();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();
//		loadNoteFromContentProvider();
//		bindNoteToViews();
//		originalNote.copyFrom(currentNote);

		Log.d(LOG_TAG, "------------------------------------------------------------");
		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
		Log.d(LOG_TAG, "currentNote => " + currentNote);
		Log.d(LOG_TAG, "originalNote => " + originalNote);
		Log.d(LOG_TAG, "noteTitleEditText.text => " + noteTitleEditText.getText().toString());
		Log.d(LOG_TAG, "noteContentEditText.text => " + noteContentEditText.getText().toString());
		Log.d(LOG_TAG, "------------------------------------------------------------");

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
	protected void onStop()
	{
		Log.v(LOG_TAG, "Hello");
		super.onStop();
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog = null;
		Log.v(LOG_TAG, "Hello");

		switch (id)
		{
			case DIALOG_ID_LOCK:
				dialog = getLockDialog();
				break;
			case DIALOG_ID_UNLOCK:
				dialog = getUnlockDialog();
				break;
		}

		Log.v(LOG_TAG, "dialog => " + dialog);
		Log.v(LOG_TAG, "Bye");
		return dialog;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.add_new_note_option_menu, menu);
		menuInflater.inflate(R.menu.edit_note_option_menu, menu);
		menuInflater.inflate(R.menu.delete_note_option_menu, menu);
		menuInflater.inflate(R.menu.share_note_option_menu, menu);
		menuInflater.inflate(R.menu.quit_menu, menu);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "currentAction => " + currentAction);

		MenuItem addNewNoteMenuItem = menu.findItem(R.id.add_new_note_menuitem);
		MenuItem editNoteMenuItem = menu.findItem(R.id.edit_note_menuitem);
		MenuItem deleteNoteMenuItem = menu.findItem(R.id.delete_note_menuitem);
		MenuItem shareNoteMenuItem = menu.findItem(R.id.share_note_menuitem);
		if (Intent.ACTION_VIEW.equals(currentAction))
		{
			addNewNoteMenuItem.setVisible(true);
			editNoteMenuItem.setVisible(true);
			deleteNoteMenuItem.setVisible(true);
			shareNoteMenuItem.setVisible(true);
		}
		else if (Intent.ACTION_EDIT.equals(currentAction))
		{
			addNewNoteMenuItem.setVisible(true);
			editNoteMenuItem.setVisible(false);
			deleteNoteMenuItem.setVisible(false);
			shareNoteMenuItem.setVisible(false);
		}
		else if (Intent.ACTION_DELETE.equals(currentAction))
		{
			addNewNoteMenuItem.setVisible(false);
			editNoteMenuItem.setVisible(false);
			deleteNoteMenuItem.setVisible(false);
			shareNoteMenuItem.setVisible(false);
		}

		Log.v(LOG_TAG, "Bye");
		return super.onPrepareOptionsMenu(menu);
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
				Log.d(LOG_TAG, "add_new_note_menuitem selected.");
				startAddNewNoteActivity();
				break;
			case R.id.edit_note_menuitem:
				Log.d(LOG_TAG, "edit_note_menuitem selected.");
				startEditNoteActivity();
				break;
			case R.id.delete_note_menuitem:
				Log.d(LOG_TAG, "delete_note_menuitem selected.");
				startDeleteNoteActivity();
				break;
			case R.id.share_note_menuitem:
				Log.d(LOG_TAG, "share_note_menuitem selected.");
				NoteUtils.shareNote(this, currentNoteUri);
				break;
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
				setResultByModifiedFlag();
				finish();
				NotepadActivity.goHomeActivity(this);
				break;
			case R.id.add_new_note_button:
				Log.d(LOG_TAG, "Add Button is clicked.");
				startAddNewNoteActivity();
				break;
			case R.id.delete_note_button:
				Log.d(LOG_TAG, "Delete Button is clicked.");
				if (Intent.ACTION_DELETE.equals(currentAction))
				{
					deleteNote();
					setResult(Activity.RESULT_OK);
					finish();
				}
				else
				{
					startDeleteNoteActivity();
				}
				break;
			case R.id.edit_note_button:
				Log.d(LOG_TAG, "Edit Button is clicked.");
				startEditNoteActivity();
				break;
			case R.id.note_title_lock_button:
				Log.d(LOG_TAG, "note_title_lock_button is clicked.");
				showDialog(DIALOG_ID_LOCK);
				break;
			case R.id.note_title_unlock_button:
				Log.d(LOG_TAG, "note_title_unlock_button is clicked.");
				showDialog(DIALOG_ID_UNLOCK);
				break;
			case R.id.note_title_edittext:
				Log.d(LOG_TAG, "note_title_edittext is clicked.");
				noteContentEditText.clearFocus();
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	// DialogInterface.OnClickListener
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "dialog => " + dialog);
		Log.d(LOG_TAG, "which => " + which);

		if (which == Dialog.BUTTON_POSITIVE)
		{
			if (dialog == getLockDialog())
			{
				setNoteTitleLocked(true);
			}
			else if (dialog == getUnlockDialog())
			{
				setNoteTitleLocked(false);
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean result;
		Log.v(LOG_TAG, "Hello");

		Log.v(LOG_TAG, "keyCode => " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Log.v(LOG_TAG, "KEYCODE_BACK is down.");
			setResultByModifiedFlag();
			finish();
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
	public void onFocusChange(View v, boolean hasFocus)
	{
		Log.v(LOG_TAG, "Hello");

		switch (v.getId())
		{
			case R.id.note_title_edittext:
				if (hasFocus)
				{
					Log.d(LOG_TAG, "note_title_edittext has focus.");
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				break;
			case R.id.note_content_edittext:
				if (hasFocus)
				{
					Log.d(LOG_TAG, "note_content_edittext has focus.");
					actionBarContainer.setVisibility(View.GONE);
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				else
				{
					actionBarContainer.setVisibility(View.VISIBLE);
				}
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void setTitle(int titleId)
	{
		super.setTitle(titleId);
		if (titleTextView != null)
		{
			titleTextView.setText(titleId);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// 子アクティビティを FLAG_ACTIVITY_CLEAR_TOP で起動するからここには来ない?
		Log.v(LOG_TAG, "Hello");

		if (requestCode == REQUEST_CODE_EDIT_NOTE)
		{
			loadNoteFromContentProvider();
			originalNote.copyFrom(currentNote);
			updateViews();
		}

		Log.v(LOG_TAG, "Bye");
	}

	protected void initWithIntent(Bundle savedInstanceState, Intent intent)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.d(LOG_TAG, "------------------------------------------------------------");
		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
		Log.d(LOG_TAG, "currentNote => " + currentNote);
		Log.d(LOG_TAG, "originalNote => " + originalNote);
		Log.d(LOG_TAG, "noteTitleEditText.text => " + noteTitleEditText.getText().toString());
		Log.d(LOG_TAG, "noteContentEditText.text => " + noteContentEditText.getText().toString());
		Log.d(LOG_TAG, "------------------------------------------------------------");

		actionBarContainer.setVisibility(View.VISIBLE);

		// load saved instance
		String newAction = null;
		Uri newNoteUri = null;
		Log.d(LOG_TAG, "savedInstanceState => " + savedInstanceState);
		if (savedInstanceState != null)
		{
			// load currentAction
			newAction = savedInstanceState.getString(SAVE_KEY_CURRENT_ACTION);
			Log.d(LOG_TAG, "SAVE_KEY_CURRENT_ACTION => " + savedInstanceState.getString(SAVE_KEY_CURRENT_ACTION));

			// load currentNoteUri
			Parcelable noteUriObj = savedInstanceState.getParcelable(SAVE_KEY_CURRENT_NOTE_URI);
			Log.d(LOG_TAG, "SAVE_KEY_NOTE_URI => " + noteUriObj);
			if (noteUriObj instanceof Uri)
			{
				newNoteUri = (Uri) noteUriObj;
				Log.d(LOG_TAG, "newNoteUri => " + newNoteUri);
			}

			// load currentNote
			Object currentNoteObj = savedInstanceState.getSerializable(SAVE_KEY_CURRENT_NOTE);
			Log.d(LOG_TAG, "SAVE_KEY_NOTE => " + currentNoteObj);
			if (currentNoteObj instanceof Note)
			{
				Note note = (Note) currentNoteObj;
				currentNote.copyFrom(note);
				Log.d(LOG_TAG, "currentNote => " + currentNoteUri);
			}
		}

		if (newAction == null)
		{
			Log.d(LOG_TAG, "intent.action => " + intent.getAction());
			newAction = intent.getAction();
			if (newAction == null)
			{
				newAction = Intent.ACTION_EDIT;
			}
		}

		if (newNoteUri == null)
		{
			Log.d(LOG_TAG, "intent.data => " + intent.getData());
			newNoteUri = intent.getData();
			if (newNoteUri == null)
			{
				newNoteUri = currentNoteUri;
			}
		}

		Log.d(LOG_TAG, "newAction => " + newAction);
		Log.d(LOG_TAG, "newNoteUri => " + newNoteUri);

		if (Intent.ACTION_INSERT.equals(newAction))
		{
			Log.d(LOG_TAG, "ACTION_INSERT");
			currentNoteUri = NoteStore.Note.CONTENT_URI;
			currentNote.setTitle("");
			currentNote.setContent("");
			currentNote.setTitleLocked(false);

			// init default text
			String extraTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
			Log.d(LOG_TAG, "EXTRA_TITLE => " + extraTitle);
			if (extraTitle != null)
			{
				currentNote.setTitle(extraTitle);
			}

			String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
			Log.d(LOG_TAG, "EXTRA_TEXT => " + extraText);
			if (extraText != null)
			{
				currentNote.setContent(extraText);
			}

			boolean extraTitleLocked = intent.getBooleanExtra(EXTRA_TITLE_LOCKED, false);
			Log.d(LOG_TAG, "EXTRA_TITLE_LOCKED => " + extraTitleLocked);
			currentNote.setTitleLocked(extraTitleLocked);

			setTitle(R.string.add_new_note_title);
			addNewNoteImageButton.setVisibility(View.VISIBLE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.GONE);

			noteEditContainer.setVisibility(View.VISIBLE);
			noteViewContainer.setVisibility(View.GONE);

			newAction = Intent.ACTION_EDIT;

			if (TextUtils.isEmpty(extraTitle))
			{
				noteTitleEditText.requestFocus();
			}
			else
			{
				noteContentEditText.requestFocus();
			}
		}
		else if (Intent.ACTION_EDIT.equals(newAction))
		{
			Log.d(LOG_TAG, "ACTION_EDIT");
			if (NoteStore.isNoteItemUri(this, newNoteUri))
			{
				if (NoteStore.exist(getContentResolver(), newNoteUri))
				{
					currentNoteUri = newNoteUri;
					loadNoteFromContentProvider();
				}
				else
				{
					Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
					finish();
					return;
				}
			}
			else
			{
				currentNoteUri = newNoteUri;
				loadNoteFromViews();
			}

			setTitle(R.string.edit_note_title);
			addNewNoteImageButton.setVisibility(View.VISIBLE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.GONE);

			noteEditContainer.setVisibility(View.VISIBLE);
			noteViewContainer.setVisibility(View.GONE);
		}
		else if (Intent.ACTION_VIEW.equals(newAction))
		{
			Log.d(LOG_TAG, "ACTION_VIEW");
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

			currentNoteUri = newNoteUri;
			loadNoteFromContentProvider();

			setTitle(R.string.view_note_title);
			addNewNoteImageButton.setVisibility(View.GONE);
			editNoteImageButton.setVisibility(View.VISIBLE);
			deleteNoteImageButton.setVisibility(View.VISIBLE);

			noteEditContainer.setVisibility(View.GONE);
			noteViewContainer.setVisibility(View.VISIBLE);
		}
		else if (Intent.ACTION_DELETE.equals(newAction))
		{
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

			currentNoteUri = newNoteUri;
			loadNoteFromContentProvider();

			setTitle(R.string.delete_note_title);
			addNewNoteImageButton.setVisibility(View.GONE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.VISIBLE);

			noteEditContainer.setVisibility(View.GONE);
			noteViewContainer.setVisibility(View.VISIBLE);
		}

		currentAction = newAction;
		originalNote.copyFrom(currentNote);
		updateViews();

		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());
		Log.d(LOG_TAG, "------------------------------------------------------------");
		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
		Log.d(LOG_TAG, "currentNote => " + currentNote);
		Log.d(LOG_TAG, "originalNote => " + originalNote);
		Log.d(LOG_TAG, "noteTitleEditText.text => " + noteTitleEditText.getText().toString());
		Log.d(LOG_TAG, "noteContentEditText.text => " + noteContentEditText.getText().toString());
		Log.d(LOG_TAG, "------------------------------------------------------------");

		Log.v(LOG_TAG, "Bye");
	}

	private void setNoteTitleEditable(boolean editable)
	{
		Log.v(LOG_TAG, "Hello");

		noteTitleEditText.setEnabled(editable);
		noteTitleEditText.setFocusable(editable);
		noteTitleEditText.setFocusableInTouchMode(editable);

		Log.v(LOG_TAG, "Bye");
	}

	private void loadNoteFromContentProvider()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "currentNoteUri => " + currentNoteUri);

		if (NoteStore.isNoteItemUri(this, currentNoteUri))
		{
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(currentNoteUri, null, null, null, null);
			if (cursor != null)
			{
				try
				{
					if (cursor.moveToFirst())
					{
						int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
						int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
						int titleLockedIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE_LOCKED);

						String noteTitle = cursor.getString(titleIndex);
						String noteContent = cursor.getString(contentIndex);
						boolean noteTitleLocked = (cursor.getInt(titleLockedIndex) != 0);

						Log.d(LOG_TAG, "noteTitle => " + noteTitle);
						Log.d(LOG_TAG, "noteContent => " + noteContent);
						Log.d(LOG_TAG, "noteTitleLocked => " + noteTitleLocked);

						currentNote.setTitle(noteTitle);
						currentNote.setContent(noteContent);
						currentNote.setTitleLocked(noteTitleLocked);
					}
				}
				finally
				{
					cursor.close();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isModified()
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		if (Intent.ACTION_EDIT.equals(currentAction))
		{
			loadNoteFromViews();
			Log.d(LOG_TAG, "currentNote => " + currentNote);
			Log.d(LOG_TAG, "originalNote => " + originalNote);

			if (currentNote.equals(originalNote) == false)
			{
				result = true;
			}
		}

		Log.d(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void setResultByModifiedFlag()
	{
		Log.v(LOG_TAG, "Hello");
		if (isModified())
		{
			Log.d(LOG_TAG, "setResult(RESULT_OK)");
			setResult(RESULT_OK);
		}
		else
		{
			Log.d(LOG_TAG, "setResult(RESULT_CANCELED)");
			setResult(RESULT_CANCELED);
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void saveNote()
	{
		if (Intent.ACTION_EDIT.equals(currentAction))
		{
			if (isModified())
			{
				Log.d(LOG_TAG, "note is modified.");

				loadNoteFromViews();

				long now = System.currentTimeMillis();
				ContentValues values = new ContentValues();
				ContentResolver contentResolver = getContentResolver();
				if (NoteStore.isNoteItemUri(this, currentNoteUri))
				{
					// Update
					values.put(NoteStore.Note.Columns.TITLE, currentNote.getTitle());
					values.put(NoteStore.Note.Columns.CONTENT, currentNote.getContent());
					values.put(NoteStore.Note.Columns.TITLE_LOCKED, currentNote.isTitleLocked());
					values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);

					int updatedCount = contentResolver.update(currentNoteUri, values, null, null);
					Log.d(LOG_TAG, "updatedCount => " + updatedCount);
				}
				else
				{
					// Insert
					values.put(NoteStore.Note.Columns.TITLE, currentNote.getTitle());
					values.put(NoteStore.Note.Columns.CONTENT, currentNote.getContent());
					values.put(NoteStore.Note.Columns.TITLE_LOCKED, currentNote.isTitleLocked());
					values.put(NoteStore.Note.Columns.DATE_ADDED, now);
					values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);

					currentNoteUri = contentResolver.insert(NoteStore.Note.CONTENT_URI, values);
					Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
				}

				originalNote.copyFrom(currentNote);
				setResult(RESULT_OK);
			}
			else
			{
				Log.d(LOG_TAG, "note is not modified.");
			}
		}
		else
		{
			Log.d(LOG_TAG, "now not in edit mode.");
		}
	}

	private void deleteNote()
	{
		Log.v(LOG_TAG, "Hello");

		if (NoteStore.isNoteItemUri(this, currentNoteUri))
		{
			ContentResolver contentResolver = getContentResolver();
			int deletedCount = contentResolver.delete(currentNoteUri, null, null);
			Log.d(LOG_TAG, "deletedCount => " + deletedCount);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startAddNewNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

//		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Intent intent = new Intent(this, AddNewNoteActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startEditNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (NoteStore.isNoteItemUri(this, currentNoteUri))
		{
			Intent intent = new Intent(Intent.ACTION_EDIT, currentNoteUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
			Log.d(LOG_TAG, "intent => " + intent);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (NoteStore.isNoteItemUri(this, currentNoteUri))
		{
			Intent intent = new Intent(Intent.ACTION_DELETE, currentNoteUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, REQUEST_CODE_DELETE_NOTE);
			Log.d(LOG_TAG, "intent => " + intent);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void updateViews()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "currentAction => " + currentAction);
		if (Intent.ACTION_EDIT.equals(currentAction))
		{
			updateNoteEditTexts();
			updateNoteTitleLockedViews();
			updateNoteLockButtonsViews();
		}
		else
		{
			updateNoteTextViews();
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void updateNoteEditTexts()
	{
		if (noteTitleEditText != null)
		{
			noteTitleEditText.setText(currentNote.getTitle());
		}
		if (noteContentEditText != null)
		{
			noteContentEditText.setText(currentNote.getContent());
		}
	}

	private void updateNoteTextViews()
	{
		if (noteTitleTextView != null)
		{
			noteTitleTextView.setText(currentNote.getTitle());
		}
		if (noteContentTextView != null)
		{
			noteContentTextView.setText(currentNote.getContent());
		}
	}

	private void loadNoteFromViews()
	{
		if (Intent.ACTION_EDIT.equals(currentAction))
		{
			if (noteTitleEditText != null)
			{
				currentNote.setTitle(noteTitleEditText.getText().toString());
			}
			if (noteContentEditText != null)
			{
				currentNote.setContent(noteContentEditText.getText().toString());
			}
		}
	}

	// ToDo : update message and title
	private Dialog getLockDialog()
	{
		if (lockDialog == null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.lock);
			builder.setMessage(R.string.lock);
			builder.setPositiveButton(android.R.string.ok, this);
			builder.setNegativeButton(android.R.string.cancel, this);
			lockDialog = builder.create();
		}
		return lockDialog;
	}

	// ToDo : update message and title
	private Dialog getUnlockDialog()
	{
		if (unlockDialog == null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.unlock);
			builder.setMessage(R.string.unlock);
			builder.setPositiveButton(android.R.string.ok, this);
			builder.setNegativeButton(android.R.string.cancel, this);
			unlockDialog = builder.create();
		}
		return unlockDialog;
	}

	private void setNoteTitleLocked(boolean locked)
	{
		Log.d(LOG_TAG, "locked => " + locked);
		currentNote.setTitleLocked(locked);
		updateNoteTitleLockedViews();
		updateNoteLockButtonsViews();
	}

	private void updateNoteTitleLockedViews()
	{
		if (currentNote.isTitleLocked())
		{
			setNoteTitleEditable(false);
			noteContentEditText.requestFocus();
		}
		else
		{
			setNoteTitleEditable(true);
			noteTitleEditText.requestFocus();
		}
	}

	private void updateNoteLockButtonsViews()
	{
		if (currentNote.isTitleLocked())
		{
			noteTitleLockImageButton.setVisibility(View.GONE);
			noteTitleUnlockImageButton.setVisibility(View.VISIBLE);
		}
		else
		{
			noteTitleLockImageButton.setVisibility(View.VISIBLE);
			noteTitleUnlockImageButton.setVisibility(View.GONE);
		}
	}
}
