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
package org.routine_work.notepad.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ClipboardManager;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;
import org.routine_work.notepad.provider.NoteStore;
import org.routine_work.notepad.utils.NoteUtils;
import org.routine_work.notepad.utils.NotepadConstants;
import org.routine_work.notepad.utils.TextViewFindWordContext;
import org.routine_work.utils.IMEUtils;
import org.routine_work.utils.Log;

public class ViewNoteFragment extends NoteDetailFragment implements NotepadConstants

{

	private static final String LOG_TAG = "simple-notepad";
	private NoteControlCallback noteControlCallback;
	private final FindWordActionModeCallback findWordActionModeCallback = new FindWordActionModeCallback();
	private ActionMode findWordActionMode = null;
	private EditText findWordEditText = null;
	private final TextViewFindWordContext findWordContext = new TextViewFindWordContext();
	private ScrollView noteViewScrollView;

	public ViewNoteFragment()
	{
		Log.v(LOG_TAG, "Hello");
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onAttach(Activity activity)
	{
		Log.v(LOG_TAG, "Hello");

		super.onAttach(activity);
		if (activity instanceof NoteControlCallback)
		{
			noteControlCallback = (NoteControlCallback) activity;
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initFindWordContextColors();

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		Log.v(LOG_TAG, "Hello");

		View view = super.onCreateView(inflater, container, savedInstanceState);

		noteViewScrollView = (ScrollView) view.findViewById(R.id.note_view_scrollview);

		if (NotepadPreferenceUtils.getNoteTitleAutoLink(getActivity()))
		{
			noteTitleTextView.setAutoLinkMask(Linkify.ALL);
		}
		else
		{
			noteTitleTextView.setAutoLinkMask(0);
		}

		if (NotepadPreferenceUtils.getNoteContentAutoLink(getActivity()))
		{
			noteContentTextView.setAutoLinkMask(Linkify.ALL);
		}
		else
		{
			noteContentTextView.setAutoLinkMask(0);
		}

		registerForContextMenu(noteTitleTextView);
		registerForContextMenu(noteContentTextView);

		Log.v(LOG_TAG, "Bye");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	{
		Log.v(LOG_TAG, "Hello");

		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.edit_note_option_menu, menu);
		menuInflater.inflate(R.menu.find_word_option_menu, menu);
		menuInflater.inflate(R.menu.share_note_option_menu, menu);

		MenuItem searchNoteMenuItem = menu.findItem(R.id.search_notes_menuitem);
		searchNoteMenuItem.setVisible(false);

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.edit_note_menuitem:
				Log.d(LOG_TAG, "edit_note_menuitem is clicked.");
				startEditNoteActivity();
				break;
			case R.id.share_note_menuitem:
				Log.d(LOG_TAG, "share_note_menuitem is clicked.");
				startShareNoteActivity();
				break;
			case R.id.delete_note_menuitem:
				Log.d(LOG_TAG, "delete_note_menuitem is clicked.");
				startDeleteNoteActivity();
				break;
			case R.id.find_word_menuitem:
				Log.d(LOG_TAG, "find_word_menuitem is clicked.");
				startFindWordActionMode();
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater menuInflater = getActivity().getMenuInflater();
		switch (v.getId())
		{
			case R.id.note_title_textview:
				menuInflater.inflate(R.menu.note_title_context_menu, menu);
				break;
			case R.id.note_content_textview:
				menuInflater.inflate(R.menu.note_content_context_menu, menu);
				break;
			default:
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		boolean result = true;
		Log.v(LOG_TAG, "Hello");

		switch (item.getItemId())
		{
			case R.id.edit_note_menuitem:
				Log.d(LOG_TAG, "edit_note_menuitem selected.");
				startEditNoteActivity();
				break;
			case R.id.copy_note_title_menuitem:
				Log.d(LOG_TAG, "copy_note_title_menuitem selected.");
				copyNoteTitleToClipboard();
				break;
			case R.id.copy_note_content_menuitem:
				Log.d(LOG_TAG, "copy_note_content_menuitem selected.");
				copyNoteContentToClipboard();
				break;
			case R.id.find_word_menuitem:
				Log.d(LOG_TAG, "find_word_menuitem selected.");
				startFindWordActionMode();
				break;
			default:
				result = super.onContextItemSelected(item);
		}

		Log.v(LOG_TAG, "Hello");
		return result;
	}

	@Override
	public void setNoteContent(String noteContent)
	{
		Log.v(LOG_TAG, "Hello");

		if (noteContentTextView != null)
		{
			findWordContext.setContentText(noteContent);
			noteContentTextView.setText(findWordContext.getSpannable());
		}

		Log.v(LOG_TAG, "Bye");
	}

	public void loadNote()
	{
		Log.v(LOG_TAG, "Hello");

		LoaderManager loaderManager = getLoaderManager();
		Loader loader = loaderManager.getLoader(NOTE_LOADER_ID);
		Log.d(LOG_TAG, "loader => " + loader);

		String type = getActivity().getContentResolver().getType(noteUri);
		Log.d(LOG_TAG, "noteUri => " + noteUri);
		Log.d(LOG_TAG, "type => " + type);
		if (NoteStore.Note.NOTE_ITEM_CONTENT_TYPE.equals(type))
		{
			loaderManager.restartLoader(NOTE_LOADER_ID, null, this);
			Log.d(LOG_TAG, "restartLoader()");
		}
		else
		{
			loaderManager.destroyLoader(NOTE_LOADER_ID);
			Log.d(LOG_TAG, "destroyLoader()");
		}

		// if in find word action mode, finish it
		if (findWordActionMode != null)
		{
			findWordActionMode.finish();
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void initFindWordContextColors()
	{
		// Init findWordContext text backgound color
		Resources.Theme theme = getActivity().getTheme();
		TypedValue outValue = new TypedValue();
		theme.resolveAttribute(R.attr.foundWordBackgroundColor, outValue, true);
		Log.v(LOG_TAG, "foundWordBackgroundColor => " + Integer.toHexString(outValue.data));
		if (outValue.type == TypedValue.TYPE_INT_COLOR_ARGB8)
		{
			findWordContext.setFoundWordBackgroundColor(outValue.data);
		}
		theme.resolveAttribute(R.attr.foundWordForegroundColor, outValue, true);
		Log.v(LOG_TAG, "foundWordForegroundColor => " + Integer.toHexString(outValue.data));
		if (outValue.type == TypedValue.TYPE_INT_COLOR_ARGB8)
		{
			findWordContext.setFoundWordForegroundColor(outValue.data);
		}

		theme.resolveAttribute(R.attr.selectedWordBackgroundColor, outValue, true);
		Log.v(LOG_TAG, "selectedWordBackgroundColor => " + Integer.toHexString(outValue.data));
		if (outValue.type == TypedValue.TYPE_INT_COLOR_ARGB8)
		{
			findWordContext.setSelectedWordBackgroundColor(outValue.data);
		}

		theme.resolveAttribute(R.attr.selectedWordForegroundColor, outValue, true);
		Log.v(LOG_TAG, "selectedWordForegroundColor => " + Integer.toHexString(outValue.data));
		if (outValue.type == TypedValue.TYPE_INT_COLOR_ARGB8)
		{
			findWordContext.setSelectedWordForegroundColor(outValue.data);
		}
	}

	private void startEditNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null
			&& NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			noteControlCallback.startEditNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startDeleteNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");

		if (noteControlCallback != null
			&& NoteStore.isNoteItemUri(getActivity(), noteUri))
		{
			noteControlCallback.startDeleteNote(noteUri);
		}

		Log.v(LOG_TAG, "Bye");
	}

	private void startShareNoteActivity()
	{
		Log.v(LOG_TAG, "Hello");
		String noteTitle = noteTitleTextView.getText().toString();
		String noteContent = noteContentTextView.getText().toString();
		NoteUtils.shareNote(getActivity(), noteTitle, noteContent);
		Log.v(LOG_TAG, "Bye");
	}

	private void copyNoteTitleToClipboard()
	{
		ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
		clipboardManager.setText(noteTitleTextView.getText());
	}

	private void copyNoteContentToClipboard()
	{
		ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
		clipboardManager.setText(noteContentTextView.getText());
	}

	private void updateFindWordViews()
	{
		this.noteContentTextView.setText(this.findWordContext.getSpannable());

		int selectedWordLineNumber = this.findWordContext.getSelectedWordLineNumber();
		if (selectedWordLineNumber >= 0)
		{
			// scroll to selected word
			Rect rect = new Rect();
			noteContentTextView.getLineBounds(selectedWordLineNumber, rect);
			noteViewScrollView.scrollTo(rect.left, rect.top - noteContentTextView.getLineHeight());
		}
	}

	private void findWord()
	{
		if (findWordActionMode != null && findWordEditText != null)
		{
			CharSequence targetWord = findWordEditText.getText();
			if (TextUtils.isEmpty(targetWord) == false)
			{
				findWord(targetWord);
			}
		}
	}

	private void findWord(CharSequence targetWord)
	{
		if (findWordActionMode != null)
		{
			findWordContext.setTargetWord(targetWord);
			updateFindWordViews();

			// Show found count message in Toast
			int yOffset = (int) (this.getResources().getDisplayMetrics().density * 50.0f);
			int foundCount = findWordContext.getFoundWordCount();
			String foundMessage = getResources().getQuantityString(R.plurals.found_word_count_message, foundCount, foundCount);
			Toast toast = Toast.makeText(getActivity(), foundMessage, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, yOffset);
			toast.show();

			IMEUtils.hideSoftKeyboardWindow(getActivity(), findWordEditText);
		}
	}

	private void findPrevWord()
	{
		if (findWordActionMode != null && findWordEditText != null)
		{
			String currentTargetWord = this.findWordContext.getTargetWord();
			CharSequence newTargetWord = this.findWordEditText.getText();

			if (TextUtils.isEmpty(currentTargetWord) || TextUtils.equals(currentTargetWord, newTargetWord) == false)
			{
				findWord();
			}
			else
			{
				this.findWordContext.prevWord();
				updateFindWordViews();
			}
		}
	}

	private void findNextWord()
	{
		if (findWordActionMode != null && findWordEditText != null)
		{
			String currentTargetWord = this.findWordContext.getTargetWord();
			CharSequence newTargetWord = this.findWordEditText.getText();

			if (TextUtils.isEmpty(currentTargetWord) || TextUtils.equals(currentTargetWord, newTargetWord) == false)
			{
				findWord();
			}
			else
			{
				this.findWordContext.nextWord();
				updateFindWordViews();
			}
		}
	}

	private void startFindWordActionMode()
	{
		if (findWordActionMode == null)
		{
			findWordActionMode = getActivity().startActionMode(findWordActionModeCallback);
			if (findWordActionMode != null)
			{
				findWordActionMode.invalidate();
			}
		}
	}

	private class FindWordActionModeCallback implements ActionMode.Callback, TextView.OnEditorActionListener, View.OnFocusChangeListener
	{

		/**
		 * Create Find Word ActionMode
		 *
		 * @param mode
		 * @param menu
		 * @return
		 */
		public boolean onCreateActionMode(ActionMode mode, Menu menu)
		{
			Log.v(LOG_TAG, "onCreateActionMode() : Hello");

			MenuInflater menuInflater = getActivity().getMenuInflater();
			menuInflater.inflate(R.menu.find_word_actionmode_menu, menu);

			Log.v(LOG_TAG, "onCreateActionMode() : Bye");
			return true;
		}

		/**
		 * このアクションモードになる直前に呼ばれるコールバック。
		 *
		 * onCreateActionMode() より後でり、アクションアイテムなどの 各種ビューが初期化完了している事が期待できる。
		 *
		 * @param mode
		 * @param menu
		 * @return mode や menu が更新されたら true を返す
		 */
		public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
			Log.v(LOG_TAG, "Hello");

			MenuItem findWordActionMenuItem = menu.findItem(R.id.find_word_action_menuitem);
			View actionView = findWordActionMenuItem.getActionView();
			if (actionView != null)
			{
				findWordEditText = (EditText) actionView.findViewById(R.id.find_word_edittext);
				if (findWordEditText != null)
				{
					findWordEditText.setOnEditorActionListener(this);
					findWordEditText.setOnFocusChangeListener(this);
					IMEUtils.requestKeyboardFocus(findWordEditText);
				}
			}

			Log.v(LOG_TAG, "Bye");
			return false;
		}

		/**
		 * アクションアイテムがクリックされた際のコールバック。
		 *
		 * @param mode
		 * @param item
		 * @return
		 */
		public boolean onActionItemClicked(ActionMode mode, MenuItem item)
		{
			Log.v(LOG_TAG, "onActionItemClicked() : Hello");
			int itemId = item.getItemId();
			switch (itemId)
			{
				case R.id.prev_word_menuitem:
					Log.v(LOG_TAG, "onActionItemClicked() : prev_word_menuitem is clicked.");
					findPrevWord();
					break;
				case R.id.next_word_menuitem:
					Log.v(LOG_TAG, "onActionItemClicked() : next_word_menuitem is clicked.");
					findNextWord();
					break;
			}
			Log.v(LOG_TAG, "onActionItemClicked() : Bye");
			return true;
		}

		/**
		 * アクションモード終了時のコールバック。
		 *
		 * ActionMode#finish() を呼び出したり、左上の「×」をクリックして アクションモードが終了したときに呼ばれる。
		 *
		 * @param mode
		 */
		public void onDestroyActionMode(ActionMode mode)
		{
			Log.v(LOG_TAG, "onDestroyActionMode() : Hello");
			if (findWordEditText != null)
			{
				findWordEditText.setText("");
				findWordEditText = null;
			}

			findWordContext.setTargetWord("");
			updateFindWordViews();
			findWordActionMode = null;

			Log.v(LOG_TAG, "onDestroyActionMode() : Bye");
		}

		public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
		{
			boolean result = false;

			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				findWord(textView.getText());
				result = true;
			}

			return result;
		}

		public void onFocusChange(View view, boolean focused)
		{
			switch (view.getId())
			{
				case R.id.find_word_edittext:
					Log.v(LOG_TAG, "find_word_edittext : focused => " + focused);
					if (focused)
					{
						IMEUtils.showSoftKeyboardWindow(getActivity(), view);
					}
					else
					{
						IMEUtils.hideSoftKeyboardWindow(getActivity(), view);
					}
					break;
				default:
					throw new AssertionError();
			}
		}

	}
}
