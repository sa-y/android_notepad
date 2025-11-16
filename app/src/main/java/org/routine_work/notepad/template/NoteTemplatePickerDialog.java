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
package org.routine_work.notepad.template;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteTemplateConstants;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplatePickerDialog extends Dialog
		implements OnItemClickListener,
		NoteTemplateConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private Cursor cursor;
	private SimpleCursorAdapter listAdapter;
	private ListView listView;
	private View emptyView;
	private AdapterView.OnItemClickListener onItemClickListener;

	public NoteTemplatePickerDialog(Context context)
	{
		super(context);
	}

	public OnItemClickListener getOnItemClickListener()
	{
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener)
	{
		Log.v(LOG_TAG, "Hello");
		this.onItemClickListener = itemClickListener;
		if (listView != null)
		{
			listView.setOnItemClickListener(itemClickListener);
		}
		Log.v(LOG_TAG, "Bye");
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");
		if (parent == listView)
		{
			NoteUtils.startActivityToAddNewNoteWithTemplate(getOwnerActivity(), id);
			dismiss();
		}
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_template_picker_dialog);
		setTitle(R.string.select_note_template_title);

		listView = (ListView) findViewById(android.R.id.list);
		emptyView = findViewById(android.R.id.empty);

		if (onItemClickListener != null)
		{
			listView.setOnItemClickListener(onItemClickListener);
		}
		else
		{
			listView.setOnItemClickListener(this);
		}

		// init List Adapter
		listAdapter = new SimpleCursorAdapter(getContext(),
				android.R.layout.simple_list_item_1, cursor,
				NOTE_TEMPLATE_LIST_MAPPING_FROM,
				NOTE_TEMPLATE_LIST_MAPPING_TO);

		listView.setAdapter(listAdapter);

		loadNoteTemplates();
		Log.d(LOG_TAG, "Bye");
	}

	@Override
	protected void onStart()
	{
		Log.d(LOG_TAG, "Hello");

		super.onStart();
		loadNoteTemplates();

		Log.d(LOG_TAG, "Hello");
	}

	@Override
	protected void onStop()
	{
		Log.d(LOG_TAG, "Hello");

		super.onStop();
		swapCursor(null);

		Log.d(LOG_TAG, "Bye");
	}

	private void loadNoteTemplates()
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver cr = getContext().getContentResolver();
		String where = NoteStore.Note.Columns.ENABLED + " = ?";
		String[] whereArgs =
				{
						"1"
				};
		Cursor c = cr.query(NoteStore.NoteTemplate.CONTENT_URI, null, where, whereArgs,
				NoteStore.NoteTemplate.Columns._ID + " ASC");
		if (c != null && c.moveToFirst())
		{
			swapCursor(c);
		}
		else
		{
			swapCursor(null);
		}

		updateVisibility();

		Log.v(LOG_TAG, "Bye");
	}

	private void swapCursor(Cursor newCursor)
	{
		listAdapter.changeCursor(newCursor);
		if (cursor != null)
		{
			cursor.close();
		}
		cursor = newCursor;
	}

	private void updateVisibility()
	{
		if (cursor != null)
		{
			listView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}
		else
		{
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}
}
