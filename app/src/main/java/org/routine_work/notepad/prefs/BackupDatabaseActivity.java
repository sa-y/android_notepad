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
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BackupDatabaseActivity extends Activity
		implements OnClickListener, BackupConstants
{

	private static final String LOG_TAG = "simple-notepad";
	static final int REQUEST_CODE_SELECT_STORAGE_FILE = 1001;


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
	protected void onResume()
	{
		super.onResume();
		Log.v(LOG_TAG, "Hello");

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		if (id == R.id.ok_button)
		{
			selectBackupStorageFile();
		}
		else if (id == R.id.cancel_button)
		{
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(LOG_TAG, "Hello");
		if (requestCode == REQUEST_CODE_SELECT_STORAGE_FILE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				if (data != null && data.getData() != null)
				{
					Uri backupFileUri = data.getData();
					Log.v(LOG_TAG, "backupFileUri => " + backupFileUri);
					writeDatabaseBackupFile(backupFileUri);
					setResult(RESULT_OK);
					finish();
				}
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void writeDatabaseBackupFile(Uri backupFileUri)
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "backupFileUri => " + backupFileUri);
		try (OutputStream outputStream = getContentResolver().openOutputStream(backupFileUri);
			 InputStream inputStream = new FileInputStream(NoteStore.getNoteDatabasePath(this)))
		{
			Log.v(LOG_TAG, "outputStream => " + outputStream);
			Log.v(LOG_TAG, "inputStream => " + inputStream);
			if (outputStream != null && inputStream != null)
			{
				byte[] buffer = new byte[8192]; // 8KB バッファ
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
			}

		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "writeBinaryToUri", e);
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void selectBackupStorageFile()
	{
		Log.v(LOG_TAG, "Hello");
		String backupFileName = BACKUP_FILE_PREFIX
				+ DateFormat.format(BACKUP_FILE_DATE_FORMAT, System.currentTimeMillis())
				+ BACKUP_FILE_SUFFIX;

		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("application/octet-stream");
		intent.putExtra(Intent.EXTRA_TITLE, backupFileName);
		startActivityForResult(intent, REQUEST_CODE_SELECT_STORAGE_FILE);

		Log.v(LOG_TAG, "Bye");
	}

	private void startBackupDatabaseService(Uri storageFileUri)
	{
		Log.v(LOG_TAG, "Hello");

		if (PermissionUtils.hasExternalStoragePermission(this))
		{ // re check
			Intent backDatabaseIntent = new Intent(this, BackupDatabaseService.class);
			backDatabaseIntent.setData(storageFileUri);
			startService(backDatabaseIntent);
		}

		Log.v(LOG_TAG, "Bye");
	}

}
