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
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
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

		if (NotepadPreferenceUtils.getNoteTitleAutoLink(getActivity()))
		{
			noteTitleTextView.setAutoLinkMask(Linkify.ALL);
		}
		else
		{
			noteTitleTextView.setAutoLinkMask(0);
		}

		if (NotepadPreferenceUtils.getNoteContentAutoLink(getActivity()))
		{
			noteContentTextView.setAutoLinkMask(Linkify.ALL);
		}
		else
		{
			noteContentTextView.setAutoLinkMask(0);
		}

		registerForContextMenu(noteTitleTextView);
		registerForContextMenu(noteContentTextView);

		Log.v(LOG_TAG, "Bye");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.edit_note_option_menu, menu);
		menuInflater.inflate(R.menu.share_note_option_menu, menu);

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
			case R.id.share_note_menuitem:
				Log.d(LOG_TAG, "share_note_menuitem is clicked.");
				startShareNoteActivity();
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater menuInflater = getActivity().getMenuInflater();
		switch (v.getId())
		{
			case R.id.note_title_textview:
				menuInflater.inflate(R.menu.note_title_context_menu, menu);
				break;
			case R.id.note_content_textview:
				menuInflater.inflate(R.menu.note_content_context_menu, menu);
				break;
			default:
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.edit_note_menuitem:
				Log.d(LOG_TAG, "edit_note_menuitem selected.");
				startEditNoteActivity();
				break;
			case R.id.copy_note_title_menuitem:
				Log.d(LOG_TAG, "copy_note_title_menuitem selected.");
				copyNoteTitleToClipboard();
				break;
			case R.id.copy_note_content_menuitem:
				Log.d(LOG_TAG, "copy_note_content_menuitem selected.");
				copyNoteContentToClipboard();
				break;
			default:
				result = super.onContextItemSelected(item);
		}

		Log.v(LOG_TAG, "Hello");
		return result;
	}

	private void startEditNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null
			&& NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			noteControlCallback.startEditNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null
			&& NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			noteControlCallback.startDeleteNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startShareNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");
		String noteTitle = noteTitleTextView.getText().toString();
		String noteContent = noteContentTextView.getText().toString();
		NoteUtils.shareNote(getActivity(), noteTitle, noteContent);
		Log.v(LOG_TAG, "Bye");
	}

	private void copyNoteTitleToClipboard()
	{
		ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
		clipboardManager.setText(noteTitleTextView.getText());
	}

	private void copyNoteContentToClipboard()
	{
		ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
		clipboardManager.setText(noteContentTextView.getText());
	}
}
