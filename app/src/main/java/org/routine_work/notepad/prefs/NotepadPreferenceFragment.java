package org.routine_work.notepad.prefs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.routine_work.notepad.R;

public class NotepadPreferenceFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.notepad_preference_root, rootKey);
	}
}
