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
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.routine_work.notepad.R;
import org.routine_work.notepad.utils.TimeFormatUtils;
import org.routine_work.utils.Log;
import org.routine_work.widget.CheckableLinearLayout;

/**
 *
 * @author sawai
 */
public class NoteListItemViewBinder
	implements SimpleCursorAdapter.ViewBinder
{

	private static final String LOG_TAG = "simple-notepad";
	private Context context;
	private boolean checkboxVisible = false;

	public NoteListItemViewBinder(Context context)
	{
		this.context = context;
	}

	public boolean isCheckboxVisible()
	{
		return checkboxVisible;
	}

	public void setCheckboxVisible(boolean checkboxVisible)
	{
		this.checkboxVisible = checkboxVisible;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	{
		boolean result = false;

		switch (view.getId())
		{
			case R.id.note_modified_textview:
				long modifiedTime = cursor.getLong(columnIndex);
				String modifiedText = TimeFormatUtils.formatTime(context, modifiedTime);
				TextView modifiedTextView = (TextView) view;
				modifiedTextView.setText(modifiedText);
				result = true;
				updateCheckedTextViewVisibility(view);
				break;
			case R.id.note_content_textview:
				String contentText = cursor.getString(columnIndex);
				contentText = contentText.replace('\n', ' ');
				TextView contentTextView = (TextView) view;
				contentTextView.setText(contentText);
				result = true;
				break;
		}

		return result;
	}

	private void updateCheckedTextViewVisibility(View view)
	{
		CheckableLinearLayout listItemView = findCheckableLinearLayout(view);
//		Log.d(LOG_TAG, "listItemView => " + listItemView);
		if (listItemView != null)
		{
			View foundView = listItemView.findViewById(android.R.id.checkbox);
			if (foundView instanceof CheckedTextView)
			{
				CheckedTextView checkedTextView = (CheckedTextView) foundView;
				if (isCheckboxVisible())
				{
					checkedTextView.setVisibility(View.VISIBLE);
				}
				else
				{
					checkedTextView.setVisibility(View.GONE);
				}
			}
		}

	}

	private CheckableLinearLayout findCheckableLinearLayout(View view)
	{
		CheckableLinearLayout result = null;
		View target = view;

		while ((target instanceof CheckableLinearLayout) == false)
		{
			target = (View) target.getParent();
		}

		if (target != null)
		{
			result = (CheckableLinearLayout) target;
		}

		return result;
	}
}
