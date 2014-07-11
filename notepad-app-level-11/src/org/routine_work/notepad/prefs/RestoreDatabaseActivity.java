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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;

public class RestoreDatabaseActivity extends Activity implements OnClickListener
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String SAVE_KEY_BACKUP_FILE_PATH = "backupFilePath";
	private String backupFilePath;
	private static final int REUQEST_CODE_PICK_BACKUP_FILE = 1;

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		switch (id)
		{
			case R.id.ok_button:
				restoreDatabaseFile();
				NotepadActivity.quitApplication(this);
				break;
			case R.id.cancel_button:
				finish();
				setResult(RESULT_CANCELED);
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restore_database_activity);

		if (savedInstanceState != null)
		{
			backupFilePath = savedInstanceState.getString(SAVE_KEY_BACKUP_FILE_PATH);
		}
		else
		{
			Intent intent = getIntent();
			Uri data = intent.getData();
			if (data != null)
			{
				backupFilePath = data.getPath();
			}
		}
		Log.d(LOG_TAG, "backupFilePath => " + backupFilePath);

		if (backupFilePath == null)
		{
			Intent pickBackupFileIntent = new Intent(this, PickBackupFileActivity.class);
			startActivityForResult(pickBackupFileIntent, REUQEST_CODE_PICK_BACKUP_FILE);
		}

		// Init Views
		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REUQEST_CODE_PICK_BACKUP_FILE:
				if (resultCode == RESULT_OK)
				{
					Uri uri = data.getData();
					backupFilePath = uri.getPath();
				}
				Log.d(LOG_TAG, "backupFilePath => " + backupFilePath);

				if (backupFilePath == null)
				{
					setResult(RESULT_CANCELED);
					finish();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString(SAVE_KEY_BACKUP_FILE_PATH, backupFilePath);
	}

	private void restoreDatabaseFile()
	{
		Log.d(LOG_TAG, "backupFilePath => " + backupFilePath);
		if (backupFilePath == null)
		{
			Log.e(LOG_TAG, "backup file path is null, the restore was canceled.");
			return;
		}

		String externalStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(externalStorageState))
		{
			File databaseFilePath = NoteStore.getNoteDatabasePath(this);
			Log.d(LOG_TAG, "databaseFilePath => " + databaseFilePath);

			try
			{
				Log.i(LOG_TAG, "Restore database " + backupFilePath + " to " + databaseFilePath);
				FileChannel inputChannel = new FileInputStream(backupFilePath).getChannel();
				FileChannel outputChannel = new FileOutputStream(databaseFilePath).getChannel();
				inputChannel.transferTo(0, inputChannel.size(), outputChannel);
				inputChannel.close();
				outputChannel.close();
			}
			catch (IOException ex)
			{
				Log.e(LOG_TAG, "The database file copying is failed.", ex);
			}
		}
		else
		{
			String message = "The external storage is not mounted.";
			Log.e(LOG_TAG, message + "externalStorageState => " + externalStorageState);
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}
}
