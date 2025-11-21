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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RestoreDatabaseActivity extends Activity implements OnClickListener
{
	private static final String LOG_TAG = "simple-notepad";
	static final int REQUEST_CODE_SELECT_STORAGE_FILE = 1001;

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		if (id == R.id.ok_button)
		{
			selectStorageFileToRestore();
		}
		else if (id == R.id.cancel_button)
		{
			finish();
			setResult(RESULT_CANCELED);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restore_database_activity);

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
			case REQUEST_CODE_SELECT_STORAGE_FILE:
				if (data != null && data.getData() != null)
				{
					Uri backupFileUri = data.getData();
					Log.d(LOG_TAG, "selected backupFileUri => " + backupFileUri);
					boolean success = restoreDatabaseFile(backupFileUri);
					if(success)
					{
						NotepadActivity.quitApplication(this);
					}
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	private void selectStorageFileToRestore()
	{
		Log.v(LOG_TAG, "Hello");

		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		startActivityForResult(intent, REQUEST_CODE_SELECT_STORAGE_FILE);

		Log.v(LOG_TAG, "Bye");
	}

	private boolean restoreDatabaseFile(Uri backupFileUri)
	{
		boolean result = false;
		Log.d(LOG_TAG, "backupFileUri => " + backupFileUri);

		if (backupFileUri == null)
		{
			Log.e(LOG_TAG, "backup file path is null, the restore was canceled.");
			return result;
		}

		if (!isSQLiteDatabase(backupFileUri)) {
			Log.e(LOG_TAG, "Selected file is not a valid SQLite database file: " + backupFileUri);
			Toast.makeText(this, R.string.selected_file_is_invalid, Toast.LENGTH_LONG).show();
			return result;
		}

		try (OutputStream outputStream = new FileOutputStream(NoteStore.getNoteDatabasePath(this));
			 InputStream inputStream = getContentResolver().openInputStream(backupFileUri))
		{
			if (outputStream != null && inputStream != null)
			{
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
			}
			result = true;
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "Failed to restore database file.", e);
			Toast.makeText(this, R.string.failed_to_restore_database_file, Toast.LENGTH_LONG).show();
		}

		return result;
	}

	private boolean isSQLiteDatabase(Uri uri)
	{
		// SQLite3ファイルのヘッダーシグネチャ ("SQLite format 3\0")
		final byte[] sqliteHeader = new byte[]{
				0x53, 0x51, 0x4c, 0x69, 0x74, 0x65, 0x20, 0x66, 0x6f,
				0x72, 0x6d, 0x61, 0x74, 0x20, 0x33, 0x00
		};
		byte[] fileHeader = new byte[sqliteHeader.length];

		try (InputStream inputStream = getContentResolver().openInputStream(uri))
		{
			if (inputStream == null)
			{
				return false;
			}

			// ファイルからヘッダー部分を読み込む
			int bytesRead = inputStream.read(fileHeader, 0, fileHeader.length);
			if (bytesRead == -1 ||  bytesRead < fileHeader.length)
			{
				return false;
			}
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "Failed to read file header for validation", e);
			return false;
		}

		// 読み込んだヘッダーとSQLiteのヘッダーシグネチャを比較
		for (int i = 0; i < fileHeader.length; i++)
		{
			if (fileHeader[i] != sqliteHeader[i])
			{
				return false;
			}
		}

		Log.d(LOG_TAG, "The file is a valid SQLite database.");
		return true;
	}
}
