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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class NoteDetailFragment extends Fragment
		implements NotepadConstants, LoaderManager.LoaderCallbacks<Cursor>
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_NOTE_URI = "noteUri";
	protected TextView noteTitleTextView;
	protected TextView noteContentTextView;
	protected Uri noteUri;
	private NoteDetailEventCallback noteDetailEventCallback;

	public NoteDetailFragment()
	{
		super();
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	public void setNoteTitle(String noteTitle)
	{
		Log.v(LOG_TAG, "Hello");

		Log.v(LOG_TAG, "noteTitle => " + noteTitle);
		Log.v(LOG_TAG, "noteTitleTextView => " + noteTitleTextView);
		if (noteTitleTextView != null)
		{
			noteTitleTextView.setText(noteTitle);
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void setNoteContent(String noteContent)
	{
		Log.v(LOG_TAG, "Hello");

		Log.v(LOG_TAG, "noteContent => " + noteContent);
		Log.v(LOG_TAG, "noteContentTextView => " + noteContentTextView);
		if (noteContentTextView != null)
		{
			noteContentTextView.setText(noteContent);
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onAttach(@NonNull Context context)
	{
		Log.v(LOG_TAG, "Hello");

		super.onAttach(context);
		if (context instanceof NoteDetailEventCallback)
		{
			noteDetailEventCallback = (NoteDetailEventCallback) context;
		}

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
			Log.d(LOG_TAG, "Load savedInstanceState.noteUri => " + uri);
			if (uri instanceof Uri)
			{
				setNoteUri((Uri) uri);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		View v = inflater.inflate(R.layout.note_detail_fragment, container, false);

		noteTitleTextView = (TextView) v.findViewById(R.id.note_title_textview);
		noteContentTextView = (TextView) v.findViewById(R.id.note_content_textview);

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onViewCreated(view, savedInstanceState);

		Log.d(LOG_TAG, "LoaderManager.initLoader()");
		LoaderManager loaderManager = LoaderManager.getInstance(this);
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
	public void onResume()
	{
		Log.v(LOG_TAG, "Hello");
		super.onResume();

		int fontSize = NotepadPreferenceUtils.getNoteDetailFontSize(requireContext());
		int fontSizeDefault = NotepadPreferenceUtils.getNoteDetailFontSizeDefault(requireContext());
		int titleFontSize = (fontSize > fontSizeDefault) ? fontSize : fontSizeDefault;
		noteTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleFontSize);
		noteContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onSaveInstanceState(outState);
		Log.d(LOG_TAG, "Save outState.noteUri => " + noteUri);
		outState.putParcelable(SAVE_KEY_NOTE_URI, noteUri);

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

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "this.noteUri => " + this.noteUri);

		CursorLoader cursorLoader = null;
		if (NoteStore.isNoteItemUri(requireContext(), noteUri))
		{
			cursorLoader = new CursorLoader(requireContext(),
					noteUri, null, null, null, null);
		}

		Log.v(LOG_TAG, "cursorLoader => " + cursorLoader);
		Log.v(LOG_TAG, "Bye");
		// Note: return null might cause issues in some versions, but CursorLoader is expected.
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor)
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
				setNoteTitle(noteTitle);
				setNoteContent(noteContent);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");

		setNoteTitle(null);
		setNoteContent(null);

		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
}
