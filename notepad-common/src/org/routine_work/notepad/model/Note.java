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
public class Note implements Serializable
{

	private long id = 0;
	private String uuid = null;
	private String title = "";
	private String content = "";
	private boolean titleLocked = false;
	private long added = 0;
	private long modified = 0;

	public static void writeNoteTo(Note note, File file) throws FileNotFoundException, IOException
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

	public static Note readNoteFrom(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		Note note = null;

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		try
		{
			note = (Note) ois.readObject();
		}
		finally
		{
			ois.close();
		}

		return note;
	}

	/**
	 * @return the id
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * @return the UUID
	 */
	public String getUuid()
	{
		return uuid;
	}

	/**
	 * @param id the id to set
	 */
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * @return the titleLocked
	 */
	public boolean isTitleLocked()
	{
		return titleLocked;
	}

	/**
	 * @param titleLocked the titleLocked to set
	 */
	public void setTitleLocked(boolean titleLocked)
	{
		this.titleLocked = titleLocked;
	}

	/**
	 * @return the added
	 */
	public long getAdded()
	{
		return added;
	}

	/**
	 * @param added the added to set
	 */
	public void setAdded(long added)
	{
		this.added = added;
	}

	/**
	 * @return the modified
	 */
	public long getModified()
	{
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(long modified)
	{
		this.modified = modified;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash + (int) (this.id ^ (this.id >>> 32));
		hash = 31 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
		hash = 31 * hash + (this.title != null ? this.title.hashCode() : 0);
		hash = 31 * hash + (this.content != null ? this.content.hashCode() : 0);
		hash = 31 * hash + (this.titleLocked ? 1 : 0);
		hash = 31 * hash + (int) (this.added ^ (this.added >>> 32));
		hash = 31 * hash + (int) (this.modified ^ (this.modified >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Note other = (Note) obj;
		if (this.id != other.id)
			return false;
		if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid))
			return false;
		if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title))
			return false;
		if ((this.content == null) ? (other.content != null) : !this.content.equals(other.content))
			return false;
		if (this.titleLocked != other.titleLocked)
			return false;
		if (this.added != other.added)
			return false;
		if (this.modified != other.modified)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Note{" + "id=" + id + ", uuid=" + uuid + ", title=" + title + ", content=" + content + ", titleLocked=" + titleLocked + ", added=" + added + ", modified=" + modified + '}';
	}

	public void copyFrom(Note other)
	{
		this.setId(other.getId());
		this.setUuid(other.getUuid());
		this.setTitle(other.getTitle());
		this.setContent(other.getContent());
		this.setTitleLocked(other.isTitleLocked());
		this.setAdded(other.getAdded());
		this.setModified(other.getModified());
	}
}
