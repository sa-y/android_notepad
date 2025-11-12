/*
 *  The MIT License
 *
 *  Copyright 2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.routine_work.notepad.R;
import org.routine_work.utils.Log;

public class BackupDatabaseActivity extends Activity
		implements OnClickListener, BackupConstants {

	private static final String LOG_TAG = "simple-notepad";
	private final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1001;

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Log.v(LOG_TAG, "Hello");

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				enableBackupFunction();
			}
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(LOG_TAG, "Hello");
		setTheme(NotepadPreferenceUtils.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_database_activity);

		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		okButton.setEnabled(false);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);

		if (Build.VERSION.SDK_INT >= 23) { // Android 6.0  or later
			if (PermissionUtils.hasExternalStoragePermission(this) == false) {
				this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
			}
		}
		Log.v(LOG_TAG, "Bye");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(LOG_TAG, "Hello");

		if (PermissionUtils.hasExternalStoragePermission(this)) { // Android 6.0  or later
			Log.v(LOG_TAG, "hasWriteStoragePermission() : true");
			enableBackupFunction();
		} else {
			Log.v(LOG_TAG, "hasWriteStoragePermission() : false");
			disableBackupFunction();
		}

		Log.v(LOG_TAG, "Bye");
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
        if (id == R.id.ok_button) {
            startBackupDatabaseService();
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.cancel_button) {
            finish();
            setResult(RESULT_CANCELED);
        }
	}

	private void enableBackupFunction() {
		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setEnabled(true);

		View rationaleView =  findViewById(R.id.request_permission_rationale_external_storage_view);
		rationaleView.setVisibility(View.GONE);
	}

	private void disableBackupFunction() {
		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setEnabled(false);

		View rationaleView =  findViewById(R.id.request_permission_rationale_external_storage_view);
		rationaleView.setVisibility(View.VISIBLE);
	}

	private void startBackupDatabaseService() {
		Log.v(LOG_TAG, "Hello");

		if (PermissionUtils.hasExternalStoragePermission(this)) { // re check
			Intent backDatabaseIntent = new Intent(this, BackupDatabaseService.class);
			startService(backDatabaseIntent);
		}

		Log.v(LOG_TAG, "Bye");
	}

}
