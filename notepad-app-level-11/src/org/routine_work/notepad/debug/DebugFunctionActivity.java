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
package org.routine_work.notepad.debug;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class DebugFunctionActivity extends ListActivity
	implements AdapterView.OnItemClickListener
{

	private static final String LOG_TAG = "simple-notepad";
	private static final int ITEM_ID_CREATE_NOTES = 0;
	private static final String[] FUNCTION_NAMES =
	{
		"Create Notes",
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_1,
			android.R.id.text1,
			FUNCTION_NAMES);
		setListAdapter(arrayAdapter);

		getListView().setOnItemClickListener(this);

		Log.v(LOG_TAG, "Bye");
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		switch (position)
		{
			case ITEM_ID_CREATE_NOTES:
				Log.d(LOG_TAG, "ITEM_ID_CREATE_NOTES is clicked.");
				startDummyNoteCreateService();
				break;
		}
	}

	private void startDummyNoteCreateService()
	{
		Log.v(LOG_TAG, "Hello");

		Intent serviceIntent = new Intent(this, DummyNoteCreateService.class);
		startService(serviceIntent);

		Log.v(LOG_TAG, "Bye");
	}
}
