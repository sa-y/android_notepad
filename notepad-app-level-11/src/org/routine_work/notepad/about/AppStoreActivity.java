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
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class AppStoreActivity extends Activity
{

	private static final String LOG_TAG = "simple-battery-logger";
	private Intent[] applicationStoreIntents;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		finish();
		openApplicationStore();
	}

	private void openApplicationStore()
	{
		Intent[] intents = getApplicationStoreIntents();
		for (Intent intent : intents)
		{
			try
			{
				Log.d(LOG_TAG, "AboutAppActivity#openApplicationStore() : intent => " + intent);
				startActivity(intent);
				break;
			}
			catch (ActivityNotFoundException e)
			{
				Log.d(LOG_TAG, "AboutAppActivity#openApplicationStore() : startActivity(" + intent + ") failed.");
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
}
