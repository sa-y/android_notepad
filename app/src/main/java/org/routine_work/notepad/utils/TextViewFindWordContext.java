package org.routine_work.notepad.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextViewFindWordContext
{

	private static final String LOG_TAG = "simple-notepad";

	class FoundWord
	{

		int lineNumber;
		int startIndex;
		int endIndex;
	}

	private int foundWordBackgroundColor = 0xFF8B008B;
	private int foundWordForegroundColor = 0xFFFFFFFF;
	private int selectedWordBackgroundColor = 0xFFAA00AA;
	private int selectedWordForegroundColor = 0xFFFFFFFF;
	private String contentText = "";
	private String targetWord = "";
	private String[] contentTextLines;
	private FoundWord[] foundWords = new FoundWord[0];
	private int selectedWordIndex = 0;

	/**
	 * @return the foundWordBackgroundColor
	 */
	public int getFoundWordBackgroundColor()
	{
		return foundWordBackgroundColor;
	}

	/**
	 * @param foundWordBackgroundColor the foundWordBackgroundColor to set
	 */
	public void setFoundWordBackgroundColor(int foundWordBackgroundColor)
	{
		this.foundWordBackgroundColor = foundWordBackgroundColor;
	}

	/**
	 * @return the selectedWordBackgroundColor
	 */
	public int getSelectedWordBackgroundColor()
	{
		return selectedWordBackgroundColor;
	}

	/**
	 * @param selectedWordBackgroundColor the selectedWordBackgroundColor to set
	 */
	public void setSelectedWordBackgroundColor(int selectedWordBackgroundColor)
	{
		this.selectedWordBackgroundColor = selectedWordBackgroundColor;
	}

	/**
	 * @return the foundWordForegroundColor
	 */
	public int getFoundWordForegroundColor()
	{
		return foundWordForegroundColor;
	}

	/**
	 * @param foundWordForegroundColor the foundWordForegroundColor to set
	 */
	public void setFoundWordForegroundColor(int foundWordForegroundColor)
	{
		this.foundWordForegroundColor = foundWordForegroundColor;
	}

	/**
	 * @return the selectedWordForegroundColor
	 */
	public int getSelectedWordForegroundColor()
	{
		return selectedWordForegroundColor;
	}

	/**
	 * @param selectedWordForegroundColor the selectedWordForegroundColor to set
	 */
	public void setSelectedWordForegroundColor(int selectedWordForegroundColor)
	{
		this.selectedWordForegroundColor = selectedWordForegroundColor;
	}

	public void setTargetWord(CharSequence serachWord)
	{
		if (targetWord == null)
		{
			targetWord = "";
		}
		this.targetWord = serachWord.toString();
		this.selectedWordIndex = 0;
		updateContext();
	}

	public String getTargetWord()
	{
		return this.targetWord;
	}

	public void setContentText(CharSequence contentText)
	{
		if (contentText == null)
		{
			contentText = "";
		}
		this.contentText = contentText.toString();
		this.selectedWordIndex = 0;
		updateContext();
	}

	public void prevWord()
	{
		if (foundWords.length > 0)
		{
			selectedWordIndex--;
			if (selectedWordIndex < 0)
			{
				selectedWordIndex = foundWords.length - 1;
			}
		}
	}

	public void nextWord()
	{
		if (foundWords.length > 0)
		{
			selectedWordIndex++;
			if (selectedWordIndex >= foundWords.length)
			{
				selectedWordIndex = 0;
			}
		}
	}

	public int getContentTextLineCount()
	{
		int lineCount = 0;
		if (contentTextLines != null)
		{
			lineCount = contentTextLines.length;
		}
		return lineCount;
	}

	public int getFoundWordCount()
	{
		return foundWords.length;
	}

	public int getSelectedWordLineNumber()
	{
		int lineNumber = -1;
		if (foundWords.length > 0)
		{
			lineNumber = foundWords[selectedWordIndex].lineNumber;
		}
		return lineNumber;
	}

	private void updateContext()
	{
		contentTextLines = this.contentText.split("\n");

		List<FoundWord> foundWordList = new ArrayList<FoundWord>();
		if (TextUtils.isEmpty(this.targetWord) == false)
		{
			for (int i = 0; i < contentTextLines.length; i++)
			{
				String line = contentTextLines[i];
				Pattern p = Pattern.compile(this.targetWord, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(line);
				while (m.find())
				{
					FoundWord foundWord = new FoundWord();
					foundWord.lineNumber = i;
					foundWord.startIndex = m.start();
					foundWord.endIndex = m.end();
					foundWordList.add(foundWord);
//					Log.v(LOG_TAG, "foundWord => " + foundWord);
//					Log.v(LOG_TAG, "foundWord.startIndex => " + foundWord.startIndex);
//					Log.v(LOG_TAG, "foundWord.endIndex => " + foundWord.endIndex);
				}
			}
		}
		this.foundWords = foundWordList.toArray(new FoundWord[0]);
	}

	public Spannable getSpannable()
	{
//		Log.v(LOG_TAG, "selectedWordIndex => " + selectedWordIndex);
		SpannableStringBuilder builder = new SpannableStringBuilder();
		for (int i = 0; i < contentTextLines.length; i++)
		{
			int lineHeadIndex = builder.length();
			builder.append(this.contentTextLines[i]);

			for (int j = 0; j < foundWords.length; j++)
			{
				FoundWord foundWord = foundWords[j];
				if (foundWord.lineNumber == i)
				{
					int startIndex = lineHeadIndex + foundWord.startIndex;
					int endIndex = lineHeadIndex + foundWord.endIndex;
					if (j == selectedWordIndex)
					{
						builder.setSpan(new BackgroundColorSpan(getSelectedWordBackgroundColor()), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						builder.setSpan(new ForegroundColorSpan(getSelectedWordForegroundColor()), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else
					{
						builder.setSpan(new BackgroundColorSpan(getFoundWordBackgroundColor()), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						builder.setSpan(new ForegroundColorSpan(getFoundWordForegroundColor()), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}

			builder.append("\n");
		}

		return builder;
	}
}
