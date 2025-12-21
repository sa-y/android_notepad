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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AddNewNoteActivity extends AppCompatActivity implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private final ActivityResultLauncher<Intent> pickTemplateLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
			{
				if (result.getResultCode() == RESULT_OK)
				{
					Intent intent = result.getData();
					if (intent != null)
					{
						Uri noteTemplateUri = intent.getData();
//						NoteUtils.startActivityToAddNewNoteWithTemplate(this, noteTemplateUri);
						Intent intentForAddNewNoteWithTemplate = NoteUtils.getIntentForAddNewNoteWithTemplate(this, noteTemplateUri);
						startActivity(intentForAddNewNoteWithTemplate);
					}
				}
				setResult(result.getResultCode());
				finish();
			});

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		// initialize noteTemplateUri
		Uri noteTemplateUri = null;
		Intent intent = getIntent();
		if (Intent.ACTION_INSERT.equals(intent.getAction()))
		{
			noteTemplateUri = intent.getData();
		}
		Log.d(LOG_TAG, "noteTemplateUri => " + noteTemplateUri);

		// create note or select template
		if (noteTemplateUri != null)
		{
			Intent intentForAddNewNoteWithTemplate = NoteUtils.getIntentForAddNewNoteWithTemplate(this, noteTemplateUri);
			startActivity(intentForAddNewNoteWithTemplate);
			finish();
		}
		else
		{
			int noteTemplateCount = NoteStore.getNoteTemplateCount(getContentResolver());
			if (noteTemplateCount >= 2)
			{
				Intent pickNoteTemplateIntent = new Intent(Intent.ACTION_PICK, NoteStore.NoteTemplate.CONTENT_URI);
				pickTemplateLauncher.launch(pickNoteTemplateIntent);
			}
			else if (noteTemplateCount == 1)
			{
				Intent intentForAddNewNoteWithFirstTemplate = NoteUtils.getIntentForAddNewNoteWithFirstTemplate(this);
				startActivity(intentForAddNewNoteWithFirstTemplate);
				finish();
			}
			else
			{
				Intent intentForAddNewBlankNote = NoteUtils.getIntentForAddNewBlankNote(this);
				startActivity(intentForAddNewBlankNote);
				finish();
			}
		}

		if (!isFinishing())
		{
			Log.v(LOG_TAG, "is not finishing.");
			Log.v(LOG_TAG, "Initialize UI.");
			setTheme(NotepadPreferenceUtils.getTheme(this));
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add_new_note_activity);
		}
		Log.v(LOG_TAG, "Bye");
	}
}
