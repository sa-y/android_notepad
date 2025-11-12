/*
 *  The MIT License
 *
 *  Copyright 2016 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.routine_work.notepad.prefs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import org.routine_work.utils.Log;

public class PermissionUtils {

	private static final String LOG_TAG = "simple-notepad";

	public static boolean hasExternalStoragePermission(Context context) {
		return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public static boolean hasPermission(Context context, String permission) {
		boolean result;
		Log.v(LOG_TAG, "Hello");

		if (Build.VERSION.SDK_INT >= 23) { // Android 6.0  or later
			if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = true;
		}

		Log.v(LOG_TAG, "Bye");
		return result;
	}

}
