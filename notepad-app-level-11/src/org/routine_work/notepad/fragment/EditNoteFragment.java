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
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class EditNoteFragment extends NoteDetailFragment
	implements View.OnFocusChangeListener
{

	private static final String LOG_TAG = "simple-notepad";
	private String originalNoteTitle = "";
	private String originalNoteContent = "";
	private String initialNoteTitle;
	private String initialNoteContent;

	public EditNoteFragment()
	{
		super();
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void setNoteTitle(String noteTitle)
	{
		Log.v(LOG_TAG, "Hello");

		super.setNoteTitle(noteTitle);
		if (noteTitleEditText != null)
		{
			originalNoteTitle = noteTitleEditText.getText().toString();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void setNoteContent(String noteContent)
	{
		Log.v(LOG_TAG, "Hello");

		super.setNoteContent(noteContent);
		if (noteContentEditText != null)
		{
			originalNoteContent = noteContentEditText.getText().toString();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		View v;
		Log.v(LOG_TAG, "Hello");

		v = super.onCreateView(inflater, container, savedInstanceState);
		setEditable(true);
//		noteContentEditText.setOnFocusChangeListener(this);

		if (initialNoteTitle != null)
		{
			setNoteTitle(initialNoteTitle);
			initialNoteTitle = null;
		}
		if (initialNoteContent != null)
		{
			setNoteContent(initialNoteContent);
			initialNoteContent = null;
		}

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		super.onPause();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();
//		loadNote();
//
//		Log.d(LOG_TAG, "------------------------------------------------------------");
//		Log.d(LOG_TAG, "noteUri => " + noteUri);
//		Log.d(LOG_TAG, "noteTitle => " + noteTitleEditText.getText());
//		Log.d(LOG_TAG, "noteContent => " + noteContentEditText.getText());
//		Log.d(LOG_TAG, "originalNoteTitle => " + originalNoteTitle);
//		Log.d(LOG_TAG, "originalNoteContent => " + originalNoteContent);
//		Log.d(LOG_TAG, "------------------------------------------------------------");
//
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");

		saveNote();
		super.onSaveInstanceState(outState);

		Log.v(LOG_TAG, "Bye");
	}

	public void onFocusChange(View view, boolean focused)
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

	public boolean saveNote()
	{
		boolean saved = false;
		Log.v(LOG_TAG, "Hello");

		if (viewIsInflated)
		{

			if (isModified())
			{ // note is modified
				Log.d(LOG_TAG, "The note is modified, and save to DB.");

				String noteTitle = noteTitleEditText.getText().toString();
				String noteContent = noteContentEditText.getText().toString();

				ContentResolver contentResolver = getActivity().getContentResolver();
				String type = contentResolver.getType(noteUri);
				if (NoteStore.NOTE_ITEM_CONTENT_TYPE.equals(type))
				{
					// Update
					int updatedCount = NoteStore.updateNote(contentResolver, noteUri, noteTitle, noteContent);
					Log.d(LOG_TAG, "Updated : updatedCount => " + updatedCount);
				}
				else
				{
					// Insert 
					noteUri = NoteStore.insertNote(contentResolver, noteTitle, noteContent);
					Log.d(LOG_TAG, "Inserted : dataUri => " + noteUri);

				}

				originalNoteTitle = noteTitle;
				originalNoteContent = noteContent;
				saved = true;
			}
			else
			{
				Log.d(LOG_TAG, "The note is not modified.");
			}
		}

		Log.v(LOG_TAG, "Bye");
		return saved;
	}

	@Override
	public void loadNote()
	{
		Log.v(LOG_TAG, "Hello");

		super.loadNote();
		if (viewIsInflated)
		{
			noteTitleEditText.requestFocus();
			originalNoteTitle = noteTitleEditText.getText().toString();
			originalNoteContent = noteContentEditText.getText().toString();
		}

		Log.v(LOG_TAG, "Bye");
	}

	public String getInitialNoteContent()
	{
		return initialNoteContent;
	}

	public void setInitialNoteContent(String initialNoteContent)
	{
		this.initialNoteContent = initialNoteContent;
	}

	public String getInitialNoteTitle()
	{
		return initialNoteTitle;
	}

	public void setInitialNoteTitle(String initialNoteTitle)
	{
		this.initialNoteTitle = initialNoteTitle;
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
}
