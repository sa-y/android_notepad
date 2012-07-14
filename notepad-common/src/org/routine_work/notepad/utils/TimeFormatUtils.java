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
package org.routine_work.notepad.utils;

import android.content.Context;
import android.text.format.DateFormat;
import java.util.Date;

/**
 *
 * @author sawai
 */
public class TimeFormatUtils
{
	private static final String YYYYMMDD = "yyyyMMdd";

	public static String formatTime(Context context, long modifiedTime)
	{
		String result;

		CharSequence modifiedDay = DateFormat.format(YYYYMMDD, modifiedTime);
		CharSequence today = DateFormat.format(YYYYMMDD, System.currentTimeMillis());
		if (modifiedDay.equals(today))
		{
			java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
			result = timeFormat.format(new Date(modifiedTime));
		}
		else
		{
			java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(context);
			result = dateFormat.format(new Date(modifiedTime));
		}

		return result;
	}
}
