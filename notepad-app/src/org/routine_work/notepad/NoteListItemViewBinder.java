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
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.routine_work.notepad.utils.TimeFormatUtils;

/**
 *
 * @author sawai
 */
public class NoteListItemViewBinder
	implements SimpleCursorAdapter.ViewBinder
{
	private static final String LOG_TAG = "simple-notepad";
	private Context context;

	public NoteListItemViewBinder(Context context)
	{
		this.context = context;
	}

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

}
