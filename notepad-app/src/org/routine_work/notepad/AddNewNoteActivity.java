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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AddNewNoteActivity extends Activity
	implements NotepadConstants
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_note_activity);

		int noteTemplateCount = NoteStore.getNoteTemplateCount(getContentResolver());
		if (noteTemplateCount > 2)
		{
			Intent pickNoteTemplateIntent = new Intent(Intent.ACTION_PICK, NoteStore.NoteTemplate.CONTENT_URI);
			startActivityForResult(pickNoteTemplateIntent, REQUEST_CODE_PICK_NOTE_TEMPLATE);
		}
		else if (noteTemplateCount == 1)
		{
			startNoteDetailActivityWithTemplate(NoteStore.NoteTemplate.CONTENT_URI);
			finish();
		}
		else if (noteTemplateCount == 0)
		{
			startNoteDetailActivity();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE_PICK_NOTE_TEMPLATE && resultCode == RESULT_OK)
		{
			Uri noteTemplateUri = data.getData();
			startNoteDetailActivityWithTemplate(noteTemplateUri);
			finish();
		}
	}

	private void startNoteDetailActivityWithTemplate(Uri noteTemplateUri)
	{
		Cursor cursor = getContentResolver().query(noteTemplateUri,
			null, null, null, null);
		try
		{
			if (cursor != null && cursor.moveToFirst())
			{
				int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
				int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
				int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);
				int contentLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT_LOCKED);

				String title = cursor.getString(titleIndex);
				String content = cursor.getString(contentIndex);
				boolean titleLocked = (cursor.getInt(titleLockedIndex) != 0);
				boolean contentLocked = (cursor.getInt(contentLockedIndex) != 0);

				Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Intent.EXTRA_TITLE, title);
				intent.putExtra(Intent.EXTRA_TEXT, content);
				intent.putExtra(EXTRA_TITLE_LOCKED, titleLocked);
				intent.putExtra(EXTRA_CONTENT_LOCKED, contentLocked);
				startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
			}
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	private void startNoteDetailActivity()
	{
		Intent intent = new Intent(Intent.ACTION_INSERT, NoteStore.Note.CONTENT_URI);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
	}
}
