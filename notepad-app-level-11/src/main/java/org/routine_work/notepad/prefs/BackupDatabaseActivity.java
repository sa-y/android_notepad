/*
 *  The MIT License
 *
 *  Copyright 2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.routine_work.notepad.R;

public class BackupDatabaseActivity extends Activity
	implements OnClickListener, BackupConstants
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_database_activity);

		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		switch (id)
		{
			case R.id.ok_button:
				startBackupDatabaseService();
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.cancel_button:
				finish();
				setResult(RESULT_CANCELED);
				break;
		}
	}

	private void startBackupDatabaseService()
	{
		Log.v(LOG_TAG, "Hello");

		Intent backDatabaseIntent = new Intent(this, BackupDatabaseService.class);
		startService(backDatabaseIntent);

		Log.v(LOG_TAG, "Bye");
	}
}
