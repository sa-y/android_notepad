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
package org.routine_work.notepad.utils;

import org.routine_work.notepad.provider.NoteStore;

/**
 * @author sawai
 */
public interface NoteTemplateConstants
{

	int REQUEST_CODE_ADD_NOTE_TEMPLATE = 201;
	int REQUEST_CODE_EDIT_NOTE_TEMPLATE = 208;
	int REQUEST_CODE_EDIT_TEMPLATE_NAME = 301;
	int REQUEST_CODE_EDIT_TEMPLATE_TITLE = 302;
	int REQUEST_CODE_EDIT_TEMPLATE_TEXT = 303;
	String[] NOTE_TEMPLATE_LIST_MAPPING_FROM =
			{
					NoteStore.NoteTemplate.Columns.NAME,
			};
	int[] NOTE_TEMPLATE_LIST_MAPPING_TO =
			{
					android.R.id.text1,
			};
}
