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
import android.os.Environment;
import android.text.format.DateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.routine_work.notepad.R;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

public class BackupDatabaseService extends IntentService
	implements BackupConstants
{

	private static final String LOG_TAG = "simple-notepad";

	public BackupDatabaseService(String name)
	{
		super(name);
	}

	public BackupDatabaseService()
	{
		this("BackupDatabaseService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String externalStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(externalStorageState))
		{
			File databaseFilePath = NoteStore.getNoteDatabasePath(this);
			Log.d(LOG_TAG, "databaseFilePath => " + databaseFilePath);

			String backupFileName = DateFormat.format(BACKUP_FILE_DATE_FORMAT, System.currentTimeMillis()) + BACKUP_FILE_SUFFIX;
			File backupDirPath = Environment.getExternalStorageDirectory();
			backupDirPath = new File(backupDirPath, BACKUP_DIR_NAME);
			Log.d(LOG_TAG, "backupDirPath => " + backupDirPath);
			File backupFilePath = new File(backupDirPath, backupFileName);
			Log.d(LOG_TAG, "backupFilePath => " + backupFilePath);

			try
			{
				if (backupDirPath.exists() == false)
				{
					Log.i(LOG_TAG, "Create backup directory. backupDirPath => " + backupDirPath);
					boolean mkdirs = backupDirPath.mkdirs();
					Log.d(LOG_TAG, "mkdirs => " + mkdirs);
				}

				Log.i(LOG_TAG, "Backup database " + databaseFilePath + " to " + backupFilePath);
				FileChannel inputChannel = new FileInputStream(databaseFilePath).getChannel();
				FileChannel outputChannel = new FileOutputStream(backupFilePath).getChannel();
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
			Log.e(LOG_TAG, "The external storage is not mounted. : externalStorageState => " + externalStorageState);
		}
	}

}
