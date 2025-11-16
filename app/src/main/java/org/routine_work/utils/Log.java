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
package org.routine_work.utils;

/**
 *
 * @author sawai
 */
public class Log
{

	public static final int CALLER_STACK_LEVEL = 4;
	public static final int VERBOSE = android.util.Log.VERBOSE;
	public static final int DEBUG = android.util.Log.DEBUG;
	public static final int INFO = android.util.Log.INFO;
	public static final int WARN = android.util.Log.WARN;
	public static final int ERROR = android.util.Log.ERROR;
	private static final int DEFAULT_OUTPUT_LEVEL = INFO;
	private static final boolean DEFAULT_TRACE_MODE = false;
	private static final boolean DEFAULT_INDENT_MODE = false;
	static int outputLevel = DEFAULT_OUTPUT_LEVEL;
	static boolean traceMode = DEFAULT_TRACE_MODE;
	static boolean indentMode = DEFAULT_INDENT_MODE;

	public static int getOutputLevel()
	{
		return outputLevel;
	}

	public static void setOutputLevel(int outputLevel)
	{
		Log.outputLevel = outputLevel;
	}

	public static boolean isTraceMode()
	{
		return traceMode;
	}

	public static void setTraceMode(boolean traceMode)
	{
		Log.traceMode = traceMode;
	}

	public static boolean isIndentMode()
	{
		return indentMode;
	}

	public static void setIndentMode(boolean indentMode)
	{
		Log.indentMode = indentMode;
	}

	public static void v(String tag, String message)
	{
		print(VERBOSE, tag, message, null);
	}

	public static void v(String tag, String message, Throwable ex)
	{
		print(VERBOSE, tag, message, ex);
	}

	public static void d(String tag, String message)
	{
		print(DEBUG, tag, message, null);
	}

	public static void d(String tag, String message, Throwable ex)
	{
		print(DEBUG, tag, message, ex);
	}

	public static void i(String tag, String message)
	{
		print(INFO, tag, message, null);
	}

	public static void i(String tag, String message, Throwable ex)
	{
		print(INFO, tag, message, ex);
	}

	public static void w(String tag, String message)
	{
		print(WARN, tag, message, null);
	}

	public static void w(String tag, String message, Throwable ex)
	{
		print(WARN, tag, message, ex);
	}

	public static void e(String tag, String message)
	{
		print(ERROR, tag, message, null);
	}

	public static void e(String tag, String message, Throwable ex)
	{
		print(ERROR, tag, message, ex);
	}

	public static void log(int priority, String tag, String message)
	{
		print(priority, tag, message, null);
	}

	public static void log(int priority, String tag, String message, Throwable ex)
	{
		print(priority, tag, message, ex);
	}

	private static void print(int priority, String tag, String message, Throwable ex)
	{
		if (priority < outputLevel)
		{
			return;
		}

		String log = message;
		if (traceMode)
		{
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			if (stackTraceElements.length > CALLER_STACK_LEVEL)
			{
				StringBuilder buffer = new StringBuilder();

				// append indent
				if (indentMode)
				{
					int indentCount = stackTraceElements.length - CALLER_STACK_LEVEL;
					for (int i = 0; i < indentCount; i++)
					{
						buffer.append(" ");
					}
				}

				// append stacktrace info
				StackTraceElement e = stackTraceElements[CALLER_STACK_LEVEL];
				buffer.append("[");
				buffer.append(e.getClassName());
				buffer.append("#");
				buffer.append(e.getMethodName());
				buffer.append("():");
				buffer.append(e.getLineNumber());
				buffer.append("] : ");
				buffer.append(message);

				log = buffer.toString();
			}
		}


		if (ex == null)
		{
			switch (priority)
			{
				case VERBOSE:
					android.util.Log.v(tag, log);
					break;
				case DEBUG:
					android.util.Log.d(tag, log);
					break;
				case INFO:
					android.util.Log.i(tag, log);
					break;
				case WARN:
					android.util.Log.w(tag, log);
					break;
				case ERROR:
					android.util.Log.e(tag, log);
					break;
			}
		}
		else
		{
			switch (priority)
			{
				case VERBOSE:
					android.util.Log.v(tag, log, ex);
					break;
				case DEBUG:
					android.util.Log.d(tag, log, ex);
					break;
				case INFO:
					android.util.Log.i(tag, log, ex);
					break;
				case WARN:
					android.util.Log.w(tag, log, ex);
					break;
				case ERROR:
					android.util.Log.e(tag, log, ex);
					break;
			}
		}
	}
}
