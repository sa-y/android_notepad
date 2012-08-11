/*
 * The MIT License
 *
 * Copyright 2011-2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
package org.routine_work.notepad.provider;

import org.routine_work.notepad.provider.NoteStore.NoteColumns;
import org.routine_work.notepad.provider.NoteStore.NoteTemplate;

/**
 *
 * @author sawai
 */
interface NoteDBConstants
{

	String DATABASE_NAME = "NoteDB";
	int DATABASE_VERSION = 7;

	interface Notes
	{

		String TABLE_NAME = "Notes";
		// CREATE SQL
		String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
			+ "  " + NoteColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ", " + NoteColumns.TITLE + " TEXT"
			+ ", " + NoteColumns.CONTENT + " TEXT"
			+ ", " + NoteColumns.TITLE_LOCKED + " BOOLEAN NOT NULL"
			+ ", " + NoteColumns.CONTENT_LOCKED + " BOOLEAN NOT NULL"
			+ ", " + NoteColumns.DATE_ADDED + " INTEGER NOT NULL"
			+ ", " + NoteColumns.DATE_MODIFIED + " INTEGER NOT NULL"
			+ ");";
		String CREATE_TITLE_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteColumns.TITLE + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteColumns.TITLE + ");";
		String CREATE_CONTENT_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteColumns.CONTENT + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteColumns.CONTENT + ");";
		String CREATE_DATE_ADDED_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteColumns.DATE_ADDED + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteColumns.DATE_ADDED + ");";
		String CREATE_DATE_MODIFIED_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteColumns.DATE_MODIFIED + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteColumns.DATE_MODIFIED + ");";
		// DROP SQL
		String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME + ";";
	}

	interface NoteTemplates
	{

		String TABLE_NAME = "NoteTemplates";
		// CREATE SQL
		String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
			+ "  " + NoteTemplate.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ", " + NoteTemplate.Columns.TITLE + " TEXT"
			+ ", " + NoteTemplate.Columns.CONTENT + " TEXT"
			+ ", " + NoteTemplate.Columns.TITLE_LOCKED + " BOOLEAN NOT NULL"
			+ ", " + NoteTemplate.Columns.CONTENT_LOCKED + " BOOLEAN NOT NULL"
			+ ");";
		String CREATE_TITLE_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteTemplate.Columns.TITLE + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteTemplate.Columns.TITLE + ");";
		String CREATE_CONTENT_INDEX_SQL = "CREATE INDEX "
			+ TABLE_NAME + "_" + NoteTemplate.Columns.CONTENT + "_index "
			+ "ON " + TABLE_NAME + "(" + NoteTemplate.Columns.CONTENT + ");";
		// DROP SQL
		String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME + ";";
	}
	String REINDEX_SQL = "REINDEX;";
	String VACCUM_SQL = "VACUUM;";
}
