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
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import org.routine_work.notepad.NotepadConstants;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class ViewNoteFragment extends NoteDetailFragment implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private NoteControlCallback noteControlCallback;

	public ViewNoteFragment()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onAttach(Activity activity)
	{
		Log.v(LOG_TAG, "Hello");

		super.onAttach(activity);
		if (activity instanceof NoteControlCallback)
		{
			noteControlCallback = (NoteControlCallback) activity;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		View view = super.onCreateView(inflater, container, savedInstanceState);
		setEditable(false);

		Log.v(LOG_TAG, "Bye");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.edit_note_option_menu, menu);
//		menuInflater.inflate(R.menu.delete_note_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.edit_note_menuitem:
				Log.d(LOG_TAG, "edit_note_menuitem is clicked.");
				startEditNoteActivity();
				break;
			case R.id.delete_note_menuitem:
				Log.d(LOG_TAG, "delete_note_menuitem is clicked.");
				startDeleteNoteActivity();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void startEditNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null && isNoteItemUri(noteUri))
		{
			noteControlCallback.startEditNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null && isNoteItemUri(noteUri))
		{
			noteControlCallback.startDeleteNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isNoteItemUri(Uri uri)
	{
		ContentResolver contentResolver = getActivity().getContentResolver();
		String type = contentResolver.getType(uri);
		Log.d(LOG_TAG, "noteUri => " + uri + ", type => " + type);
		boolean result = NoteStore.NOTE_ITEM_CONTENT_TYPE.equals(type);

		return result;
	}
}
