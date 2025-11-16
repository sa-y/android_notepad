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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.routine_work.utils.Log;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AppManagementActivity extends Activity
{

	private static final String LOG_TAG = "simple-battery-logger";
	private Intent[] applicationManagementIntents;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		finish();
		openApplicationManagement();
	}

	private void openApplicationManagement()
	{
		Intent[] intents = getApplicationManagementIntents();
		for (Intent intent : intents)
		{
			try
			{
				Log.i(LOG_TAG, "AboutAppActivity#openApplicationManagement() : intent => " + intent);
				startActivity(intent);
				break;
			}
			catch (ActivityNotFoundException e)
			{
				Log.i(LOG_TAG, "AboutAppActivity#openApplicationManagement() : startActivity(" + intent + ") failed.");
			}
		}
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
}
