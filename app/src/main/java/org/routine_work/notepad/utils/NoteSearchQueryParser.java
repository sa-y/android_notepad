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

import android.content.ContentUris;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;

import org.routine_work.notepad.provider.NoteStore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "id:<note_item_id>" - Item Id query other - word query
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteSearchQueryParser
{

	private static final Pattern ITEM_ID_QUERY_PATTERN = Pattern.compile("id:(\\d+)");

	public static Uri parseQuery(CharSequence queryString)
	{
		Uri contentUri;

		if (TextUtils.isEmpty(queryString))
		{ // fetch all notes
			contentUri = NoteStore.Note.CONTENT_URI;
		}
		else
		{
			long itemId = parseItemIdQuery(queryString);
			if (itemId != -1)
			{ // item id query
				contentUri = ContentUris.withAppendedId(NoteStore.Note.CONTENT_URI, itemId);
			}
			else
			{    // word query
				Builder builder = NoteStore.Note.CONTENT_URI.buildUpon();
				builder.appendQueryParameter(NoteStore.PARAM_KEY_QUERY, queryString.toString());
				contentUri = builder.build();
			}
		}

		return contentUri;
	}

	private static long parseItemIdQuery(CharSequence queryString)
	{
		long result = -1;

		Matcher matcher = ITEM_ID_QUERY_PATTERN.matcher(queryString);
		if (matcher.matches())
		{
			String idString = matcher.group(1);
			result = Long.parseLong(idString);
		}

		return result;
	}
}
