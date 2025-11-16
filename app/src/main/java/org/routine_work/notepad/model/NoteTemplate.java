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
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class NoteTemplate implements Serializable
{

	private long id;
	private String uuid = null;
	private boolean enabled = true;
	private String name;
	private String title;
	private String content;
	private boolean titleLocked = false;
	private boolean editSameTitle = true;

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

	public void copyFrom(NoteTemplate other)
	{
		this.setId(other.getId());
		this.setUuid(other.getUuid());
		this.setEnabled(other.isEnabled());
		this.setName(other.getName());
		this.setTitle(other.getTitle());
		this.setContent(other.getContent());
		this.setTitleLocked(other.isTitleLocked());
		this.setEditSameTitle(other.isEditSameTitle());
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
		hash = 53 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
		hash = 53 * hash + (this.enabled ? 1 : 0);
		hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
		hash = 53 * hash + (this.content != null ? this.content.hashCode() : 0);
		hash = 53 * hash + (this.titleLocked ? 1 : 0);
		hash = 53 * hash + (this.editSameTitle ? 1 : 0);
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
		if (this.id != other.id)
		{
			return false;
		}
		if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid))
		{
			return false;
		}
		if (this.enabled != other.enabled)
		{
			return false;
		}
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
		if (this.editSameTitle != other.editSameTitle)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "NoteTemplate{" + "id=" + id + ", uuid=" + uuid + ", enabled=" + enabled + ", name=" + name + ", title=" + title + ", content=" + content + ", titleLocked=" + titleLocked + ", editSameTitle=" + editSameTitle + '}';
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

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
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
	 * @return the editSameTitle
	 */
	public boolean isEditSameTitle()
	{
		return editSameTitle;
	}

	/**
	 * @param editSameTitle the editSameTitle to set
	 */
	public void setEditSameTitle(boolean editSameTitle)
	{
		this.editSameTitle = editSameTitle;
	}
}
