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
package org.routine_work.notepad.template;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.*;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.common.EditTextActivity;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.utils.Log;

/**
 * <ul> <li>View note</li> <li>Edit note</li> </ul>
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplateDetailActivity2 extends Activity
	implements View.OnClickListener, NoteTemplateConstants
{

	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private static final String SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI = "currentNoteTemplateUri";
	private static final String LOG_TAG = "simple-notepad";
	// views
	private TextView titleTextView;
	private ImageButton homeImageButton;
	private TextView noteTemplateNameTextView;
	private TextView noteTemplateTitleTextView;
	private TextView noteTemplateContentTextView;
	private CheckBox noteTemplateTitleLockedCheckBox;
	// data
	private String originalTemplateName = "";
	private String originalTemplateTitle = "";
	private String originalTemplateContent = "";
	private boolean originalTemplateTitleLocked = false;
	private boolean originalTemplateContentLocked = false;
	private String currentAction;
	private Uri currentNoteTemplateUri;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.note_template_detail_activity2);

		titleTextView = (TextView) findViewById(R.id.title_textview);
		homeImageButton = (ImageButton) findViewById(R.id.home_button);
		noteTemplateNameTextView = (TextView) findViewById(R.id.note_template_name_textview);
		noteTemplateTitleTextView = (TextView) findViewById(R.id.note_template_title_textview);
		noteTemplateContentTextView = (TextView) findViewById(R.id.note_template_content_textview);
		noteTemplateTitleLockedCheckBox = (CheckBox) findViewById(R.id.note_template_title_lock_checkbox);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_CODE_EDIT_TEMPLATE_NAME:
				if (resultCode == RESULT_OK)
				{
					String name = data.getStringExtra(Intent.EXTRA_TEXT);
					noteTemplateNameTextView.setText(name);
				}
				break;
			case REQUEST_CODE_EDIT_TEMPLATE_TITLE:
				if (resultCode == RESULT_OK)
				{
					String title = data.getStringExtra(Intent.EXTRA_TEXT);
					noteTemplateTitleTextView.setText(title);
				}
				break;
			case REQUEST_CODE_EDIT_TEMPLATE_TEXT:
				if (resultCode == RESULT_OK)
				{
					String content = data.getStringExtra(Intent.EXTRA_TEXT);
					noteTemplateContentTextView.setText(content);
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
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
			case R.id.note_template_name_textview:
				Log.d(LOG_TAG, "note_template_name is clicked.");
				startEditNameActivity();
				break;
			case R.id.note_template_title_textview:
				Log.d(LOG_TAG, "note_template_title is clicked.");
				startEditTitleTemplateActivity();
				break;
			case R.id.note_template_content_textview:
				Log.d(LOG_TAG, "note_template_content is clicked.");
				startEditTextTemplateActivity();
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

	protected void initWithIntent(Bundle savedInstanceState, Intent intent)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.d(LOG_TAG, "currentAction => " + currentAction);
		Log.d(LOG_TAG, "currentNoteTemplateUri => " + currentNoteTemplateUri);

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
			noteTemplateTitleTextView.setText(null);
			noteTemplateContentTextView.setText(null);

			titleTextView.setText(R.string.add_new_note_template_title);

			newAction = Intent.ACTION_EDIT;

			noteTemplateNameTextView.requestFocus();
		}
		else if (Intent.ACTION_EDIT.equals(newAction))
		{
			if (isNoteTemplateItemUri(newNoteTemplateUri))
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
			noteTemplateNameTextView.requestFocus();
		}

		currentAction = newAction;
		originalTemplateName = noteTemplateNameTextView.getText().toString();
		originalTemplateTitle = noteTemplateTitleTextView.getText().toString();
		originalTemplateContent = noteTemplateContentTextView.getText().toString();
		originalTemplateTitleLocked = noteTemplateTitleLockedCheckBox.isChecked();
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
					int titleLockedIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.TITLE_LOCKED);

					String templateName = cursor.getString(nameIndex);
					String templateTitle = cursor.getString(titleIndex);
					String templateContent = cursor.getString(contentIndex);
					boolean templateTitleLocked = cursor.getInt(titleLockedIndex) != 0;

					Log.d(LOG_TAG, "templateName => " + templateName);
					Log.d(LOG_TAG, "templateTitle => " + templateTitle);
					Log.d(LOG_TAG, "templateContent => " + templateContent);
					Log.d(LOG_TAG, "templateTitleLocked => " + templateTitleLocked);

					noteTemplateNameTextView.setText(templateName);
					noteTemplateTitleTextView.setText(templateTitle);
					noteTemplateContentTextView.setText(templateContent);
					noteTemplateTitleLockedCheckBox.setChecked(templateTitleLocked);

					originalTemplateName = noteTemplateNameTextView.getText().toString();
					originalTemplateTitle = noteTemplateTitleTextView.getText().toString();
					originalTemplateContent = noteTemplateContentTextView.getText().toString();
					originalTemplateTitleLocked = noteTemplateTitleLockedCheckBox.isChecked();
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isModified()
	{
		boolean result = false;
		Log.v(LOG_TAG, "Hello");

		String templateName = noteTemplateNameTextView.getText().toString();
		String templateTitle = noteTemplateTitleTextView.getText().toString();
		String templateContent = noteTemplateContentTextView.getText().toString();
		boolean templateTitleLocked = noteTemplateTitleLockedCheckBox.isChecked();

		Log.d(LOG_TAG, "templateName => " + templateName);
		Log.d(LOG_TAG, "templateTitle => " + templateTitle);
		Log.d(LOG_TAG, "templateContent => " + templateContent);
		Log.d(LOG_TAG, "templateTitleLocked => " + templateTitleLocked);

		Log.d(LOG_TAG, "originalTemplateName => " + originalTemplateName);
		Log.d(LOG_TAG, "originalTemplateTitle => " + originalTemplateTitle);
		Log.d(LOG_TAG, "originalTemplateContent => " + originalTemplateContent);
		Log.d(LOG_TAG, "originalTemplateTitleLocked => " + originalTemplateTitleLocked);

		if ((templateName.equals(originalTemplateName)
			&& templateTitle.equals(originalTemplateTitle)
			&& templateContent.equals(originalTemplateContent)
			&& (templateTitleLocked == originalTemplateTitleLocked)) == false)
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

			String templateName = noteTemplateNameTextView.getText().toString();
			String templateTitle = noteTemplateTitleTextView.getText().toString();
			String templateContent = noteTemplateContentTextView.getText().toString();
			boolean templateTitleLocked = noteTemplateTitleLockedCheckBox.isChecked();

			ContentValues values = new ContentValues();
			values.put(NoteStore.NoteTemplate.Columns.NAME, templateName);
			values.put(NoteStore.NoteTemplate.Columns.TITLE, templateTitle);
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, templateContent);
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, templateTitleLocked);

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
			originalTemplateContent = templateContent;
			originalTemplateTitleLocked = templateTitleLocked;
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
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "uri => " + uri);

		if (uri != null)
		{
			String type = getContentResolver().getType(uri);
			Log.v(LOG_TAG, "type => " + type);
			if (NoteStore.NoteTemplate.NOTE_TEMPLATE_ITEM_CONTENT_TYPE.equals(type))
			{
				result = true;
			}
		}

		Log.d(LOG_TAG, "result => " + result);
		Log.v(LOG_TAG, "Bye");
		return result;
	}

	private void startEditNameActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.template_name);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, noteTemplateNameTextView.getText().toString());
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_NAME);
	}

	private void startEditTitleTemplateActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.title_template);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, noteTemplateTitleTextView.getText().toString());
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_TITLE);
	}

	private void startEditTextTemplateActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.text_template);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, noteTemplateContentTextView.getText().toString());
		intent.putExtra(EditTextActivity.EXTRA_INPUT_TYPE, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_TEXT);
	}
}
