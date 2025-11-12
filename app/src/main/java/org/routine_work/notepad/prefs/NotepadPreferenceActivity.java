/*
 *  The MIT License
 *
 *  Copyright 2011-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.routine_work.notepad.prefs;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.List;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
public class NotepadPreferenceActivity extends PreferenceActivity {

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		NotepadActivity.enableHomeButton(this);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		Log.v(LOG_TAG, "Hello");

		super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.notepad_preference_header, target);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_option_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;

		int itemId = item.getItemId();
        if (itemId == R.id.quit_menuitem) {
            NotepadActivity.quitApplication(this);
        } else if (itemId == android.R.id.home) {
            NotepadActivity.goHomeActivity(this);
        } else {
            result = super.onOptionsItemSelected(item);
        }

		return result;
	}

	private static final String[] VALID_FRAGMENT_NAMES = {
		"org.routine_work.notepad.prefs.DisplayPreferenceFragment",
		"org.routine_work.notepad.prefs.ShareDataPreferenceFragment",
		"org.routine_work.notepad.prefs.BackupAndResetPreferenceFragment",
		"org.routine_work.notepad.prefs.AboutAppPreferenceFragment"
	};

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected boolean isValidFragment(String fragmentName) {
		boolean result = false;
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "fragmentName => " + fragmentName);

		for (String validFragmentName : VALID_FRAGMENT_NAMES) {
			if (validFragmentName.equals(fragmentName)) {
				result = true;
				break;
			}
		}
		Log.v(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

}
