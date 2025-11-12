/*
 *  The MIT License
 * 
 *  Copyright 2011-2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
package org.routine_work.notepad.about;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AppVersionActivity extends Activity
{

	private static final String LOG_TAG = "simple-battery-logger";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_version_activity);

		// init package build time
		Date buildTime = getPackageBuildTime();
		if (buildTime != null)
		{
			String buildTimeformat = getString(R.string.build_time_format);
			CharSequence buildTimeText = DateFormat.format(buildTimeformat, buildTime);

			TextView buildTimeTextView = (TextView) findViewById(R.id.build_time_textview);
			buildTimeTextView.setText(buildTimeText);
		}

		// init package version
		PackageManager packageManager = getPackageManager();
		try
		{
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

			TextView versionTextView = (TextView) findViewById(R.id.version_textview);
			versionTextView.setText(packageInfo.versionName);

		}
		catch (NameNotFoundException ex)
		{
			Log.e(LOG_TAG, "Get package infomation failed.", ex);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_option_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;

		int itemId = item.getItemId();
        if (itemId == R.id.quit_menuitem) {
            NotepadActivity.quitApplication(this);
        } else {
            result = super.onOptionsItemSelected(item);
        }

		return result;
	}

	private Date getPackageBuildTime()
	{
		final String targetFileName = "classes.dex";
		Date packageBuildTime = null;
		try
		{
			FileInputStream fis = new FileInputStream(getPackageCodePath());
			try
			{
				ZipInputStream zipInputStream = new ZipInputStream(fis);
				try
				{
					ZipEntry zipEntry;
					while ((zipEntry = zipInputStream.getNextEntry()) != null)
					{
						try
						{
							String entryName = zipEntry.getName();
							Date entryTime = new Date(zipEntry.getTime());
							if (targetFileName.equals(entryName))
							{
								packageBuildTime = entryTime;
							}
						}
						finally
						{
							zipInputStream.closeEntry();
						}

					}
				}
				finally
				{
					zipInputStream.close();
				}
			}
			finally
			{
				fis.close();
			}
		}
		catch (IOException ex)
		{
			Log.e(LOG_TAG, "Getting package build time failed.", ex);
		}

		return packageBuildTime;
	}
}
