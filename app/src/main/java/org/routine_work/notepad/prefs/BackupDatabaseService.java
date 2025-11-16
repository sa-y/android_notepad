/*
 * The MIT License
 *
 * Copyright 2014 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
package org.routine_work.notepad.prefs;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BackupDatabaseService extends IntentService
		implements BackupConstants
{
	public final String STORAGE_FILE_URI_KEY = "STORAGE_FILE_URI_KEY ";

	private static final String LOG_TAG = "simple-notepad";
	private Handler handler;

	public BackupDatabaseService(String name)
	{
		super(name);
		handler = new Handler();
	}

	public BackupDatabaseService()
	{
		this("BackupDatabaseService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Uri backupFileUri = intent.getData();

		try (OutputStream outputStream = getContentResolver().openOutputStream(backupFileUri);
			 InputStream inputStream = new FileInputStream(NoteStore.getNoteDatabasePath(this)))
		{
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
	}

}
