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
package org.routine_work.notepad.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class EditTextActivity extends Activity
{

	public static final String EXTRA_INPUT_TYPE = EditTextActivity.class.getPackage().getName() + ".INPUT_TYPE";
	private static final String LOG_TAG = "simple-notepad";
	private EditText mainEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
			| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.edit_text_activity);

		EditText singleLineEditText = (EditText) findViewById(R.id.single_line_edittext);
		EditText multiLineEditText = (EditText) findViewById(R.id.multi_line_edittext);

		Intent intent = getIntent();

		int defaultInputType = singleLineEditText.getInputType();
		int inputType = intent.getIntExtra(EXTRA_INPUT_TYPE, defaultInputType);
		Log.v(LOG_TAG, "EXTRA_INPUT_TYPE => " + inputType);
		if (((inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0)
			|| ((inputType & InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE) != 0))
		{
			mainEditText = multiLineEditText;
			singleLineEditText.setVisibility(View.GONE);
			multiLineEditText.setVisibility(View.VISIBLE);
		}
		else
		{
			mainEditText = singleLineEditText;
			singleLineEditText.setVisibility(View.VISIBLE);
			multiLineEditText.setVisibility(View.GONE);
		}

		if (inputType != mainEditText.getInputType())
		{
			mainEditText.setInputType(inputType);
		}

		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		Log.v(LOG_TAG, "EXTRA_TITLE => " + title);
		if (!TextUtils.isEmpty(title))
		{
			setTitle(title);
			mainEditText.setHint(title);
		}

		String text = intent.getStringExtra(Intent.EXTRA_TEXT);
		Log.v(LOG_TAG, "EXTRA_TEXT => " + text);
		if (!TextUtils.isEmpty(text))
		{
			mainEditText.setText(text);
		}

		mainEditText.requestFocus();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.save_cancel_option_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		switch (item.getItemId())
		{
			case R.id.save_menuitem:
				String text = mainEditText.getText().toString();
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Intent.EXTRA_TEXT, text);
				setResult(RESULT_OK, resultIntent);
				finish();
				break;
			case R.id.cancel_menuitem:
				setResult(RESULT_CANCELED);
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}
		return result;
	}
}
