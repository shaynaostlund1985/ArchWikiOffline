/**
 * Copyright (C) 2012 Tetractys Productions LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Exiquio Cooper-Anderson (exiquio [at] gmail [dot] com)
 *
 */
package com.tetractysproductions.AWOL;

import java.io.File;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DefaultListActivity extends ListActivity {
	private static String TAG = "AWOL -  DEFAULT_LA";

	protected ArchWikiOfflineApplication app;
	protected String about_text;
	protected String error_text;
	protected Context context;
	protected ProgressDialog dialog;
	protected File config_dir;
	protected String wiki_filepath;

	// PROTECTED METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   	app = (ArchWikiOfflineApplication) getApplication();
   	context = this;
   	dialog = new ProgressDialog(context);
   	config_dir = new File(context.getFilesDir().getAbsoluteFile() + "/awol");
    Log.d(TAG, "config_dir: " + config_dir.getAbsolutePath());
    wiki_filepath = config_dir.getAbsolutePath() + "/archwiki.zim";
		about_text = getString(R.string.about);
		Log.d(TAG, "about_text: " + about_text);
		error_text = getString (R.string.error);
		Log.d(TAG, "error_text: " + error_text);
	}

	protected void toastAbout() {
		Log.d(TAG, "toastAbout called, making toast...");
		Toast toast = Toast.makeText(context, about_text, Toast.LENGTH_LONG);
		toast.show();
		Log.d(TAG, "done toasting!");
	}

	protected void toastError() {
		Log.d(TAG, "toastError called, making toast...");
		Toast toast = Toast.makeText(context, error_text, Toast.LENGTH_LONG);
		toast.show();
		Log.d(TAG, "done toasting!");
	}

	// PUBLIC METHODS
	@Override
  public boolean onSearchRequested() {
		Log.d(TAG, "onSearchRequested called...");
   	Log.d(TAG, "calling startSearch...");
    startSearch(null, false, null, false);
    Log.d(TAG, "startSearch called!");
    Log.d(TAG, "completed response to search request!");
    return true;
  }
}
