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
package org.routine_work.notepad.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplate implements Serializable
{

	long id;
	String title;
	String content;
	boolean titleLocked;
	boolean contentLocked;

	static void writeNoteTo(NoteTemplate note, File file) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		try
		{
			oos.writeObject(note);
		}
		finally
		{
			oos.close();
		}
	}

	static NoteTemplate readNoteFrom(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		NoteTemplate note = null;

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		try
		{
			note = (NoteTemplate) ois.readObject();
		}
		finally
		{
			ois.close();
		}

		return note;
	}
}
