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
package org.routine_work.notepad;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.*;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

/**
 * <ul> <li>View note</li> <li>Edit note</li> </ul>
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplateDetailActivity extends Activity
	implements View.OnClickListener, OnFocusChangeListener,
	NotepadConstants
{

	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private static final String SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI = "currentNoteTemplateUri";
	private static final String LOG_TAG = "simple-notepad";
	// views
	private ViewGroup actionBarContainer;
	private TextView titleTextView;
	private ImageButton homeImageButton;
	private EditText noteTemplateNameEditText;
	private EditText noteTemplateTitleEditText;
	private EditText noteTemplateContentEditText;
	// data
	private String originalTemplateName = "";
	private String originalTemplateTitle = "";
	private String originalTemplateontent = "";
	private String currentAction;
	private Uri currentNoteTemplateUri;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		// When software keyboard was displayed, the window is adjust resize.
//		getWindow().setSoftInputMode(
//			WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//			| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		setContentView(R.layout.note_template_detail_activity);

		actionBarContainer = (ViewGroup) findViewById(R.id.actionbar_container);
		noteTemplateNameEditText = (EditText) findViewById(R.id.note_template_name_edittext);
		noteTemplateNameEditText.setOnFocusChangeListener(this);
		noteTemplateTitleEditText = (EditText) findViewById(R.id.note_template_title_edittext);
		noteTemplateTitleEditText.setOnFocusChangeListener(this);
		noteTemplateContentEditText = (EditText) findViewById(R.id.note_template_content_edittext);
		noteTemplateContentEditText.setOnFocusChangeListener(this);

		titleTextView = (TextView) findViewById(R.id.title_textview);
		homeImageButton = (ImageButton) findViewById(R.id.home_button);
		homeImageButton.setOnClickListener(this);

		// process intent
		initWithIntent(savedInstanceState, getIntent());

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.v(LOG_TAG, "Hello");
		super.onSaveInstanceState(outState);
		saveNoteTemplate();
		outState.putString(SAVE_KEY_CURRENT_ACTION, currentAction);
		outState.putParcelable(SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI, currentNoteTemplateUri);
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.v(LOG_TAG, "Hello");

		super.onNewIntent(intent);
		initWithIntent(null, intent);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onPause()
	{
		Log.v(LOG_TAG, "Hello");

		super.onPause();
		saveNoteTemplate();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume()
	{
		Log.v(LOG_TAG, "Hello");

		super.onResume();
		Log.d(LOG_TAG, "------------------------------------------------------------");
		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteTemplateUri => " + currentNoteTemplateUri);
		Log.d(LOG_TAG, "noteTemplateNameEditText.text => " + noteTemplateNameEditText.getText().toString());
		Log.d(LOG_TAG, "noteTemplateTitleEditText.text => " + noteTemplateTitleEditText.getText().toString());
		Log.d(LOG_TAG, "noteTemplateContentEditText.text => " + noteTemplateContentEditText.getText().toString());
		Log.d(LOG_TAG, "originalTemplateName => " + originalTemplateName);
		Log.d(LOG_TAG, "originalTemplateTitle => " + originalTemplateTitle);
		Log.d(LOG_TAG, "originalTemplateontent => " + originalTemplateontent);
		Log.d(LOG_TAG, "------------------------------------------------------------");

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onDestroy()
	{
		Log.v(LOG_TAG, "Hello");
		super.onDestroy();
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onStop()
	{
		Log.v(LOG_TAG, "Hello");
		super.onStop();
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.v(LOG_TAG, "Hello");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.quit_menu, menu);

		Log.v(LOG_TAG, "Bye");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.quit_menuitem:
				Log.d(LOG_TAG, "quit_menuitem selected.");
				NotepadActivity.quitApplication(this);
				finish();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	public void onClick(View view)
	{
		Log.v(LOG_TAG, "Hello");

		int viewId = view.getId();
		switch (viewId)
		{
			case R.id.home_button:
				Log.d(LOG_TAG, "Home Button is clicked.");
				setResultByModifiedFlag();
				finish();
				NotepadActivity.goHomeActivity(this);
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean result;
		Log.v(LOG_TAG, "Hello");

		Log.v(LOG_TAG, "keyCode => " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Log.v(LOG_TAG, "KEYCODE_BACK is down.");
			setResultByModifiedFlag();
			finish();
			result = true;
		}
		else
		{
			result = super.onKeyDown(keyCode, event);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		Log.v(LOG_TAG, "Hello");

		switch (v.getId())
		{
			case R.id.note_template_name_edittext:
				if (hasFocus)
				{
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				break;
			case R.id.note_template_title_edittext:
				if (hasFocus)
				{
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				break;
			case R.id.note_template_content_edittext:
				if (hasFocus)
				{
					IMEUtils.showSoftKeyboardWindow(this, v);
				}
				break;
		}

		Log.v(LOG_TAG, "Bye");
	}

	protected void initWithIntent(Bundle savedInstanceState, Intent intent)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteTemplateUri => " + currentNoteTemplateUri);

		actionBarContainer.setVisibility(View.VISIBLE);
		// load saved note uri
		String newAction = null;
		Uri newNoteTemplateUri = null;
		Log.d(LOG_TAG, "savedInstanceState => " + savedInstanceState);
		if (savedInstanceState != null)
		{
			// load currentAction
			newAction = savedInstanceState.getString(SAVE_KEY_CURRENT_ACTION);
			Log.d(LOG_TAG, "SAVE_KEY_CURRENT_ACTION => " + savedInstanceState.getString(SAVE_KEY_CURRENT_ACTION));

			// load currentNoteUri
			Parcelable noteUriObj = savedInstanceState.getParcelable(SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI);
			Log.d(LOG_TAG, "SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI => " + savedInstanceState.getParcelable(SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI));
			if (noteUriObj instanceof Uri)
			{
				newNoteTemplateUri = (Uri) noteUriObj;
				Log.d(LOG_TAG, "newNoteTemplateUri => " + newNoteTemplateUri);
			}
		}

		if (newAction == null)
		{
			Log.d(LOG_TAG, "intent.action => " + intent.getAction());
			newAction = intent.getAction();
			if (newAction == null)
			{
				newAction = Intent.ACTION_EDIT;
			}
		}

		if (newNoteTemplateUri == null)
		{
			Log.d(LOG_TAG, "intent.data => " + intent.getData());
			newNoteTemplateUri = intent.getData();
			if (newNoteTemplateUri == null)
			{
				newNoteTemplateUri = currentNoteTemplateUri;
			}
		}


		Log.d(LOG_TAG, "newAction => " + newAction);
		Log.d(LOG_TAG, "newNoteTemplateUri => " + newNoteTemplateUri);

		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());
		if (Intent.ACTION_INSERT.equals(newAction))
		{
			currentNoteTemplateUri = NoteStore.NoteTemplate.CONTENT_URI;
			noteTemplateTitleEditText.setText(null);
			noteTemplateContentEditText.setText(null);

			titleTextView.setText(R.string.add_new_note_template_title);

			newAction = Intent.ACTION_EDIT;

			noteTemplateNameEditText.requestFocus();
		}
		else if (Intent.ACTION_EDIT.equals(newAction))
		{
			if (newNoteTemplateUri != null)
			{
				if (NoteStore.exist(getContentResolver(), newNoteTemplateUri))
				{
					currentNoteTemplateUri = newNoteTemplateUri;
					loadNote();
				}
				else
				{
					Toast.makeText(this, R.string.note_not_exist, Toast.LENGTH_LONG).show();
					finish();
					return;
				}
			}

			titleTextView.setText(R.string.edit_note_template_title);
			noteTemplateNameEditText.requestFocus();
		}

		currentAction = newAction;
		originalTemplateName = noteTemplateNameEditText.getText().toString();
		originalTemplateTitle = noteTemplateTitleEditText.getText().toString();
		originalTemplateontent = noteTemplateContentEditText.getText().toString();
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.v(LOG_TAG, "Bye");
	}

	private void loadNote()
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "currentNoteTemplateUri => " + currentNoteTemplateUri);

		if (isNoteTemplateItemUri(currentNoteTemplateUri))
		{
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(currentNoteTemplateUri, null, null, null, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					int nameIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.NAME);
					int titleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE);
					int contentIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.CONTENT);
					String templateName = cursor.getString(nameIndex);
					String templateTitle = cursor.getString(titleIndex);
					String templateContent = cursor.getString(contentIndex);
					Log.d(LOG_TAG, "templateTitle => " + templateTitle);
					Log.d(LOG_TAG, "templateContent => " + templateContent);

					noteTemplateNameEditText.setText(templateName);
					noteTemplateTitleEditText.setText(templateTitle);
					noteTemplateContentEditText.setText(templateContent);

					originalTemplateName = noteTemplateNameEditText.getText().toString();
					originalTemplateTitle = noteTemplateTitleEditText.getText().toString();
					originalTemplateontent = noteTemplateContentEditText.getText().toString();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isModified()
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		String templateName = noteTemplateNameEditText.getText().toString();
		String templateTitle = noteTemplateTitleEditText.getText().toString();
		String templateContent = noteTemplateContentEditText.getText().toString();
		Log.d(LOG_TAG, "templateName => " + templateName);
		Log.d(LOG_TAG, "templateTitle => " + templateTitle);
		Log.d(LOG_TAG, "templateContent => " + templateContent);
		Log.d(LOG_TAG, "originalNoteName => " + originalTemplateName);
		Log.d(LOG_TAG, "originalNoteTitle => " + originalTemplateTitle);
		Log.d(LOG_TAG, "originalNoteContent => " + originalTemplateontent);

		if ((templateName.equals(originalTemplateName)
			&& templateTitle.equals(originalTemplateTitle)
			&& templateContent.equals(originalTemplateontent)) == false)
		{
			result = true;
		}

		Log.d(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void setResultByModifiedFlag()
	{
		Log.v(LOG_TAG, "Hello");
		if (isModified())
		{
			Log.d(LOG_TAG, "setResult(RESULT_OK)");
			setResult(RESULT_OK);
		}
		else
		{
			Log.d(LOG_TAG, "setResult(RESULT_CANCELED)");
			setResult(RESULT_CANCELED);
		}
		Log.v(LOG_TAG, "Bye");
	}

	private void saveNoteTemplate()
	{

		if (isModified())
		{
			Log.d(LOG_TAG, "note is modified.");

			String templateName = noteTemplateNameEditText.getText().toString();
			String templateTitle = noteTemplateTitleEditText.getText().toString();
			String templateContent = noteTemplateContentEditText.getText().toString();

			ContentValues values = new ContentValues();
			values.put(NoteStore.NoteTemplate.Columns.NAME, templateName);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, templateTitle);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, templateContent);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, false);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT_LOCKED, false);

			ContentResolver contentResolver = getContentResolver();
			if (isNoteTemplateItemUri(currentNoteTemplateUri))
			{
				// Update
				int updatedCount = contentResolver.update(currentNoteTemplateUri, values, null, null);
				Log.d(LOG_TAG, "updated : updatedCount => " + updatedCount);
			}
			else
			{
				// Insert
				currentNoteTemplateUri = contentResolver.insert(NoteStore.NoteTemplate.CONTENT_URI, values);
				Log.d(LOG_TAG, "inserted : currentNoteTemplateUri => " + currentNoteTemplateUri);
			}
			originalTemplateName = templateName;
			originalTemplateTitle = templateTitle;
			originalTemplateontent = templateContent;
			setResult(RESULT_OK);
		}
		else
		{
			Log.d(LOG_TAG, "note is not modified.");
		}

	}

	private boolean isNoteTemplateItemUri(Uri uri)
	{
		boolean result = false;

		ContentResolver contentResolver = getContentResolver();
		String type = contentResolver.getType(uri);
		Log.v(LOG_TAG, "uri => " + uri);
		Log.v(LOG_TAG, "type => " + type);
		if (NoteStore.NoteTemplate.NOTE_TEMPLATE_ITEM_CONTENT_TYPE.equals(type))
		{
			result = true;
		}
		Log.v(LOG_TAG, "isNoteTemplateItemUri => " + result);

		return result;
	}
}
