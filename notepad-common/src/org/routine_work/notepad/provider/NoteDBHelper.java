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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.routine_work.utils.Log;

/**
 *
 * @author sawai
 */
class NoteDBHelper extends SQLiteOpenHelper
		implements NoteDBConstants {

	private String LOG_TAG = "simple-notepad";

	NoteDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.w(LOG_TAG, "Create database.");
		Log.w(LOG_TAG, "CREATE_TABLE_SQL => " + CREATE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
		Log.w(LOG_TAG, "CREATE_TITLE_INDEX_SQL => " + CREATE_TITLE_INDEX_SQL);
		db.execSQL(CREATE_TITLE_INDEX_SQL);
		Log.w(LOG_TAG, "CREATE_CONTENT_INDEX_SQL => " + CREATE_CONTENT_INDEX_SQL);
		db.execSQL(CREATE_CONTENT_INDEX_SQL);
		Log.w(LOG_TAG, "CREATE_DATE_ADDED_INDEX_SQL => " + CREATE_DATE_ADDED_INDEX_SQL);
		db.execSQL(CREATE_DATE_ADDED_INDEX_SQL);
		Log.w(LOG_TAG, "CREATE_DATE_MODIFIED_INDEX_SQL => " + CREATE_DATE_MODIFIED_INDEX_SQL);
		db.execSQL(CREATE_DATE_MODIFIED_INDEX_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL(DROP_TABLE_SQL);
		onCreate(db);
	}

	public void reindex(SQLiteDatabase db) {
		db.execSQL(REINDEX_SQL);
	}

	public void vacuum(SQLiteDatabase db) {
		db.execSQL(VACCUM_SQL);
	}
}