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
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.routine_work.notepad.NotepadActivity;
import org.routine_work.notepad.R;
import org.routine_work.notepad.common.EditTextActivity;
import org.routine_work.notepad.model.NoteTemplate;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteTemplateConstants;
import org.routine_work.utils.Log;

/**
 * <ul> <li>View note</li> <li>Edit note</li> </ul>
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplateDetailActivity extends ListActivity
	implements View.OnClickListener, OnItemClickListener, NoteTemplateConstants
{

	private static final String SAVE_KEY_CURRENT_ACTION = "currentAction";
	private static final String SAVE_KEY_CURRENT_NOTE_TEMAPLATE_URI = "currentNoteTemplateUri";
	private static final int POSITION_NAME = 0;
	private static final int POSITION_TITLE = 1;
	private static final int POSITION_TITLE_LOCKED = 2;
	private static final int POSITION_EDIT_SAME_TITLE = 3;
	private static final int POSITION_CONTENT = 4;
	private static final String LOG_TAG = "simple-notepad";
	// view
	private TextView titleTextView;
	// model
	private NoteTemplate currentNoteTemplate = new NoteTemplate();
	private NoteTemplate originalNoteTemplate = new NoteTemplate();
	private String currentAction;
	private Uri currentNoteTemplateUri;
	private NoteTemplateDetailListAdapter noteTemplateDetailListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.note_template_detail_activity);

		titleTextView = (TextView) findViewById(R.id.title_textview);
		ImageButton homeImageButton = (ImageButton) findViewById(R.id.home_button);
		homeImageButton.setOnClickListener(this);

		// process intent
		initWithIntent(savedInstanceState, getIntent());

		noteTemplateDetailListAdapter = new NoteTemplateDetailListAdapter();
		setListAdapter(noteTemplateDetailListAdapter);

		ListView listView = getListView();
		listView.setOnItemClickListener(this);

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
					currentNoteTemplate.setName(name);
					noteTemplateDetailListAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_CODE_EDIT_TEMPLATE_TITLE:
				if (resultCode == RESULT_OK)
				{
					String title = data.getStringExtra(Intent.EXTRA_TEXT);
					currentNoteTemplate.setTitle(title);
					noteTemplateDetailListAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_CODE_EDIT_TEMPLATE_TEXT:
				if (resultCode == RESULT_OK)
				{
					String content = data.getStringExtra(Intent.EXTRA_TEXT);
					currentNoteTemplate.setContent(content);
					noteTemplateDetailListAdapter.notifyDataSetChanged();
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

	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "position => " + id);
		switch (position)
		{
			case POSITION_NAME:
				startEditNameActivity();
				break;
			case POSITION_TITLE:
				startEditTitleTemplateActivity();
				break;
			case POSITION_TITLE_LOCKED:
				CheckedTextView titleLockCheck = (CheckedTextView) view.findViewById(R.id.note_template_title_lock_checkbox);
				titleLockCheck.toggle();
				currentNoteTemplate.setTitleLocked(titleLockCheck.isChecked());
				break;
			case POSITION_EDIT_SAME_TITLE:
				CheckedTextView editSameTitleCheck = (CheckedTextView) view.findViewById(R.id.note_template_edit_same_title_checkbox);
				editSameTitleCheck.toggle();
				currentNoteTemplate.setEditSameTitle(editSameTitleCheck.isChecked());
				break;
			case POSITION_CONTENT:
				startEditTextTemplateActivity();
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
			currentNoteTemplate.setName("");
			currentNoteTemplate.setTitle("");
			currentNoteTemplate.setContent("");
			currentNoteTemplate.setTitleLocked(false);
			currentNoteTemplate.setEditSameTitle(true);

			titleTextView.setText(R.string.add_new_note_template_title);

			newAction = Intent.ACTION_EDIT;
		}
		else if (Intent.ACTION_EDIT.equals(newAction))
		{
			if (NoteStore.isNoteTemplateItemUri(this, newNoteTemplateUri))
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
		}

		currentAction = newAction;
		originalNoteTemplate.copyFrom(currentNoteTemplate);
		Log.d(LOG_TAG, "isFinishing() => " + isFinishing());

		Log.v(LOG_TAG, "Bye");
	}

	private void loadNote()
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "currentNoteTemplateUri => " + currentNoteTemplateUri);

		if (NoteStore.isNoteTemplateItemUri(this, currentNoteTemplateUri))
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
					int editSameTitleIndex = cursor.getColumnIndex(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE);

					String templateName = cursor.getString(nameIndex);
					String templateTitle = cursor.getString(titleIndex);
					String templateContent = cursor.getString(contentIndex);
					boolean templateTitleLocked = cursor.getInt(titleLockedIndex) != 0;
					boolean templateEditSameTitle = cursor.getInt(editSameTitleIndex) != 0;

					if (templateName == null)
					{
						templateName = "";
					}
					if (templateTitle == null)
					{
						templateTitle = "";
					}
					if (templateContent == null)
					{
						templateContent = "";
					}

					Log.d(LOG_TAG, "templateName => " + templateName);
					Log.d(LOG_TAG, "templateTitle => " + templateTitle);
					Log.d(LOG_TAG, "templateContent => " + templateContent);
					Log.d(LOG_TAG, "templateTitleLocked => " + templateTitleLocked);
					Log.d(LOG_TAG, "templateEditSameTitle => " + templateEditSameTitle);

					currentNoteTemplate.setName(templateName);
					currentNoteTemplate.setTitle(templateTitle);
					currentNoteTemplate.setTitleLocked(templateTitleLocked);
					currentNoteTemplate.setEditSameTitle(templateEditSameTitle);
					currentNoteTemplate.setContent(templateContent);

					originalNoteTemplate.copyFrom(currentNoteTemplate);
				}
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	private boolean isModified()
	{
		Log.v(LOG_TAG, "Hello");

//		Log.d(LOG_TAG, "originalNoteTemplate => " + originalNoteTemplate);
//		Log.d(LOG_TAG, "currentNoteTemplate => " + currentNoteTemplate);
		boolean result = currentNoteTemplate.equals(originalNoteTemplate) == false;

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
			Log.d(LOG_TAG, "note template is modified.");

			ContentValues values = new ContentValues();
			values.put(NoteStore.NoteTemplate.Columns.NAME, currentNoteTemplate.getName());
			values.put(NoteStore.NoteTemplate.Columns.TITLE, currentNoteTemplate.getTitle());
			values.put(NoteStore.NoteTemplate.Columns.CONTENT, currentNoteTemplate.getContent());
			values.put(NoteStore.NoteTemplate.Columns.TITLE_LOCKED, currentNoteTemplate.isTitleLocked());
			values.put(NoteStore.NoteTemplate.Columns.EDIT_SAME_TITLE, currentNoteTemplate.isEditSameTitle());

			ContentResolver contentResolver = getContentResolver();
			if (NoteStore.isNoteTemplateItemUri(this, currentNoteTemplateUri))
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
			originalNoteTemplate.copyFrom(currentNoteTemplate);
			setResult(RESULT_OK);
		}
		else
		{
			Log.d(LOG_TAG, "note is not modified.");
		}

	}

	private void startEditNameActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.template_name);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, currentNoteTemplate.getName());
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_NAME);
	}

	private void startEditTitleTemplateActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.title_template);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, currentNoteTemplate.getTitle());
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_TITLE);
	}

	private void startEditTextTemplateActivity()
	{
		Intent intent = new Intent(this, EditTextActivity.class);
		String title = getString(R.string.text_template);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_TEXT, currentNoteTemplate.getContent());
		intent.putExtra(EditTextActivity.EXTRA_INPUT_TYPE, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE_TEXT);
	}

	class NoteTemplateDetailListAdapter extends BaseAdapter
	{

		private LayoutInflater inflater;

		public NoteTemplateDetailListAdapter()
		{
			inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount()
		{
			return 5;
		}

		public Object getItem(int position)
		{
			Object result = null;
			switch (position)
			{
				case POSITION_NAME:
					result = currentNoteTemplate.getName();
					break;
				case POSITION_TITLE:
					result = currentNoteTemplate.getTitle();
					break;
				case POSITION_TITLE_LOCKED:
					result = Boolean.valueOf(currentNoteTemplate.isTitleLocked());
					break;
				case POSITION_EDIT_SAME_TITLE:
					result = Boolean.valueOf(currentNoteTemplate.isEditSameTitle());
					break;
				case POSITION_CONTENT:
					result = currentNoteTemplate.getContent();
					break;
			}
			return result;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemView;
			Log.d(LOG_TAG, "Hello");

			if (convertView == null)
			{
				itemView = inflater.inflate(R.layout.note_template_detail_item, null);
			}
			else
			{
				itemView = convertView;
			}
			View nameView = itemView.findViewById(R.id.note_template_detail_item_name);
			View titleView = itemView.findViewById(R.id.note_template_detail_item_title);
			View titleLockedView = itemView.findViewById(R.id.note_template_detail_item_title_locked);
			View editSameTitleView = itemView.findViewById(R.id.note_template_detail_item_edit_same_title);
			View contentView = itemView.findViewById(R.id.note_template_detail_item_content);

			nameView.setVisibility(View.GONE);
			titleView.setVisibility(View.GONE);
			titleLockedView.setVisibility(View.GONE);
			editSameTitleView.setVisibility(View.GONE);
			contentView.setVisibility(View.GONE);

			switch (position)
			{
				case POSITION_NAME:
					nameView.setVisibility(View.VISIBLE);
					TextView nameTextView = (TextView) itemView.findViewById(R.id.note_template_name_textview);
					nameTextView.setText(currentNoteTemplate.getName());
					break;
				case POSITION_TITLE:
					TextView titleTextView = (TextView) itemView.findViewById(R.id.note_template_title_textview);
					titleTextView.setText(currentNoteTemplate.getTitle());
					titleView.setVisibility(View.VISIBLE);
					break;
				case POSITION_TITLE_LOCKED:
					CheckedTextView titleLockCheck = (CheckedTextView) itemView.findViewById(R.id.note_template_title_lock_checkbox);
					titleLockCheck.setChecked(currentNoteTemplate.isTitleLocked());
					titleLockedView.setVisibility(View.VISIBLE);
					break;
				case POSITION_EDIT_SAME_TITLE:
					CheckedTextView editSameTitleCheck = (CheckedTextView) itemView.findViewById(R.id.note_template_edit_same_title_checkbox);
					editSameTitleCheck.setChecked(currentNoteTemplate.isEditSameTitle());
					editSameTitleView.setVisibility(View.VISIBLE);
					break;
				case POSITION_CONTENT:
					TextView contentTextView = (TextView) itemView.findViewById(R.id.note_template_content_textview);
					contentTextView.setText(currentNoteTemplate.getContent());
					contentView.setVisibility(View.VISIBLE);
					break;
			}

			Log.d(LOG_TAG, "Bye");
			return itemView;
		}
	}
}
