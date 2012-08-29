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
import android.os.Bundle;
import android.view.*;
import org.routine_work.notepad.NotepadConstants;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class DeleteNoteFragment extends NoteDetailFragment implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	public DeleteNoteFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		View v;
		Log.v(LOG_TAG, "Hello");

		v = super.onCreateView(inflater, container, savedInstanceState);

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.delete_cancel_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.cancel_menuitem:
				Log.d(LOG_TAG, "cancel_menuitem");
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
				result = true;
				break;
			case R.id.delete_note_menuitem:
				Log.d(LOG_TAG, "delete_note_menuitem");
				deleteNote();
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void deleteNote()
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver contentResolver = getActivity().getContentResolver();
		String type = contentResolver.getType(noteUri);
		Log.d(LOG_TAG, "noteUri => " + noteUri);
		Log.d(LOG_TAG, "type => " + type);
		if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
		{
			contentResolver.delete(getNoteUri(), null, null);
		}

		Log.v(LOG_TAG, "Bye");
	}
}
