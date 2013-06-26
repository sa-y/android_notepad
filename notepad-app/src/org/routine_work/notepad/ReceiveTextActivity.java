/*
 * The MIT License
 *
 * Copyright 2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class ReceiveTextActivity extends Activity implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Log.d(LOG_TAG, "intent.action => " + intent.getAction());
		Log.d(LOG_TAG, "intent.type => " + intent.getType());
		Log.d(LOG_TAG, "intent.data => " + intent.getDataString());

		if (Intent.ACTION_SEND.equals(intent.getAction()))
		{
			String noteTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
			if (TextUtils.isEmpty(noteTitle))
			{
				noteTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT);
			}

			String noteContent = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (!TextUtils.isEmpty(noteContent))
			{
				Intent insertNoteIntent = new Intent(Intent.ACTION_INSERT);
				insertNoteIntent.setType(NoteStore.Note.NOTE_LIST_CONTENT_TYPE);
				insertNoteIntent.putExtra(EXTRA_TITLE, noteTitle);
				insertNoteIntent.putExtra(EXTRA_TEXT, noteContent);
				insertNoteIntent.putExtra(EXTRA_MODIFIED, true);
				startActivity(insertNoteIntent);
			}
		}

		finish();
		Log.v(LOG_TAG, "Bye");
	}
}
