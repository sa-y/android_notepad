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

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
	public void onResume()
	{
		Log.v(LOG_TAG, "Hello");
		super.onResume();

		int fontSize = NotepadPreferenceUtils.getNoteDetailFontSize(this.getActivity());
		int fontSizeDefault = NotepadPreferenceUtils.getNoteDetailFontSizeDefault(this.getActivity());
		int titleFontSize = (fontSize > fontSizeDefault) ? fontSize : fontSizeDefault;
		noteTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleFontSize);
		noteContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
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

	public void loadNote()
	{
		Log.v(LOG_TAG, "Hello");

		LoaderManager loaderManager = getLoaderManager();
		Loader loader = loaderManager.getLoader(NOTE_LOADER_ID);
		Log.d(LOG_TAG, "loader => " + loader);

		String type = getActivity().getContentResolver().getType(noteUri);
		Log.d(LOG_TAG, "noteUri => " + noteUri);
		Log.d(LOG_TAG, "type => " + type);
		if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
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
		Log.d(LOG_TAG, "this.noteUri => " + this.noteUri);

		if (NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			cursorLoader = new CursorLoader(getActivity(),
				noteUri, null, null, null, null);
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
				setNoteTitle(noteTitle);
				setNoteContent(noteContent);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void onLoaderReset(Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");

		setNoteTitle(null);
		setNoteContent(null);

		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
}
