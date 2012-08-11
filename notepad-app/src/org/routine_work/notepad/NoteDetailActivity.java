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
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.*;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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
	NotepadConstants
{

	private static final String SAVE_KEY_CURRENT_NOTE_URI = "currentNoteUri";
	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private static final String LOG_TAG = "simple-notepad";
	// views
	private ViewGroup actionBarContainer;
	private TextView titleTextView;
	private ImageButton homeImageButton;
	private ImageButton addNewNoteImageButton;
	private ImageButton editNoteImageButton;
	private ImageButton deleteNoteImageButton;
	private EditText noteTitleEditText;
	private EditText noteContentEditText;
	// data
	private String originalNoteTitle = "";
	private String originalNoteContent = "";
	private String currentAction;
	private Uri currentNoteUri;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
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

		actionBarContainer = (ViewGroup) findViewById(R.id.actionbar_container);
		noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
		noteTitleEditText.setOnFocusChangeListener(this);
		noteContentEditText = (EditText) findViewById(R.id.note_content_edittext);
		noteContentEditText.setOnFocusChangeListener(this);

		titleTextView = (TextView) findViewById(R.id.title_textview);
		homeImageButton = (ImageButton) findViewById(R.id.home_button);
		addNewNoteImageButton = (ImageButton) findViewById(R.id.add_new_note_button);
		editNoteImageButton = (ImageButton) findViewById(R.id.edit_note_button);
		deleteNoteImageButton = (ImageButton) findViewById(R.id.delete_note_button);
		homeImageButton.setOnClickListener(this);
		addNewNoteImageButton.setOnClickListener(this);
		editNoteImageButton.setOnClickListener(this);
		deleteNoteImageButton.setOnClickListener(this);

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
		Log.d(LOG_TAG, "------------------------------------------------------------");
		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
		Log.d(LOG_TAG, "noteTitleEditText.text => " + noteTitleEditText.getText().toString());
		Log.d(LOG_TAG, "noteContentEditText.text => " + noteContentEditText.getText().toString());
		Log.d(LOG_TAG, "originalNoteTitle => " + originalNoteTitle);
		Log.d(LOG_TAG, "originalNoteContent => " + originalNoteContent);
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
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				break;
			case R.id.note_content_edittext:
				if (hasFocus)
				{
					Log.d(LOG_TAG, LOG_TAG);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// 子アクティビティを FLAG_ACTIVITY_CLEAR_TOP で起動するからここには来ない?
		Log.v(LOG_TAG, "Hello");

		if (requestCode == REQUEST_CODE_EDIT_NOTE)
		{
			loadNote();
		}

		Log.v(LOG_TAG, "Bye");
	}

	protected void initWithIntent(Bundle savedInstanceState, Intent intent)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);

		actionBarContainer.setVisibility(View.VISIBLE);
		// load saved note uri
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
			Log.d(LOG_TAG, "SAVE_KEY_NOTE_URI => " + savedInstanceState.getParcelable(SAVE_KEY_CURRENT_NOTE_URI));
			if (noteUriObj instanceof Uri)
			{
				newNoteUri = (Uri) noteUriObj;
				Log.d(LOG_TAG, "newNoteUri => " + newNoteUri);
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

		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());
		if (Intent.ACTION_INSERT.equals(newAction))
		{
			currentNoteUri = NoteStore.Note.CONTENT_URI;
			noteTitleEditText.setText(null);
			noteContentEditText.setText(null);

			// init default text
			String extraTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
			Log.d(LOG_TAG, "EXTRA_TITLE => " + extraTitle);
			if (extraTitle != null)
			{
				noteTitleEditText.setText(extraTitle);
			}

			String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
			Log.d(LOG_TAG, "EXTRA_TEXT => " + extraText);
			if (extraText != null)
			{
				noteContentEditText.setText(extraText);
			}

			titleTextView.setText(R.string.add_new_note_title);
			addNewNoteImageButton.setVisibility(View.VISIBLE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.GONE);

			newAction = Intent.ACTION_EDIT;
			setEditable(true);

			if (extraTitle == null)
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
			if (newNoteUri != null)
			{
				if (NoteStore.checkNote(getContentResolver(), newNoteUri))
				{
					currentNoteUri = newNoteUri;
					loadNote();
				}
				else
				{
					Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
					finish();
					return;
				}
			}

			titleTextView.setText(R.string.edit_note_title);
			addNewNoteImageButton.setVisibility(View.VISIBLE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.GONE);

			setEditable(true);
			noteTitleEditText.requestFocus();
		}
		else if (Intent.ACTION_VIEW.equals(newAction))
		{
			if (newNoteUri == null)
			{
				Toast.makeText(this, R.string.note_not_specified, Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			else if (NoteStore.checkNote(getContentResolver(), newNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			currentNoteUri = newNoteUri;
			loadNote();

			titleTextView.setText(R.string.view_note_title);
			addNewNoteImageButton.setVisibility(View.GONE);
			editNoteImageButton.setVisibility(View.VISIBLE);
			deleteNoteImageButton.setVisibility(View.VISIBLE);

			setEditable(false);
		}
		else if (Intent.ACTION_DELETE.equals(newAction))
		{
			if (newNoteUri == null)
			{
				Toast.makeText(this, R.string.note_not_specified, Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			if (NoteStore.checkNote(getContentResolver(), newNoteUri) == false)
			{
				Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			currentNoteUri = newNoteUri;
			loadNote();

			titleTextView.setText(R.string.delete_note_title);
			addNewNoteImageButton.setVisibility(View.GONE);
			editNoteImageButton.setVisibility(View.GONE);
			deleteNoteImageButton.setVisibility(View.VISIBLE);

			setEditable(false);
		}

		currentAction = newAction;
		originalNoteTitle = noteTitleEditText.getText().toString();
		originalNoteContent = noteContentEditText.getText().toString();
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.v(LOG_TAG, "Bye");
	}

	private void setEditable(boolean editable)
	{
		noteTitleEditText.setEnabled(editable);
		noteTitleEditText.setFocusable(editable);
		noteTitleEditText.setFocusableInTouchMode(editable);
		noteContentEditText.setEnabled(editable);
		noteContentEditText.setFocusable(editable);
		noteContentEditText.setFocusableInTouchMode(editable);
	}

	private void loadNote()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "currentNoteUri => " + currentNoteUri);

		ContentResolver contentResolver = getContentResolver();
		String type = contentResolver.getType(currentNoteUri);
		if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
		{
			Cursor cursor = contentResolver.query(currentNoteUri, null, null, null, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					int titleIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
					int contentIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
					String noteTitle = cursor.getString(titleIndex);
					String noteContent = cursor.getString(contentIndex);
					Log.d(LOG_TAG, "noteTitle => " + noteTitle);
					Log.d(LOG_TAG, "noteContent => " + noteContent);

					noteTitleEditText.setText(noteTitle);
					noteContentEditText.setText(noteContent);

					originalNoteTitle = noteTitleEditText.getText().toString();
					originalNoteContent = noteContentEditText.getText().toString();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isModified()
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		String noteTitle = noteTitleEditText.getText().toString();
		String noteContent = noteContentEditText.getText().toString();
		Log.d(LOG_TAG, "noteTitle => " + noteTitle);
		Log.d(LOG_TAG, "noteContent => " + noteContent);
		Log.d(LOG_TAG, "originalNoteTitle => " + originalNoteTitle);
		Log.d(LOG_TAG, "originalNoteContent => " + originalNoteContent);

		if ((noteTitle.equals(originalNoteTitle)
			&& noteContent.equals(originalNoteContent)) == false)
		{
			result = true;
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

		if (isModified())
		{
			Log.d(LOG_TAG, "note is modified.");

			String noteTitle = noteTitleEditText.getText().toString();
			String noteContent = noteContentEditText.getText().toString();

			ContentResolver contentResolver = getContentResolver();
			if (isNoteItemUri(currentNoteUri))
			{
				// Update
				int updatedCount = NoteStore.updateNote(contentResolver, currentNoteUri, noteTitle, noteContent);
				Log.d(LOG_TAG, "updatedCount => " + updatedCount);
			}
			else
			{
				// Insert
				currentNoteUri = NoteStore.insertNote(contentResolver, noteTitle, noteContent);
				Log.d(LOG_TAG, "currentNoteUri => " + currentNoteUri);
			}
			originalNoteTitle = noteTitle;
			originalNoteContent = noteContent;
			setResult(RESULT_OK);
		}
		else
		{
			Log.d(LOG_TAG, "note is not modified.");
		}

	}

	private void deleteNote()
	{
		Log.v(LOG_TAG, "Hello");

		if (isNoteItemUri(currentNoteUri))
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

		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra(Intent.EXTRA_TITLE, "New Note");
//		intent.putExtra(Intent.EXTRA_TEXT, "This is a text of new note.");
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startEditNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (isNoteItemUri(currentNoteUri))
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

		if (isNoteItemUri(currentNoteUri))
		{
			Intent intent = new Intent(Intent.ACTION_DELETE, currentNoteUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, REQUEST_CODE_DELETE_NOTE);
			Log.d(LOG_TAG, "intent => " + intent);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isNoteItemUri(Uri uri)
	{
		boolean result = false;

		ContentResolver contentResolver = getContentResolver();
		String type = contentResolver.getType(uri);
		Log.v(LOG_TAG, "uri => " + uri);
		Log.v(LOG_TAG, "type => " + type);
		if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
		{
			result = true;
		}
		Log.v(LOG_TAG, "isNoteItemUri => " + result);

		return result;
	}
}
