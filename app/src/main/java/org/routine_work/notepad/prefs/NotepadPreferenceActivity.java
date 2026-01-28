/*
 *  The MIT License
 *
 *  Copyright 2011-2013 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

import java.util.Objects;

/**
 * @author sawai
 */
public class NotepadPreferenceActivity extends AppCompatActivity
		implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{

	private static final String LOG_TAG = "simple-notepad";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notepad_preference);
		NotepadActivity.enableHomeButton(this);

		if (savedInstanceState == null)
		{
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.notepad_preference_container, new NotepadPreferenceFragment())
					.commit();
		}

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM)
		{
			View content = findViewById(android.R.id.content);
			if (content != null)
			{
				content.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
				{
					@Override
					public WindowInsets onApplyWindowInsets(View v, WindowInsets insets)
					{
						int top = insets.getSystemWindowInsetTop();
						int bottom = insets.getSystemWindowInsetBottom();
						int left = insets.getSystemWindowInsetLeft();
						int right = insets.getSystemWindowInsetRight();
						v.setPadding(left, top, right, bottom);

						return insets;
					}
				});
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref)
	{
		Log.v(LOG_TAG, "Hello");
		// Instantiate the new Fragment
		final Bundle args = pref.getExtras();
		final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
				getClassLoader(),
				pref.getFragment());
		fragment.setArguments(args);
		fragment.setTargetFragment(caller, 0);
		// Replace the existing Fragment with the new Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.notepad_preference_container, fragment)
				.addToBackStack(null)
				.commit();

		setTitle(pref.getTitle());

		Log.v(LOG_TAG, "Bye");
		return true;
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
		Log.v(LOG_TAG, "Hello");
		boolean result = true;

		int itemId = item.getItemId();
		if (itemId == R.id.quit_menuitem)
		{
			NotepadActivity.quitApplication(this);
		}
		else if (itemId == android.R.id.home)
		{
			Log.v(LOG_TAG, "android.R.id.home is clicked.");
			if (getSupportFragmentManager().getBackStackEntryCount() > 0)
			{
				Log.v(LOG_TAG, "pop back stack.");
				getSupportFragmentManager().popBackStack();
				setTitle(R.string.preferences_title);
			}
			else
			{
				Log.v(LOG_TAG, "finish activity.");
				finish();
			}
		}
		else
		{
			result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}
}
