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

import java.util.List;

import com.tetractysproductions.OfflineWiki.OfflineWikiReader;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchWikiActivity extends ListActivity {
	// CONSTANTS
	private static String TAG = "AWOL - SWA";
	private static CharSequence ABOUT_TEXT = "AWOL: ArchWiki Offline - Copyright (C) 2012 Tetractys Productions. All rights reserved. Written by Exiquio Cooper-Anderson. GPLv3 (http://www.gnu.org/licenses/)";
	private static String DONT_DISPLAY_ME = " - ArchWiki.html";
	
	// PRIVATE INSTANCE VARIABLES
	private Context context;
	private ProgressDialog dialog;
	private String wiki_filepath;
	private String query;
	
	// LIFECYCLE CALLBACKS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    context = this;
	    dialog = new ProgressDialog(context);
		Log.d(TAG, "SearchWikiActivity created...");

	    Log.d(TAG, "unloading search variables...");
	    Intent intent = getIntent();
	    final Bundle app_data = intent.getBundleExtra(SearchManager.APP_DATA);
	    if(app_data != null) {
	    	wiki_filepath = app_data.getString("wiki_filepath");
	    	Log.d(TAG, "wiki_filepath: " + wiki_filepath);
	    } else {
	    	Log.w(TAG, "app_data is null!"); // FIXME Handle problems like wiki_file being null (exiquio)
	    }
	    if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    	Log.d(TAG, "query: " + query);
	        Log.d(TAG, "search variables unloaded!");
	    }
		
		Log.d(TAG, "starting SearchWikiTask...");
		SearchWikiTask task = new SearchWikiTask();
		task.execute(query);
		Log.d(TAG, "SearchWikiTask should have reported complete!");
	}
	
	// PUBLIC INSTANCE METHODS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "creating options menu...");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sw_a_menu, menu);
	    Log.d(TAG, "options menu created!");
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "menu item selected...");
		switch (item.getItemId()) {
			case R.id.menu_item_home:
				Log.d(TAG, "menu_item_home selected");
				Log.d(TAG, "lauching ArchWikiOfflineActivity");
				Intent i = new Intent(context, ArchWikiOfflineActivity.class);                      
				startActivity(i);
				return true;
			case R.id.menu_item_search:
	        	Log.d(TAG, "menu_item_search selected");
	        	Log.d(TAG, "launching search");
	        	onSearchRequested();
	            return true;
	        case R.id.menu_item_about:
	        	Log.d(TAG, "menu_item_about_selected");
	        	toastAbout();
	            return true;
	        default:
	        	Log.d(TAG, "default, calling fallback");
	            return super.onOptionsItemSelected(item);
	    }
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
		Toast toast = Toast.makeText(context, ABOUT_TEXT, Toast.LENGTH_LONG);
		toast.show();	
		Log.d(TAG, "done toasting!");
	}
	
	// PRIVATE INSTANCE METHODS
	private void displayPage(String page_title) {
    	Log.d(TAG, "starting displayPage...");
    	Log.d(TAG, "page_title: " + page_title);
    	Intent intent = new Intent(getBaseContext(), DisplayPageActivity.class);
    	Log.d(TAG, "packing extras...");
    	intent.putExtra("wiki_filepath", wiki_filepath);
    	intent.putExtra("page_title", page_title);
    	Log.d(TAG, "extras packed!");
    	Log.d(TAG, "calling DisplayPageActivity...");
    	startActivity(intent);
    	Log.d(TAG, "displayPage done!");
    }
	
	private List<String> searchWiki(String wiki_filepath, String query, boolean ignore_case) {
		List<String> results = null;
		OfflineWikiReader owr = new OfflineWikiReader(wiki_filepath);
		results = owr.search(query, ignore_case);
		return results;
	}
	
	// PRIVATE INNER CLASSES
	private class SearchWikiTask extends AsyncTask<String, Void, List<String>> {
		@Override 
		protected void onPreExecute() {
			Log.d(TAG, "SearchWikiTask has started...");
			dialog.setMessage("Loading search results. Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}
		
		@Override
		protected List<String> doInBackground(String... queries) {
			Log.d(TAG, "start searchWiki!");
			Log.d(TAG, "wiki_filepath: " + wiki_filepath);
			List<String> results = null;
	    	results = searchWiki(wiki_filepath, queries[0], true);
	    	for(int i = 0; i < results.size(); i++) {
	    		results.set(i, results.get(i).replace(DONT_DISPLAY_ME, ""));
	    	}
	    	Log.d(TAG, "finished searchWiki!");
	    	return results;
		}
		
		@Override
		protected void onPostExecute(List<String> results) {
			setListAdapter(new ArrayAdapter<String>(context, R.layout.search_result_item, results));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(
				new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String result_text = (String) ((TextView) view).getText();
						Log.d(TAG, "result text: " + result_text);
						displayPage(result_text + DONT_DISPLAY_ME);
					}
				}
			);
			dialog.dismiss();
			Log.d(TAG, "SearchWikiTask has completed!");
		}	
	}
}