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

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplateInitializer extends IntentService
{

	private static final String LOG_TAG = "simple-notepad";

	public NoteTemplateInitializer(String name)
	{
		super(name);
	}

	public NoteTemplateInitializer()
	{
		this(NoteTemplateInitializer.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");
		ContentResolver contentResolver = getContentResolver();
		if (NoteStore.getNoteTemplateCount(contentResolver) == 0)
		{
			Log.d(LOG_TAG, "note template count is zero, insert initial template data.");
			Resources resources = getResources();
			ContentValues values = new ContentValues();
			String name, title, content;
			boolean titleLocked, editSameTitle;

			// blank note template
			name = resources.getString(R.string.template_blank_note_name);
			title = resources.getString(R.string.template_blank_note_title);
			content = resources.getString(R.string.template_blank_note_content);
			titleLocked = resources.getBoolean(R.bool.template_blank_note_title_locked);
			editSameTitle = resources.getBoolean(R.bool.template_blank_note_edit_same_title);
			values.put(NoteStore.NoteTemplate.Columns.NAME, name);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, title);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, content);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, titleLocked);
			values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, editSameTitle);
			contentResolver.insert(NoteStore.NoteTemplate.CONTENT_URI, values);

			// quick note template
			values.clear();
			name = resources.getString(R.string.template_quick_note_name);
			title = resources.getString(R.string.template_quick_note_title);
			content = resources.getString(R.string.template_quick_note_content);
			titleLocked = resources.getBoolean(R.bool.template_quick_note_title_locked);
			editSameTitle = resources.getBoolean(R.bool.template_quick_note_edit_same_title);
			values.put(NoteStore.NoteTemplate.Columns.NAME, name);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, title);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, content);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, titleLocked);
			values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, editSameTitle);
			contentResolver.insert(NoteStore.NoteTemplate.CONTENT_URI, values);

			// daily report template
			values.clear();
			name = resources.getString(R.string.template_daily_report_note_name);
			title = resources.getString(R.string.template_daily_report_note_title);
			content = resources.getString(R.string.template_daily_report_note_content);
			titleLocked = resources.getBoolean(R.bool.template_daily_report_note_title_locked);
			editSameTitle = resources.getBoolean(R.bool.template_daily_report_note_edit_same_title);
			values.put(NoteStore.NoteTemplate.Columns.NAME, name);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, title);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, content);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, titleLocked);
			values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, editSameTitle);
			contentResolver.insert(NoteStore.NoteTemplate.CONTENT_URI, values);

			// diary template
			values.clear();
			name = resources.getString(R.string.template_diary_note_name);
			title = resources.getString(R.string.template_diary_note_title);
			content = resources.getString(R.string.template_diary_note_content);
			titleLocked = resources.getBoolean(R.bool.template_diary_note_title_locked);
			editSameTitle = resources.getBoolean(R.bool.template_diary_note_edit_same_title);
			values.put(NoteStore.NoteTemplate.Columns.NAME, name);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, title);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, content);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, titleLocked);
			values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, editSameTitle);
			contentResolver.insert(NoteStore.NoteTemplate.CONTENT_URI, values);
		}
		Log.v(LOG_TAG, "Bye");
	}
}
