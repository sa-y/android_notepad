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
package org.routine_work.notepad.debug;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class DummyNoteCreateService extends IntentService
{

	private static final String LOG_TAG = "simple-notepad";
	private static final int DATA_COUNT = 100;

	public DummyNoteCreateService(String name)
	{
		super(name);
	}

	public DummyNoteCreateService()
	{
		this(DummyNoteCreateService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		ContentResolver contentResolver = getContentResolver();
		ContentValues values = new ContentValues();

		for (int i = 0; i < DATA_COUNT; i++)
		{
			String title = String.format("Title : %08d", i);
			String content = String.format("Text : %08d", i);
			boolean titleLocked = (i % 3) == 0;
			long now = System.currentTimeMillis();
			values.clear();
			values.put(NoteStore.Note.Columns.TITLE, title);
			values.put(NoteStore.Note.Columns.CONTENT, content);
			values.put(NoteStore.Note.Columns.TITLE_LOCKED, titleLocked);
			values.put(NoteStore.Note.Columns.DATE_ADDED, now);
			values.put(NoteStore.Note.Columns.DATE_MODIFIED, now);
			contentResolver.insert(NoteStore.Note.CONTENT_URI, values);
		}

		Log.v(LOG_TAG, "Bye");
	}
}
