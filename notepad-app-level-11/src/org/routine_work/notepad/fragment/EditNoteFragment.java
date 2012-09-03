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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import org.routine_work.notepad.R;
import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.template.NoteTemplatePickerDialogFragment;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

public class EditNoteFragment extends Fragment
	implements LoaderManager.LoaderCallbacks<Cursor>,
	View.OnFocusChangeListener,
	View.OnClickListener,
	NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_NOTE_URI = "noteUri";
	private static final String FT_TITLE_LOCK = "FT_TITLE_LOCK";
	private static final String FT_TITLE_UNLOCK = "FT_TITLE_UNLOCK";
	private static final String FT_NOTE_TEMPLATE_PICKER = "FT_NOTE_TEMPLATE_PICKER";
	// views
	private EditText noteTitleEditText;
	private EditText noteContentEditText;
	private ImageButton noteTitleLockImageButton;
	private ImageButton noteTitleUnlockImageButton;
	private boolean viewIsInflated = false;
	private boolean actionBarAutoHide;
	// callback listener
	private NoteDetailEventCallback noteDetailEventCallback;
	// model data
	private Uri noteUri;
	private final Note currentNote = new Note();
	private final Note originalNote = new Note();

	public EditNoteFragment()
	{
		super();
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
		{
			Parcelable uri = savedInstanceState.getParcelable(SAVE_KEY_NOTE_URI);
			Log.d(LOG_TAG, "Load savedInstanceState.uri => " + uri);
			if (uri instanceof Uri)
			{
				setNoteUri((Uri) uri);
			}
		}

		setHasOptionsMenu(true);
		actionBarAutoHide = NotepadPreferenceUtils.getActionBarAutoHide(getActivity());
		Log.d(LOG_TAG, "actionBarAutoHide => " + actionBarAutoHide);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onAttach(Activity activity)
	{
		Log.v(LOG_TAG, "Hello");

		super.onAttach(activity);
		if (activity instanceof NoteDetailEventCallback)
		{
			noteDetailEventCallback = (NoteDetailEventCallback) activity;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		View v = inflater.inflate(R.layout.edit_note_fragment, container, false);

		noteTitleEditText = (EditText) v.findViewById(R.id.note_title_edittext);
		noteContentEditText = (EditText) v.findViewById(R.id.note_content_edittext);
		noteContentEditText.setOnFocusChangeListener(this);
		noteTitleLockImageButton = (ImageButton) v.findViewById(R.id.note_title_lock_button);
		noteTitleUnlockImageButton = (ImageButton) v.findViewById(R.id.note_title_unlock_button);
		viewIsInflated = true;

		noteTitleLockImageButton.setOnClickListener(this);
		noteTitleUnlockImageButton.setOnClickListener(this);

		updateNoteEditText();
		updateNoteTitleLockedViews();
		updateNoteLockButtonsViews();
		updateFocusedView();

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onActivityCreated(savedInstanceState);

		Log.d(LOG_TAG, "LoaderManager.initLoader()");
		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(NOTE_LOADER_ID, null, this);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onStart()
	{
		Log.v(LOG_TAG, "Hello");

		super.onStart();

		if (noteDetailEventCallback != null)
		{
			noteDetailEventCallback.onNoteDetailStarted();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onStop()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteDetailEventCallback != null)
		{
			noteDetailEventCallback.onNoteDetailStopped();
		}

		super.onStop();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		saveNote();

		super.onSaveInstanceState(outState);

		Log.d(LOG_TAG, "Save noteUri=> " + noteUri);
		outState.putParcelable(SAVE_KEY_NOTE_URI, noteUri);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		Log.v(LOG_TAG, "Hello");

		inflater.inflate(R.menu.add_note_option_menu, menu);
		inflater.inflate(R.menu.share_note_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;

		switch (item.getItemId())
		{
			case R.id.add_new_note_menuitem:
				Log.d(LOG_TAG, "add_new_note_menuitem is clicked.");
				startAddNewNoteActivity();
				break;
			case R.id.share_note_menuitem:
				Log.d(LOG_TAG, "share_note_menuitem is clicked.");
				startShareNoteActivity();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	// BEGIN : View.OnFocusChangeListener
	public void onFocusChange(View view, boolean focused)
	{
		switch (view.getId())
		{
			case R.id.note_title_edittext:
				Log.v(LOG_TAG, "note_title_edittext : focused => " + focused);
				if (focused)
				{
					IMEUtils.showSoftKeyboardWindow(getActivity(), view);
				}
				break;
			case R.id.note_content_edittext:
				Log.v(LOG_TAG, "note_content_edittext : focused => " + focused);
				if (focused)
				{
					IMEUtils.showSoftKeyboardWindow(getActivity(), view);
				}

				if (actionBarAutoHide)
				{
					ActionBar actionBar = getActivity().getActionBar();
					if (actionBar != null)
					{
						if (focused)
						{
							actionBar.hide();
						}
						else
						{
							actionBar.show();
						}
					}
				}

				break;
			default:
				throw new AssertionError();
		}
	}
	// END : View.OnFocusChangeListener

	public boolean saveNote()
	{
		boolean saved = false;
		Log.v(LOG_TAG, "Hello");

		loadNoteFromViews();

		if (isModified())
		{ // note is modified
			Log.d(LOG_TAG, "The note is modified, and save to DB.");

			ContentResolver contentResolver = getActivity().getContentResolver();
			ContentValues values = new ContentValues();
			long now = System.currentTimeMillis();
			if (NoteStore.isNoteItemUri(getActivity(), noteUri))
			{
				// Update
				values.put(NoteStore.Note.Columns.TITLE, currentNote.getTitle());
				values.put(NoteStore.Note.Columns.CONTENT, currentNote.getContent());
				values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);
				int updatedCount = contentResolver.update(noteUri, values, null, null);
				Log.d(LOG_TAG, "Updated : updatedCount => " + updatedCount);
			}
			else
			{
				// Insert 
				values.put(NoteStore.Note.Columns.TITLE, currentNote.getTitle());
				values.put(NoteStore.Note.Columns.CONTENT, currentNote.getContent());
				values.put(NoteStore.Note.Columns.TITLE_LOCKED, currentNote.isTitleLocked());
				values.put(NoteStore.Note.Columns.DATE_ADDED, now);
				values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);
				noteUri = contentResolver.insert(NoteStore.Note.CONTENT_URI, values);
				Log.d(LOG_TAG, "Inserted : dataUri => " + noteUri);
			}

			originalNote.copyFrom(currentNote);
			saved = true;
		}
		else
		{
			Log.d(LOG_TAG, "The note is not modified.");
		}

		Log.v(LOG_TAG, "Bye");
		return saved;
	}

	public void loadNoteFromContentProvider()
	{
		Log.v(LOG_TAG, "Hello");

		LoaderManager loaderManager = getLoaderManager();
		Loader loader = loaderManager.getLoader(NOTE_LOADER_ID);
		Log.d(LOG_TAG, "loader => " + loader);

		Log.d(LOG_TAG, "noteUri => " + noteUri);
		if (NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			loaderManager.restartLoader(NOTE_LOADER_ID, null, this);
			Log.d(LOG_TAG, "restartLoader()");
		}
		else
		{
			loaderManager.destroyLoader(NOTE_LOADER_ID);
			Log.d(LOG_TAG, "destroyLoader()");
		}

		Log.v(LOG_TAG, "Bye");
	}

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		CursorLoader cursorLoader = null;

		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		if (NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			cursorLoader = new CursorLoader(getActivity(), noteUri,
				null, null, null, null);
		}

		Log.v(LOG_TAG, "cursorLoader => " + cursorLoader);
		Log.v(LOG_TAG, "Bye");
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		Log.v(LOG_TAG, "Hello");

		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				int titleColumnIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE);
				int contentColumnIndex = cursor.getColumnIndex(NoteStore.Note.Columns.CONTENT);
				int titleLockedColumnIndex = cursor.getColumnIndex(NoteStore.Note.Columns.TITLE_LOCKED);
				String noteTitle = cursor.getString(titleColumnIndex);
				String noteContent = cursor.getString(contentColumnIndex);
				boolean noteTitleLocked = cursor.getInt(titleLockedColumnIndex) != 0;
				Log.d(LOG_TAG, "noteTitle => " + noteTitle);
				Log.d(LOG_TAG, "noteContent => " + noteContent);
				Log.d(LOG_TAG, "noteTitleLocked => " + noteTitleLocked);
				setNoteContents(noteTitle, noteContent, noteTitleLocked);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void onLoaderReset(Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");

//		setNoteContents("", "", false);

		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------

	// BEGIN ---------- View.OnClickListener----------
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.note_title_lock_button:
				Log.d(LOG_TAG, "note_title_lock_button is clicked");
				LockTitleDialogFragment lockTitleDialogFragment = new LockTitleDialogFragment();
				lockTitleDialogFragment.show(getFragmentManager(), FT_TITLE_LOCK);
				break;
			case R.id.note_title_unlock_button:
				Log.d(LOG_TAG, "note_title_unlock_button is clicked");
				UnlockTitleDialogFragment unlockTitleDialogFragment = new UnlockTitleDialogFragment();
				unlockTitleDialogFragment.show(getFragmentManager(), FT_TITLE_UNLOCK);
				break;
			default:
				throw new AssertionError();
		}

	}
	// END ---------- View.OnClickListener----------

	public Uri getNoteUri()
	{
		return noteUri;
	}

	public void setNoteUri(Uri newNoteUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "newNoteUri => " + newNoteUri);

		noteUri = newNoteUri;

		Log.v(LOG_TAG, "Bye");
	}

	public void setNoteContents(String noteTitle, String noteContent, boolean noteTitleLocked)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteTitle => " + noteTitle);
		Log.d(LOG_TAG, "noteContent => " + noteContent);
		Log.d(LOG_TAG, "noteTitleLocked => " + noteTitleLocked);
		if (noteTitle == null)
		{
			noteTitle = "";
		}
		if (noteContent == null)
		{
			noteContent = "";
		}
		currentNote.setTitle(noteTitle);
		currentNote.setContent(noteContent);
		currentNote.setTitleLocked(noteTitleLocked);
		originalNote.copyFrom(currentNote);

		updateNoteEditText();
		updateNoteTitleLockedViews();
		updateNoteLockButtonsViews();
		updateFocusedView();
	}

	private void updateNoteEditText()
	{
		if (viewIsInflated)
		{
			noteTitleEditText.setText(currentNote.getTitle());
			noteContentEditText.setText(currentNote.getContent());
		}
	}

	private void updateFocusedView()
	{
		Log.v(LOG_TAG, "Hello");

		if (viewIsInflated)
		{
			if ((TextUtils.isEmpty(currentNote.getTitle()) == false)
				&& (TextUtils.isEmpty(currentNote.getContent()) == true))
			{
				Log.v(LOG_TAG, "noteContentEditText#requestFocus()");
				IMEUtils.requestSoftKeyboardWindow(getActivity(), noteContentEditText);
			}
			else
			{
				Log.v(LOG_TAG, "noteTitleEditText#requestFocus()");
				IMEUtils.requestSoftKeyboardWindow(getActivity(), noteTitleEditText);
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void loadNoteFromViews()
	{
		if (viewIsInflated)
		{
			String noteTitle = noteTitleEditText.getText().toString();
			String noteContent = noteContentEditText.getText().toString();
			currentNote.setTitle(noteTitle);
			currentNote.setContent(noteContent);
		}
	}

	private boolean isModified()
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "currentNote => " + currentNote);
		Log.d(LOG_TAG, "originalNote => " + originalNote);

		if (currentNote.equals(originalNote) == false)
		{
			result = true;
		}

		Log.d(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void startAddNewNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

//		Intent intent = new Intent(getActivity(), AddNewNoteActivity.class);
//		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		saveNote();
		NoteTemplatePickerDialogFragment dialogFragment = new NoteTemplatePickerDialogFragment();
		dialogFragment.show(getFragmentManager(), FT_NOTE_TEMPLATE_PICKER);

		Log.v(LOG_TAG, "Bye");
	}

	private void startShareNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		String noteTitle = noteTitleEditText.getText().toString();
		String noteContent = noteContentEditText.getText().toString();
		NoteUtils.shareNote(getActivity(), noteTitle, noteContent);

		Log.v(LOG_TAG, "Bye");

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
		if (viewIsInflated)
		{
			if (currentNote.isTitleLocked())
			{
				noteTitleEditText.clearFocus();
				noteTitleEditText.setEnabled(false);
				noteTitleEditText.setFocusable(false);
				noteTitleEditText.setFocusableInTouchMode(false);
//				IMEUtils.requestKeyboardFocus(noteContentEditText);
				noteContentEditText.requestFocus();
			}
			else
			{
				noteTitleEditText.setEnabled(true);
				noteTitleEditText.setFocusable(true);
				noteTitleEditText.setFocusableInTouchMode(true);
				noteTitleEditText.requestFocus();
//				IMEUtils.requestKeyboardFocus(noteTitleEditText);
				noteTitleEditText.requestFocus();
			}
		}
	}

	private void updateNoteLockButtonsViews()
	{
		if (viewIsInflated)
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

	class LockTitleDialogFragment extends DialogFragment
		implements DialogInterface.OnClickListener
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.lock_title);
			builder.setMessage(R.string.lock_title_confirm);
			builder.setPositiveButton(android.R.string.ok, this);
			builder.setNegativeButton(android.R.string.cancel, this);

			return builder.create();
		}

		public void onClick(DialogInterface di, int which)
		{
			if (which == Dialog.BUTTON_POSITIVE)
			{
				setNoteTitleLocked(true);
			}
		}
	}

	class UnlockTitleDialogFragment extends DialogFragment
		implements DialogInterface.OnClickListener
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.unlock_title);
			builder.setMessage(R.string.unlock_title_confirm);
			builder.setPositiveButton(android.R.string.ok, this);
			builder.setNegativeButton(android.R.string.cancel, this);

			return builder.create();
		}

		public void onClick(DialogInterface di, int which)
		{
			if (which == Dialog.BUTTON_POSITIVE)
			{
				setNoteTitleLocked(false);
			}
		}
	}
}
