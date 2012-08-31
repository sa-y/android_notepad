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
package org.routine_work.utils;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class IMEUtils
{

	public static void showSoftKeyboardWindow(Context context, View view)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	public static void hideSoftKeyboardWindow(Context context, View view)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void requestKeyboardFocus(EditText editText)
	{
		requestKeyboardFocus(editText, 200);
	}

	public static void requestKeyboardFocus(EditText editText, long delayInMillis)
	{
		final EditText targetEditText = editText;
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
//				long now = SystemClock.uptimeMillis();
//				targetEditText.dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0));
//				targetEditText.dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 0.0f, 0.0f, 0));
				targetEditText.requestFocus();
			}
		}, delayInMillis);
	}

	public static void requestKeyboardFocusByClick(EditText editText)
	{
		requestKeyboardFocusByClick(editText, 200);

	}

	public static void requestKeyboardFocusByClick(EditText editText, long delayInMillis)
	{
		final EditText targetEditText = editText;
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				long now = SystemClock.uptimeMillis();
				targetEditText.dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0));
				targetEditText.dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 0.0f, 0.0f, 0));
			}
		}, delayInMillis);
	}
}
