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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.routine_work.utils.Log;

public class DeleteNotesActivity extends Activity implements NotepadConstants
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_notes_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");
		super.onCreateOptionsMenu(menu);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_option_menu, menu);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		Log.d(LOG_TAG, "item.getItemId() => " + item.getItemId());
		switch (item.getItemId())
		{
			case android.R.id.home:
				NotepadActivity.goHomeActivity(this);
				finish();
				break;
			case R.id.quit_menuitem:
				NotepadActivity.quitApplication(this);
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}
}