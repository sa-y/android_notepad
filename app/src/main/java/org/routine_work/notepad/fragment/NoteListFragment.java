/*
 * The MIT License
 *
 * Copyright 2012-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

import java.util.Arrays;

public class NoteListFragment extends ListFragment
		implements NotepadConstants,
		LoaderManager.LoaderCallbacks<Cursor>,
		AbsListView.MultiChoiceModeListener
{

	private static final String LOG_TAG = "simple-notepad";
	private NoteCursorAdapter listAdapter;
	private Uri contentUri;
	private NoteControlCallback noteControlCallback;

	public NoteListFragment()
	{
		this.contentUri = NoteStore.Note.CONTENT_URI;
	}

	@Override
	public void onAttach(@NonNull Context context)
	{
		Log.v(LOG_TAG, "Hello");
		super.onAttach(context);

		if (context instanceof NoteControlCallback)
		{
			noteControlCallback = (NoteControlCallback) context;
		}
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		View v = inflater.inflate(R.layout.note_list_fragment, container, false);

		Log.v(LOG_TAG, "Bye");
		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onViewCreated(view, savedInstanceState);

		listAdapter = new NoteCursorAdapter(requireContext(), null, false);
		setListAdapter(listAdapter);

		LoaderManager loaderManager = LoaderManager.getInstance(this);
		loaderManager.initLoader(NOTE_LOADER_ID, null, this);

		ListView listView = getListView();
//		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//		listView.setMultiChoiceModeListener(this);

		registerForContextMenu(listView);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onDestroy()
	{
		Log.v(LOG_TAG, "Hello");

		super.onDestroy();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
									@Nullable ContextMenuInfo menuInfo)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == getListView())
		{
			MenuInflater menuInflater = requireActivity().getMenuInflater();
			menuInflater.inflate(R.menu.note_list_context_menu, menu);
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item)
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		if (menuInfo != null)
		{
			int itemId = item.getItemId();
			if (itemId == R.id.delete_note_menuitem)
			{
				deleteNote(menuInfo.id);
				result = true;
			}
			else if (itemId == R.id.edit_note_menuitem)
			{
				editNote(menuInfo.id);
				result = true;
			}
			else if (itemId == R.id.share_note_menuitem)
			{
				NoteUtils.shareNote(requireActivity(), menuInfo.id);
				result = true;
			}
			else if (itemId == R.id.copy_note_title_menuitem)
			{
				NoteUtils.copyNoteTitleToClipboard(requireActivity(), menuInfo.id);
				Toast.makeText(requireActivity(), R.string.copy_note_title_done, Toast.LENGTH_SHORT).show();
				result = true;
			}
			else if (itemId == R.id.copy_note_content_menuitem)
			{
				NoteUtils.copyNoteContentToClipboard(requireActivity(), menuInfo.id);
				Toast.makeText(requireActivity(), R.string.copy_note_content_done, Toast.LENGTH_SHORT).show();
				result = true;
			}
			else
			{
				result = super.onContextItemSelected(item);
			}
		}
		else
		{
			result = super.onContextItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");

		super.onListItemClick(l, v, position, id);
		showNoteDetail(id);
		getListView().requestFocus();

		Log.v(LOG_TAG, "Bye");
	}

	// BEGIN ---------- LoaderManager.LoaderCallbacks<Cursor> ----------
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "this.contentUri => " + this.contentUri);

		String where = NoteStore.Note.Columns.ENABLED + " = ?";
		String[] whereArgs
				= {
				"1"
		};
		String sortOrder = NotepadPreferenceUtils.getNoteListSortOrder(requireContext());
		Log.d(LOG_TAG, String.format("where => %s, whereArgs => %s, sortOrder => %s", where, Arrays.toString(whereArgs), sortOrder));
		CursorLoader cursorLoader = new CursorLoader(requireContext(),
				contentUri, null, where, whereArgs, sortOrder);

		Log.v(LOG_TAG, "Bye");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "cursor => " + cursor);
		listAdapter.swapCursor(cursor);
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader)
	{
		Log.v(LOG_TAG, "Hello");
		listAdapter.swapCursor(null);
		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- LoaderManager.LoaderCallbacks<Cursor> ----------

	// BEGIN ---------- AbsListView.MultiChoiceModeListener
	public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = requireActivity().getMenuInflater();
		menuInflater.inflate(R.menu.delete_note_option_menu, menu);
		actionMode.setTitle(R.string.delete_notes_title);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");
		listAdapter.setCheckable(true);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		if (menuItem.getItemId() == R.id.delete_note_menuitem)
		{
			deleteCheckedNotes();
			actionMode.finish();
			reload();
		}
		else
		{
			result = false;
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	public void onDestroyActionMode(ActionMode actionMode)
	{
		Log.v(LOG_TAG, "Hello");
		listAdapter.setCheckable(false);
		Log.v(LOG_TAG, "Bye");
	}
	// END ---------- AbsListView.MultiChoiceModeListener

	public void setContentUri(Uri contentUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "contentUri => " + contentUri);

		this.contentUri = contentUri;
		reload();

		Log.v(LOG_TAG, "Bye");
	}

	public void reload()
	{
		Log.v(LOG_TAG, "Hello");

		LoaderManager.getInstance(this).restartLoader(NOTE_LOADER_ID, null, this);

		Log.v(LOG_TAG, "Bye");
	}

	private void deleteNote(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		if (noteControlCallback != null)
		{
			noteControlCallback.startDeleteNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void showNoteDetail(long id)
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null)
		{
			Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
			noteControlCallback.startViewNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void editNote(long id)
	{
		Log.v(LOG_TAG, "Hello");

		Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
		if (noteControlCallback != null)
		{
			noteControlCallback.startEditNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void deleteCheckedNotes()
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver cr = requireActivity().getContentResolver();
		ListView listView = getListView();
		long[] checkItemIds = listView.getCheckItemIds();
		for (int i = checkItemIds.length - 1; i >= 0; i--)
		{
			long id = checkItemIds[i];
			Log.d(LOG_TAG, "delete note. i => " + i + ", id => " + id);
			Uri noteUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, id);
			cr.delete(noteUri, null, null);
		}

		Log.v(LOG_TAG, "Bye");
	}
}
