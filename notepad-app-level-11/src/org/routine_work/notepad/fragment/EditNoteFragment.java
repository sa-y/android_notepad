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
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
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
import org.routine_work.notepad.AddNewNoteActivity;
import org.routine_work.notepad.NotepadConstants;
import org.routine_work.notepad.R;
import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

public class EditNoteFragment extends Fragment
	implements LoaderManager.LoaderCallbacks<Cursor>,
	View.OnFocusChangeListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_NOTE_URI = "noteUri";
	// views
	protected EditText noteTitleEditText;
	protected EditText noteContentEditText;
	protected boolean viewIsInflated = false;
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
		viewIsInflated = true;

		updateNoteEditText();
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
				ActionBar actionBar = getActivity().getActionBar();
				if (actionBar != null)
				{
					if (focused)
					{
						IMEUtils.showSoftKeyboardWindow(getActivity(), view);
						actionBar.hide();
					}
					else
					{
						actionBar.show();
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
			if (NoteUtils.isNoteItemUri(getActivity(), noteUri))
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
		if (NoteUtils.isNoteItemUri(getActivity(), noteUri))
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

		if (NoteUtils.isNoteItemUri(getActivity(), noteUri))
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
				String noteTitle = cursor.getString(titleColumnIndex);
				String noteContent = cursor.getString(contentColumnIndex);
				Log.d(LOG_TAG, "noteTitle => " + noteTitle);
				Log.d(LOG_TAG, "noteContent => " + noteContent);
				setNoteContents(noteTitle, noteContent);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void onLoaderReset(Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");

		setNoteContents("", "");

		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------

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

	public void setNoteContents(String noteTitle, String noteContent)
	{
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
		originalNote.copyFrom(currentNote);

		updateNoteEditText();
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
				IMEUtils.requestKeyboardFocus(noteContentEditText);
			}
			else
			{
				Log.v(LOG_TAG, "noteTitleEditText#requestFocus()");
				IMEUtils.requestKeyboardFocus(noteTitleEditText);
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

		Intent intent = new Intent(getActivity(), AddNewNoteActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

		Log.v(LOG_TAG,
			"Bye");
	}

	private void startShareNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		String noteTitle = noteTitleEditText.getText().toString();
		String noteContent = noteContentEditText.getText().toString();
		NoteUtils.shareNote(getActivity(), noteTitle, noteContent);

		Log.v(LOG_TAG, "Bye");

	}
}
