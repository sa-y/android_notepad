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
package org.routine_work.notepad.prefs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AboutAppActivity extends Activity
	implements View.OnClickListener
{

	private static final String LOG_TAG = "android.R";
	private Intent[] applicationManagementIntents;
	private Intent[] applicationStoreIntents;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
//		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_app_activity);

		// init buttons
		Button openApplicationStoreButton = (Button) findViewById(R.id.open_application_store_button);
		openApplicationStoreButton.setOnClickListener(this);
		Button openApplicationManagementButton = (Button) findViewById(R.id.open_application_management_button);
		openApplicationManagementButton.setOnClickListener(this);

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
		menuInflater.inflate(R.menu.quit_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.quit_menuitem:
				NotepadActivity.quitApplication(this);
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		switch (id)
		{
			case R.id.open_application_store_button:
				openApplicationStore();
				break;
			case R.id.open_application_management_button:
				openApplicationManagement();
				break;
			default:
				throw new AssertionError();
		}
	}

	private void openApplicationStore()
	{
		Intent[] intents = getApplicationStoreIntents();
		for (Intent intent : intents)
		{
			try
			{
				Log.i(LOG_TAG, "AboutAppActivity#openApplicationStore() : intent => " + intent);
				startActivity(intent);
				break;
			}
			catch (ActivityNotFoundException e)
			{
				Log.i(LOG_TAG, "AboutAppActivity#openApplicationStore() : startActivity(" + intent + ") failed.");
			}
		}

	}

	private void openApplicationManagement()
	{
		Intent[] intents = getApplicationManagementIntents();
		for (Intent intent : intents)
		{
			try
			{
				Log.i(LOG_TAG, "open application management : intent => " + intent);
				startActivity(intent);
				break;
			}
			catch (ActivityNotFoundException e)
			{
				Log.i(LOG_TAG, "open application management : startActivity(" + intent + ") failed.");
			}
		}
	}

	private Intent[] getApplicationStoreIntents()
	{
		if (applicationStoreIntents == null)
		{
			String marketUri = String.format("market://details?id=%s", getPackageName());
			Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUri));
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// For which market app (play app) is not installed.
			String playStoreUri = String.format("https://play.google.com/store/apps/details?id=%s", getPackageName());
			Intent httpPlayStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri));
			httpPlayStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			applicationStoreIntents = new Intent[]
			{
				marketIntent, httpPlayStoreIntent,
			};
		}
		return applicationStoreIntents;
	}

	private Intent[] getApplicationManagementIntents()
	{
		if (applicationManagementIntents == null)
		{
			// API Level 9 or later
			Intent intent0 = new Intent(Intent.ACTION_VIEW);
			intent0.setClassName("com.android.settings",
				"com.android.settings.applications.InstalledAppDetails");
			intent0.setData(Uri.fromParts("package", getPackageName(), null));
			intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// API Level 8 or earlier
			Intent intent1 = new Intent(Intent.ACTION_VIEW);
			intent1.setClassName("com.android.settings",
				"com.android.settings.InstalledAppDetails");
			intent1.putExtra("pkg", getPackageName());
			intent1.putExtra("com.android.settings.ApplicationPkgName",
				getPackageName());
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// API Level 3(not in use?)
			Intent intent2 = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			applicationManagementIntents = new Intent[]
			{
				intent0, intent1, intent2,
			};
		}
		return applicationManagementIntents;
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
