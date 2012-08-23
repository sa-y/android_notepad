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
package org.routine_work.notepad;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.routine_work.notepad.utils.TimeFormatUtils;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteCursorAdapter extends SimpleCursorAdapter
	implements NotepadConstants, SimpleCursorAdapter.ViewBinder
{

	private static final String LOG_TAG = "simple-notepad";
	private Context context;
	private boolean checkable = false;

	public NoteCursorAdapter(Context context, Cursor c)
	{
		this(context, c, false);
	}

	public NoteCursorAdapter(Context context, Cursor c, boolean checkable)
	{
		super(context, R.layout.note_list_item_checkable, c, NOTE_LIST_MAPPING_FROM, NOTE_LIST_MAPPING_TO);
		this.checkable = checkable;
		this.context = context;
		setViewBinder(this);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		Log.v(LOG_TAG, "Hello");
		super.bindView(view, context, cursor);

		View checkbox = view.findViewById(android.R.id.checkbox);
		if (checkbox != null)
		{
			if (checkable)
			{
				checkbox.setVisibility(View.VISIBLE);
			}
			else
			{
				checkbox.setVisibility(View.GONE);
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	// SimpleCursorAdapter.ViewBinder
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	{
		boolean result = false;

		int viewId = view.getId();
		switch (viewId)
		{
			case R.id.note_modified_textview:
				long modifiedTime = cursor.getLong(columnIndex);
				String modifiedText = TimeFormatUtils.formatTime(context, modifiedTime);
				TextView modifiedTextView = (TextView) view;
				modifiedTextView.setText(modifiedText);
				result = true;
				break;
		}

		return result;
	}

	public boolean isCheckable()
	{
		return checkable;
	}

	public void setCheckable(boolean checkable)
	{
		this.checkable = checkable;
	}
}
