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
package org.routine_work.notepad.prefs;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.routine_work.notepad.*;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class PickBackupFileActivity extends ListActivity
	implements AdapterView.OnItemClickListener, NotepadConstants, BackupConstants
{

	private static final String LOG_TAG = "simple-notepad";
	private static final String MAPPING_KEY_FILE_PATH = "FILE";
	private static final String MAPPING_KEY_FILE_NAME = "BASENAME";
	private static final String[] MAPPING_FROM = new String[]
	{
		MAPPING_KEY_FILE_NAME,
	};
	private static final int[] MAPPING_TO = new int[]
	{
		android.R.id.text1,
	};
	// instances
	private final FilenameFilter backupFilenameFilter = new BackupFilenameFilter();
	private final List<Map<String, String>> backupFileDataList = new ArrayList<Map<String, String>>();
	private SimpleAdapter simpleAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

//		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_backup_file_activity);

		// initialize title
		Intent intent = getIntent();
		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		if (title != null)
		{
			TextView titleTextView = (TextView) findViewById(R.id.title_textview);
			titleTextView.setText(title);
		}

		// initialize list adapter
		simpleAdapter = new SimpleAdapter(this, backupFileDataList,
			android.R.layout.simple_list_item_1,
			MAPPING_FROM, MAPPING_TO);
		setListAdapter(simpleAdapter);

		ListView listView = getListView();
		listView.setOnItemClickListener(this);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");
		super.onResume();

		updateListData();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onItemClick(AdapterView<?> av, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");

		Map<String, String> backupFileData = backupFileDataList.get(position);
		String backupFilePath = backupFileData.get(MAPPING_KEY_FILE_PATH);
		Uri backupFileUri = Uri.fromFile(new File(backupFilePath));

		Intent resultIntent = new Intent();
		resultIntent.setData(backupFileUri);
		Log.d(LOG_TAG, "resultIntent => " + resultIntent);

		setResult(Activity.RESULT_OK, resultIntent);
		finish();

		Log.v(LOG_TAG, "Bye");
	}

	private synchronized void updateListData()
	{
		Log.v(LOG_TAG, "Hello");

		String externalStorageState = Environment.getExternalStorageState();
		if (externalStorageState.equals(Environment.MEDIA_MOUNTED))
		{
			File backupDirPath = Environment.getExternalStorageDirectory();
			backupDirPath = new File(backupDirPath, BACKUP_DIR_NAME);
			Log.d(LOG_TAG, "backupDirPath => " + backupDirPath);

			if (backupDirPath.exists() && backupDirPath.canRead())
			{
				File[] listFiles = backupDirPath.listFiles(backupFilenameFilter);
				if (listFiles != null)
				{
					backupFileDataList.clear();
					for (File file : listFiles)
					{
						Map<String, String> backupFileData = new HashMap<String, String>();
						backupFileData.put(MAPPING_KEY_FILE_PATH, file.getAbsolutePath());
						backupFileData.put(MAPPING_KEY_FILE_NAME, file.getName());
						Log.d(LOG_TAG, "backupFileData => " + backupFileData);
						backupFileDataList.add(backupFileData);
					}
				}
			}
		}

		if (simpleAdapter != null)
		{
			simpleAdapter.notifyDataSetChanged();
		}

		Log.v(LOG_TAG, "Bye");
	}

	class BackupFilenameFilter implements FilenameFilter
	{

		public boolean accept(File dir, String name)
		{
			boolean result = false;
			if (name.endsWith(BACKUP_FILE_SUFFIX))
			{
				String basename = name.substring(0, name.length() - BACKUP_FILE_SUFFIX.length());
				Log.d(LOG_TAG, "basename => " + basename);
				if (basename.length() >= BACKUP_FILE_DATE_FORMAT.length())
				{
					result = true;
				}
			}
			return result;
		}
	}
}
