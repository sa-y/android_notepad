package org.routine_work.notepad.utils;

import android.graphics.Color;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;

import org.routine_work.notepad.R;
import org.routine_work.notepad.prefs.NotepadPreferenceUtils;

/**
 * Utility class for Edge-to-Edge setup.
 */
public class EdgeToEdgeUtils
{
	/**
	 * Setup Edge-to-Edge for the given activity based on the current theme.
	 *
	 * @param activity The activity to setup.
	 */
	public static void setup(ComponentActivity activity)
	{
		int themeId = NotepadPreferenceUtils.getTheme(activity);
		activity.setTheme(themeId);
		if (themeId == R.style.Theme_Notepad_Dark)
		{
			EdgeToEdge.enable(activity, SystemBarStyle.dark(Color.TRANSPARENT));
		}
		else
		{
			EdgeToEdge.enable(activity, SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
		}
	}
}
