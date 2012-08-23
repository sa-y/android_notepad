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

import org.routine_work.notepad.provider.NoteStore;

/**
 *
 * @author sawai
 */
public interface NotepadConstants
{

	int REQUEST_CODE_EDIT_NOTE = 102;
	int REQUEST_CODE_ADD_NOTE = 103;
	int REQUEST_CODE_DELETE_NOTE = 104;
	int REQUEST_CODE_DELETE_NOTES = 105;
	int REQUEST_CODE_PICK_NOTE = 106;
	int REQUEST_CODE_PICK_NOTE_TEMPLATE = 107;
	int NOTE_LOADER_ID = 0;
	String EXTRA_TITLE_LOCKED = NotepadConstants.class.getPackage().getName() + ".EXTRA_TITLE_LOCKED";
	String EXTRA_CONTENT_LOCKED = NotepadConstants.class.getPackage().getName() + ".EXTRA_CONTENT_LOCKED";
	String EXTRA_NOTE_TEMPLATE = NotepadConstants.class.getPackage().getName() + ".EXTRA_NOTE_TEMPLATE";
	String[] NOTE_LIST_MAPPING_FROM =
	{
		NoteStore.Note.Columns.TITLE,
		NoteStore.Note.Columns.CONTENT,
		NoteStore.Note.Columns.DATE_MODIFIED,
	};
	int[] NOTE_LIST_MAPPING_TO =
	{
		R.id.note_title_textview,
		R.id.note_content_textview,
		R.id.note_modified_textview,
	};
}
