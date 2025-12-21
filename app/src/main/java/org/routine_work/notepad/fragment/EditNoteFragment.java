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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.routine_work.notepad.R;
import org.routine_work.notepad.model.Note;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.template.NoteTemplatePickerDialogFragment;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

public class EditNoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnFocusChangeListener, View.OnClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_NOTE_URI = "noteUri";
	private static final String SAVE_KEY_CURRENT_NOTE = "currentNote";
	private static final String SAVE_KEY_ORIGINAL_NOTE = "originalNote";
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
	private String appendText;

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

		setHasOptionsMenu(true);
		actionBarAutoHide = NotepadPreferenceUtils.getActionBarAutoHide(requireContext());
		Log.d(LOG_TAG, "actionBarAutoHide => " + actionBarAutoHide);

		if (savedInstanceState != null)
		{
			Log.d(LOG_TAG, "Load data from savedInstanceState.uri");
			Parcelable uri = savedInstanceState.getParcelable(SAVE_KEY_NOTE_URI);
			if (uri instanceof Uri)
			{
				setNoteUri((Uri) uri);
			}

			Object currentNoteObj = savedInstanceState.getSerializable(SAVE_KEY_CURRENT_NOTE);
			if (currentNoteObj instanceof Note)
			{
				currentNote.copyFrom((Note) currentNoteObj);
			}

			Object originalNoteObj = savedInstanceState.getSerializable(SAVE_KEY_ORIGINAL_NOTE);
			if (originalNoteObj instanceof Note)
			{
				originalNote.copyFrom((Note) originalNoteObj);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onAttach(@NonNull Context context)
	{
		super.onAttach(context);
		Log.v(LOG_TAG, "Hello");

		if (context instanceof NoteDetailEventCallback)
		{
			noteDetailEventCallback = (NoteDetailEventCallback) context;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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

		// Update capitalization mode of the note title 
		int noteTitleInputType = InputType.TYPE_CLASS_TEXT;
		if (NotepadPreferenceUtils.getNoteTitleCapitalization(requireContext()))
		{
			noteTitleInputType |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
		}
		noteTitleEditText.setInputType(noteTitleInputType);

		// Update capitalization mode of the note content 
		int noteContentInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
		if (NotepadPreferenceUtils.getNoteContentCapitalization(requireContext()))
		{
			noteContentInputType |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
		}
		noteContentEditText.setInputType(noteContentInputType);

		updateNoteEditTexts();
		updateNoteTitleLockedViews();
		updateNoteLockButtonsViews();
		updateFocusedView();

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onViewCreated(view, savedInstanceState);

		if (NoteStore.isNoteItemUri(requireContext(), noteUri))
		{
			Log.d(LOG_TAG, "LoaderManager.initLoader()");
			LoaderManager loaderManager = LoaderManager.getInstance(this);
			loaderManager.initLoader(NOTE_LOADER_ID, null, this);
		}

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
	public void onResume()
	{
		Log.v(LOG_TAG, "Hello");
		super.onResume();

		int fontSize = NotepadPreferenceUtils.getNoteDetailFontSize(requireContext());
		int fontSizeDefault = NotepadPreferenceUtils.getNoteDetailFontSizeDefault(requireContext());
		int titleFontSize = Math.max(fontSize, fontSizeDefault);
		noteTitleEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleFontSize);
		noteContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		saveNote();

		super.onSaveInstanceState(outState);

		Log.d(LOG_TAG, "Save noteUri=> " + noteUri);
		outState.putParcelable(SAVE_KEY_NOTE_URI, noteUri);
		outState.putSerializable(SAVE_KEY_CURRENT_NOTE, currentNote);
		outState.putSerializable(SAVE_KEY_ORIGINAL_NOTE, originalNote);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		Log.v(LOG_TAG, "Hello");

		inflater.inflate(R.menu.add_note_option_menu, menu);
		inflater.inflate(R.menu.share_note_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		boolean result = true;

		int itemId = item.getItemId();
		if (itemId == R.id.add_new_note_menuitem)
		{
			Log.d(LOG_TAG, "add_new_note_menuitem is clicked.");
			startAddNewNoteActivity();
		}
		else if (itemId == R.id.share_note_menuitem)
		{
			Log.d(LOG_TAG, "share_note_menuitem is clicked.");
			startShareNoteActivity();
		}
		else
		{
			result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	// BEGIN : View.OnFocusChangeListener
	public void onFocusChange(View view, boolean focused)
	{
		int id = view.getId();
		if (id == R.id.note_title_edittext)
		{
			Log.v(LOG_TAG, "note_title_edittext : focused => " + focused);
			if (focused)
			{
				IMEUtils.showSoftKeyboardWindow(requireContext(), view);
			}
		}
		else if (id == R.id.note_content_edittext)
		{
			Log.v(LOG_TAG, "note_content_edittext : focused => " + focused);
			if (focused)
			{
				IMEUtils.showSoftKeyboardWindow(requireContext(), view);
			}

			if (actionBarAutoHide)
			{
				ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
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
		}
		else
		{
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

			ContentResolver contentResolver = requireActivity().getContentResolver();
			ContentValues values = new ContentValues();
			long now = System.currentTimeMillis();
			if (NoteStore.isNoteItemUri(requireContext(), noteUri))
			{
				// Update
				values.put(NoteStore.Note.Columns.TITLE, currentNote.getTitle());
				values.put(NoteStore.Note.Columns.CONTENT, currentNote.getContent());
				values.put(NoteStore.Note.Columns.TITLE_LOCKED, currentNote.isTitleLocked());
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

		LoaderManager loaderManager = LoaderManager.getInstance(this);
		Loader loader = loaderManager.getLoader(NOTE_LOADER_ID);
		Log.d(LOG_TAG, "loader => " + loader);

		Log.d(LOG_TAG, "noteUri => " + noteUri);
		if (NoteStore.isNoteItemUri(requireContext(), noteUri))
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
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteUri => " + noteUri);

		CursorLoader cursorLoader = new CursorLoader(requireContext(), noteUri, null, null, null, null);

		Log.v(LOG_TAG, "cursorLoader => " + cursorLoader);
		Log.v(LOG_TAG, "Bye");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor)
	{
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "requireActivity().isFinishing() => " + requireActivity().isFinishing());
		if (requireActivity().isFinishing() == false)
		{
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					Log.d(LOG_TAG, "The note loading is started.");
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
		}
		else
		{
			Log.d(LOG_TAG, "The note loading is cancelled.");
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");

//		setNoteContents("", "", false);
		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------

	// BEGIN ---------- View.OnClickListener----------
	public void onClick(View view)
	{
		int id = view.getId();
		if (id == R.id.note_title_lock_button)
		{
			Log.d(LOG_TAG, "note_title_lock_button is clicked");
			//LockTitleDialogFragment lockTitleDialogFragment = new LockTitleDialogFragment();
			//lockTitleDialogFragment.show(getFragmentManager(), FT_TITLE_LOCK);
			setNoteTitleLocked(true);
		}
		else if (id == R.id.note_title_unlock_button)
		{
			Log.d(LOG_TAG, "note_title_unlock_button is clicked");
			//UnlockTitleDialogFragment unlockTitleDialogFragment = new UnlockTitleDialogFragment();
			//unlockTitleDialogFragment.show(getFragmentManager(), FT_TITLE_UNLOCK);
			setNoteTitleLocked(false);
		}
		else
		{
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

	public String getAppendText()
	{
		return appendText;
	}

	public void setAppendText(String appendText)
	{
		this.appendText = appendText;
	}

	public void setNoteContents(String noteTitle, String noteContent, boolean noteTitleLocked)
	{
		this.setNoteContents(noteTitle, noteContent, noteTitleLocked, false);
	}

	public void setNoteContents(String noteTitle, String noteContent, boolean noteTitleLocked, boolean modified)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "noteTitle => " + noteTitle);
		Log.d(LOG_TAG, "noteContent => " + noteContent);
		Log.d(LOG_TAG, "noteTitleLocked => " + noteTitleLocked);
		Log.d(LOG_TAG, "modified => " + modified);
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

		// append text
		if (!TextUtils.isEmpty(appendText))
		{
			String newContent = currentNote.getContent() + "\n" + appendText;
			currentNote.setContent(newContent);
			appendText = null;
		}

		// setup originalNote
		if (modified)
		{
			originalNote.setTitle("");
			originalNote.setContent("");
		}
		else
		{
			originalNote.copyFrom(currentNote);
		}

		updateNoteEditTexts();
		updateNoteTitleLockedViews();
		updateNoteLockButtonsViews();
		updateFocusedView();
	}

	private void updateNoteEditTexts()
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
			if ((TextUtils.isEmpty(currentNote.getTitle()) == false) && (TextUtils.isEmpty(currentNote.getContent()) == true))
			{
//				IMEUtils.requestKeyboardFocus(noteContentEditText); // 
				IMEUtils.requestSoftKeyboardWindow(requireContext(), noteContentEditText);
			}
			else
			{
//				IMEUtils.requestKeyboardFocus(noteTitleEditText);
				IMEUtils.requestSoftKeyboardWindow(requireContext(), noteTitleEditText);
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

		if (!currentNote.equals(originalNote))
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

		int noteTemplateCount = NoteStore.getNoteTemplateCount(requireActivity().getContentResolver());
		Log.d(LOG_TAG, "noteTemplateCount => " + noteTemplateCount);
		if (noteTemplateCount >= 2)
		{
			NoteTemplatePickerDialogFragment dialogFragment = new NoteTemplatePickerDialogFragment();
			dialogFragment.show(getParentFragmentManager(), FT_NOTE_TEMPLATE_PICKER);
		}
		else if (noteTemplateCount == 1)
		{
			Intent intentForAddNewNoteWithFirstTemplate = NoteUtils.getIntentForAddNewNoteWithFirstTemplate(requireActivity());
			startActivity(intentForAddNewNoteWithFirstTemplate);
		}
		else
		{
			Intent intentForAddNewBlankNote = NoteUtils.getIntentForAddNewBlankNote(requireActivity());
			startActivity(intentForAddNewBlankNote);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startShareNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		String noteTitle = noteTitleEditText.getText().toString();
		String noteContent = noteContentEditText.getText().toString();
		NoteUtils.shareNote(requireActivity(), noteTitle, noteContent);

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
}
