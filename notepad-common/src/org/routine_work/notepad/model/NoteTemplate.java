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
package org.routine_work.notepad.model;

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

	public long id;
	public String name;
	public String title;
	public String content;
	public boolean titleLocked;
	public boolean contentLocked;

	public void copyFrom(NoteTemplate other)
	{
		this.id = other.id;
		this.name = other.name;
		this.title = other.title;
		this.content = other.content;
		this.titleLocked = other.titleLocked;
		this.contentLocked = other.contentLocked;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 73 * hash + (this.title != null ? this.title.hashCode() : 0);
		hash = 73 * hash + (this.content != null ? this.content.hashCode() : 0);
		hash = 73 * hash + (this.titleLocked ? 1 : 0);
		hash = 73 * hash + (this.contentLocked ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final NoteTemplate other = (NoteTemplate) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
		{
			return false;
		}
		if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title))
		{
			return false;
		}
		if ((this.content == null) ? (other.content != null) : !this.content.equals(other.content))
		{
			return false;
		}
		if (this.titleLocked != other.titleLocked)
		{
			return false;
		}
		if (this.contentLocked != other.contentLocked)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "NoteTemplate{" + "id=" + id + ", name=" + name + ", title=" + title + ", content=" + content + ", titleLocked=" + titleLocked + ", contentLocked=" + contentLocked + '}';
	}

	public static void writeNoteTo(NoteTemplate note, File file) throws FileNotFoundException, IOException
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

	public static NoteTemplate readNoteFrom(File file) throws FileNotFoundException, IOException, ClassNotFoundException
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
