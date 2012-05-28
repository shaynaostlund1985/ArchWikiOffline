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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tetractysproductions.AWOL.DefaultActivity;
import com.tetractysproductions.OfflineWiki.OfflineWikiReader;

public class DisplayPageActivity extends DefaultActivity {
	private static String TAG = "AWOL - LPA";

	private LoadWikiPageTask load_wiki_page_task = new LoadWikiPageTask();
	private FormatMarkupTask format_task = new FormatMarkupTask();
	private String page_title;
	private String live_url;
	private Boolean loaded = false;

	// PRIVATE METHODS
	private String getWikiPage(String wiki_filepath, String page_title) {
		String results = null;
		OfflineWikiReader owr = new OfflineWikiReader(wiki_filepath);
		if(page_title.equals("index")) {
			results = owr.getIndex();
		} else {
			results = owr.getPage(page_title);
		}
		Log.d(TAG, "results length: " + results.length());
		return results;
	}

	// PROTECTED METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	  setContentView(R.layout.display_page);
		Log.d(TAG, "DisplayPageActivity created...");

		Log.d(TAG, "unloading extras...");
	  Intent intent = getIntent();
	  if(intent.hasExtra("page_title")) {
	  	page_title = intent.getExtras().getString("page_title");
	  	live_url = "http://wiki.archlinux.org/index.php/" +
				page_title.replace(app.DONT_DISPLAY_ME, "").replaceAll(" ", "_");
	    Log.d(TAG, "page_title: " + page_title);
			Log.d(TAG, "extras unloaded!");

			Log.d(TAG, "getting wiki page...");
			load_wiki_page_task.execute(page_title);
			Log.d(TAG, "finished getting wiki page!");
	  } else {
	    Log.e(TAG, "page_title was not passed as an extra!");
			toastError();
	    finish();
	  }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		load_wiki_page_task.cancel(true);
		format_task.cancel(true);
	}

	// PUBLIC METHODS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "creating options menu...");
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.dp_a_menu, menu);
	  Log.d(TAG, "options menu created!");
	  return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "menu item selected...");
		Intent i;
		switch (item.getItemId()) {
			case R.id.menu_item_home:
				Log.d(TAG, "menu_item_home selected");
				Log.d(TAG, "lauching ArchWikiOfflineActivity");
				i = new Intent(context, ArchWikiOfflineActivity.class);
				startActivity(i);
				return true;
			case R.id.menu_item_search:
	      Log.d(TAG, "menu_item_search selected");
	      Log.d(TAG, "launching search");
	      onSearchRequested();
	      return true;
			case R.id.menu_item_live:
				Log.d(TAG, "menu_item_live selected");
				Log.d(TAG, "launching browser intent");
				i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(live_url));
				startActivity(i);
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

	// PRIVATE INNER CLASSES
	private class WikiWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    Log.d(TAG, "URL requested...");
	    Log.d(TAG, "URL: " + url);
			// REVIEW: Ensure this catches all external URLs.
	    if(url.contains("http://") || url.contains("https://")) {
	      Intent i = new Intent(Intent.ACTION_VIEW);
	      i.setData(Uri.parse(url));
	      startActivity(i);
	    } else {
	      Log.d(TAG, "The last url requested did not contain 'http'");
	    }
	    return true;
		}

	  @Override
	  public void onPageFinished(WebView webview, String url) {
			if(!loaded) {
				Log.d(TAG, "performing JavaScript operations...");
	   		format_task.execute(webview);
	   		Log.d(TAG, "JavaScript operations performed!");
	   		loaded = true;
	   	} else {
	   		Log.d(TAG, "page already loaded.");
	   	}
	  }
	}

	private class FormatMarkupTask extends AsyncTask<WebView, Void, String> {
		private WebView webview;

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "FomrmatMarkupTask started...");
			setProgressBarIndeterminateVisibility(true);
			dialog.setMessage("Formating markup... Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(WebView... webviews) {
			webview = webviews[0];
			Log.d(TAG, "creating scripts...");
	    String script = "document.getElementById('archnavbarmenu').innerHTML =" +
				" '<p>View article online at <a href=\"" + live_url +
				"\">ArchWiki</a>';" +
	    	"var elements = document.getElementById('bodyContent'" +
				").getElementsByTagName('div');"+
	    	"elements[0].parentNode.removeChild(elements[0]);" +
	    	"elements[0].parentNode.removeChild(elements[0]);" +
	    	"elements[0].parentNode.removeChild(elements[0]);" +
	    	"elements[0].removeChild(elements[0].getElementsByTagName('div')[0])" +
				";elements[1].parentNode.removeChild(elements[1]);" +
	    	"elements[1].parentNode.removeChild(elements[1]);" +
	    	"elements[1].parentNode.removeChild(elements[1]);" +
	    	"var c1 = document.getElementById('column-one');" +
	    	"c1.parentNode.removeChild(c1);" +
	    	"document.getElementById('footer').innerHTML = " +
				"'<p>Content is available under GNU Free Documentation License<p>'";
			Log.d(TAG, "JavaScript created");
			Log.d(TAG, "JavaScript:\n" + script);
	   	return "javascript:" + script;
		}

		@Override
		protected void onPostExecute(String script) {
			Log.d(TAG, "running scripts...");
			webview.loadUrl(script);
			Log.d(TAG, "scripts completed!");
			Log.d(TAG, "FormatMarkupTask completed!");
			dialog.dismiss();
		}
	}

	private class LoadWikiPageTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			dialog.setMessage("Loading page. Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... page_titles) {
			String html = null;
			html = getWikiPage(wiki_filepath, page_titles[0]);
	    return html;
		}

		@Override
		protected void onPostExecute(String html) {
			Log.d(TAG, "loading webview...");
	    WebView webview = (WebView) findViewById(R.id.webview);
	    webview.setWebViewClient(new WikiWebViewClient());
		  webview.setBackgroundColor(Color.parseColor("#F6F9FC"));

			WebSettings webview_settings = webview.getSettings();
		  webview_settings.setJavaScriptEnabled(true);
		  webview_settings.setLoadWithOverviewMode(true);
		  webview_settings.setUseWideViewPort(true);
		  webview_settings.setBuiltInZoomControls(true);
		  try {
				webview.loadData(
					URLEncoder.encode(html, "UTF-8").replaceAll("\\+"," "),
					"text/html",
					"utf-8"
				);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "error stacktrace:\n" + e.toString());
				toastError();
				finish();
			}
		    Log.d(TAG, "webview loaded!");
		}
	}
}


// HACK: This app would crash on certain changes that cause the app to be
// killed and restarted. At the moment the app is using a work around in
// AndroidManifest.xml (android:configChanges="orientation|keyboardHidden").
// This is just masking the problem and should be corrected like the example
// shown at the following URL:
//		http://goo.gl/3Et8F
// (exiquio)

// FIXME: Known Issues:
// -Local URLs do not load anything useful. (exiquio)
