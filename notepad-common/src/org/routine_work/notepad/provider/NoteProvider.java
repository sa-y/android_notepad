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

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import org.routine_work.notepad.provider.NoteStore.NoteColumns;
import org.routine_work.utils.Log;

public class NoteProvider extends ContentProvider
	implements NoteDBConstants
{

	private static final String LOG_TAG = "simple-notepad";
	// URI
	private static final int ITEM_ALL = 1;
	private static final int ITEM_BY_ID = 2;
	private static final int SUGGEST_SEARCH_ALL = 3;
	private static final int SUGGEST_SEARCH_BY_WORD = 4;
	private static final UriMatcher URI_MATCHER;
	private static final Map<String, String> SUGGESTION_PROJECTION_MAP;
	// DB
	private SQLiteDatabase noteDB;

	static
	{
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(NoteStore.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGEST_SEARCH_ALL);
		URI_MATCHER.addURI(NoteStore.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SUGGEST_SEARCH_BY_WORD);
		URI_MATCHER.addURI(NoteStore.AUTHORITY, "notes", ITEM_ALL);
		URI_MATCHER.addURI(NoteStore.AUTHORITY, "notes/#", ITEM_BY_ID);

		SUGGESTION_PROJECTION_MAP = new HashMap<String, String>();
		SUGGESTION_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1,
			NoteColumns.TITLE + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		SUGGESTION_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2,
			NoteColumns.CONTENT + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
		SUGGESTION_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
			NoteColumns._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		SUGGESTION_PROJECTION_MAP.put(NoteColumns._ID, NoteColumns._ID);
		SUGGESTION_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA,
			"'" + NoteStore.CONTENT_URI + "'" + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA);
	}

	@Override
	public boolean onCreate()
	{
		NoteDBHelper helper = new NoteDBHelper(getContext());
		noteDB = helper.getWritableDatabase();

		return noteDB != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection,
		String selection, String[] selectionArgs, String sort)
	{
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "query uri => " + uri);

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);

		int match = URI_MATCHER.match(uri);
		switch (match)
		{
			case SUGGEST_SEARCH_ALL:
			case SUGGEST_SEARCH_BY_WORD:
				qb.setProjectionMap(SUGGESTION_PROJECTION_MAP);
				break;
		}

		switch (match)
		{
			case SUGGEST_SEARCH_BY_WORD:
				Log.d(LOG_TAG, "*SEARCH_BY_WORD");
				String queryWord = uri.getLastPathSegment().trim();
				Log.d(LOG_TAG, "queryWord => " + queryWord);
				setUpQueryByWord(qb, queryWord);
				break;
			case SUGGEST_SEARCH_ALL:
				break;
			case ITEM_ALL:
				uri.getQueryParameter("q");
				Log.d(LOG_TAG, "*SEARCH_BY_WORD");
				String q = uri.getQueryParameter("q");
				Log.d(LOG_TAG, "queryWord => " + q);
				setUpQueryByWord(qb, q);
				break;
			case ITEM_BY_ID:
				qb.appendWhere(NoteColumns._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		String limit = getLimitParameter(uri);

		Cursor cursor = qb.query(noteDB, projection, selection, selectionArgs,
			null, null, sort, limit);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		Log.v(LOG_TAG, "Bye");
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		Log.v(LOG_TAG, "Hello");
		Uri newUri = null;

		long rowID = noteDB.insert(TABLE_NAME, null, initialValues);
		Log.d(LOG_TAG, "rowID => " + rowID);

		if (rowID > 0)
		{
			newUri = ContentUris.withAppendedId(NoteStore.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(newUri, null);
		}
		else
		{
			throw new SQLException("Failed to insert row into " + uri);
		}
		Log.d(LOG_TAG, "newUri => " + newUri);

		Log.v(LOG_TAG, "Bye");
		return newUri;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs)
	{
		int count;
		Log.v(LOG_TAG, "Hello");

		switch (URI_MATCHER.match(uri))
		{
			case ITEM_ALL:
				count = noteDB.delete(TABLE_NAME, where, whereArgs);
				break;
			case ITEM_BY_ID:
				String itemId = uri.getPathSegments().get(1);
				StringBuilder whereClause = new StringBuilder();
				whereClause.append(NoteStore.NoteColumns._ID);
				whereClause.append("=");
				whereClause.append(itemId);
				if (!TextUtils.isEmpty(where))
				{
					whereClause.append(" AND (");
					whereClause.append(where);
					whereClause.append(")");
				}

				count = noteDB.delete(TABLE_NAME, whereClause.toString(),
					whereArgs);
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		Log.v(LOG_TAG, "Bye");
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
		String[] whereArgs)
	{
		int count;
		Log.v(LOG_TAG, "Hello");

		switch (URI_MATCHER.match(uri))
		{
			case ITEM_ALL:
				count = noteDB.update(TABLE_NAME, values, where, whereArgs);
				break;

			case ITEM_BY_ID:
				String segment = uri.getPathSegments().get(1);

				StringBuilder whereClause = new StringBuilder();
				whereClause.append(NoteColumns._ID);
				whereClause.append("=");
				whereClause.append(segment);
				if (!TextUtils.isEmpty(where))
				{
					whereClause.append(" AND ");
					whereClause.append("(");
					whereClause.append(where);
					whereClause.append(")");
				}
				count = noteDB.update(TABLE_NAME, values,
					whereClause.toString(), whereArgs);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		Log.v(LOG_TAG, "Bye");
		return count;
	}

	@Override
	public String getType(Uri uri)
	{
		String type = null;
		Log.v(LOG_TAG, "Hello");
		Log.d(LOG_TAG, "uri => " + uri);

		switch (URI_MATCHER.match(uri))
		{
			case SUGGEST_SEARCH_ALL:
			case ITEM_ALL:
				type = NoteStore.NOTE_LIST_CONTENT_TYPE;
				break;
			case ITEM_BY_ID:
				type = NoteStore.NOTE_ITEM_CONTENT_TYPE;
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		Log.d(LOG_TAG, "type => " + type);
		Log.v(LOG_TAG, "Bye");
		return type;
	}

	private static String getLimitParameter(Uri uri)
	{
		String limit;

		limit = uri.getQueryParameter("limit");
		Log.d(LOG_TAG, "uri limit => " + limit);
		if (limit != null)
		{
			try
			{
				Integer.parseInt(limit);
			}
			catch (NumberFormatException e)
			{
				Log.e(LOG_TAG, "limit parameter is illegal value : limitText => " + limit);
				limit = null;
			}
		}

		return limit;
	}

	private static void setUpQueryByWord(SQLiteQueryBuilder queryBuilder, String queryWord)
	{
		if (!TextUtils.isEmpty(queryWord))
		{
			queryWord = "%" + queryWord + "%";
			queryBuilder.appendWhere(NoteColumns.TITLE + " LIKE ");
			queryBuilder.appendWhereEscapeString(queryWord);
			queryBuilder.appendWhere(" OR ");
			queryBuilder.appendWhere(NoteColumns.CONTENT + " LIKE ");
			queryBuilder.appendWhereEscapeString(queryWord);
		}
	}
}
