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
package org.routine_work.notepad.shortcut;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

public class CreateSearchNoteShortcutActivity extends Activity
	implements View.OnClickListener, NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	static
	{
		Log.setOutputLevel(Log.VERBOSE);
		Log.setTraceMode(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_search_note_shortcut_activity);
		setTitle(R.string.new_search_shortcut_title);

		// init views
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		Button okButton = (Button) findViewById(R.id.ok_button);
		cancelButton.setOnClickListener(this);
		okButton.setOnClickListener(this);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onClick(View view)
	{
		Log.v(LOG_TAG, "Hello");

		int viewId = view.getId();
		switch (viewId)
		{
			case R.id.cancel_button:
				Log.d(LOG_TAG, "Cancel Button is clicked.");
				setResult(Activity.RESULT_CANCELED);
				finish();
				break;
			case R.id.ok_button:
				Log.d(LOG_TAG, "OK Button is clicked.");
				createSearchNoteShortcut();
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void createSearchNoteShortcut()
	{
		Log.v(LOG_TAG, "Hello");

		// Check search qeury
		EditText searchQueryEditText = (EditText) findViewById(R.id.search_query_edittext);
		String searchQuery = searchQueryEditText.getText().toString();
		if (TextUtils.isEmpty(searchQuery))
		{
			Toast.makeText(this, R.string.no_search_query_message, Toast.LENGTH_LONG).show();
			return;
		}

		// Check shortcut name
		EditText shortcutNameEditText = (EditText) findViewById(R.id.shortcut_name_edittext);
		String shortcutName = shortcutNameEditText.getText().toString();
		if (TextUtils.isEmpty(shortcutName))
		{
			Toast.makeText(this, R.string.no_shortcut_name_message, Toast.LENGTH_LONG).show();
			return;
		}

		// Create shortcut
		Intent searchNoteIntent = new Intent(Intent.ACTION_SEARCH, NoteStore.Note.CONTENT_URI);
		searchNoteIntent.putExtra(SearchManager.QUERY, searchQuery);

		ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_notepad_search);

		Intent resultIntent = new Intent();
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, searchNoteIntent);
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();

		Log.v(LOG_TAG, "Bye");
	}
}
