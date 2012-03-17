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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DefaultActivity extends Activity {
	private static String TAG = "AWOL - DEFAULT_A";
	private CharSequence about_text;
	protected Context context;
	protected ProgressDialog dialog;
	protected File config_dir;
	protected String wiki_filepath;

	// PUBLIC METHODS
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	about_text = "AWOL: ArchWiki Offline (" + getString(R.string.app_version)  + ") - (C) 2012 Tetractys Productions. All rights reserved.";
    	context = this;
    	dialog = new ProgressDialog(context);
    	config_dir = new File(context.getFilesDir().getAbsoluteFile() + "/awol");
        Log.d(TAG, "config_dir: " + config_dir.getAbsolutePath());
        wiki_filepath = config_dir.getAbsolutePath() + "/archwiki.zim";
	}
	
	@Override
    public boolean onSearchRequested() {
		Log.d(TAG, "onSearchRequested called...");
		Log.d(TAG, "loading bundle...");
    	Bundle app_data = new Bundle();
    	app_data.putString("wiki_filepath", wiki_filepath);
    	Log.d(TAG, "bundle loaded!");
    	Log.d(TAG, "calling startSearch...");
        startSearch(null, false, app_data, false);
        Log.d(TAG, "startSearch called!");
        Log.d(TAG, "completed response to search request!");
        return true;
    }
	
	public void toastAbout() {
		Log.d(TAG, "toastAbout called, making toast...");
		Toast toast = Toast.makeText(context, about_text, Toast.LENGTH_LONG);
		toast.show();	
		Log.d(TAG, "done toasting!");
	}
}
