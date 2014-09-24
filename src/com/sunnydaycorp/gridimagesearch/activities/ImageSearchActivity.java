package com.sunnydaycorp.gridimagesearch.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.sunnydaycorp.gridimagesearch.R;
import com.sunnydaycorp.gridimagesearch.adapters.GoogleImagesGridItemAdapter;
import com.sunnydaycorp.gridimagesearch.fragments.UpdateSettingsFragment;
import com.sunnydaycorp.gridimagesearch.interfaces.GoogleImageSearchClientListener;
import com.sunnydaycorp.gridimagesearch.interfaces.OnSettingsUpdatedListener;
import com.sunnydaycorp.gridimagesearch.models.GoogleImage;
import com.sunnydaycorp.gridimagesearch.networking.GoogleImageSearchClient;

public class ImageSearchActivity extends Activity {

	public static final String IMAGE_EXTRA_TAG = "IMAGE";

	private GridView gvResults;
	private SearchView searchView;
	private MenuItem searchMenuItem;

	private GoogleImageSearchClient googleImageSearchClient;
	private GoogleImagesGridItemAdapter adapter;
	private InputMethodManager imm;

	private String query;

	private class MyOnScrollListener extends EndlessScrollListener {

		@Override
		public void onLoadMore(int page) {
			googleImageSearchClient.fetchGoogleImages(query, page);
		}

	};

	private GoogleImageSearchClientListener googleImageSearchClientListener = new GoogleImageSearchClientListener() {
		public void onGoogleImagesFetched(List<GoogleImage> newImages) {
			adapter.addAll(newImages);
		}

		public void onError(GoogleImageSearchClient.ResultCode resultCode) {
			switch (resultCode) {
			case FAILED_REQUEST:
				Toast.makeText(ImageSearchActivity.this, "Error connecting Google Image Search. Please try again", Toast.LENGTH_SHORT).show();
				break;
			case JSON_PARSING_EXCEPTION:
				Toast.makeText(ImageSearchActivity.this, "Error in fetchig images from Google", Toast.LENGTH_SHORT).show();
				break;
			case NO_INTERNET:
				Toast.makeText(ImageSearchActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
				break;
			case EXCEEDED_QPS:
				// this is a placeholder only user does not really need to know
				// it, may be load more action should be provided in this case
				Toast.makeText(ImageSearchActivity.this, "Exceeded QPS. Please try again", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	public OnSettingsUpdatedListener onSettingsUpdatedListener = new OnSettingsUpdatedListener() {

		@Override
		public void onSettingsUpdated(DialogInterface dialog) {
			if (query != null && !query.isEmpty()) {
				startNewImageSearch(query);
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		setupViews();

		googleImageSearchClient = new GoogleImageSearchClient(this, googleImageSearchClientListener);
		imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		List<GoogleImage> images = new ArrayList<GoogleImage>();
		adapter = new GoogleImagesGridItemAdapter(this, images);
		gvResults.setAdapter(adapter);
		gvResults.setOnScrollListener(new MyOnScrollListener());
		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(ImageSearchActivity.this, ImageDetailsActivity.class);
				i.putExtra(IMAGE_EXTRA_TAG, adapter.getItem(position));
				startActivity(i);
			}

		});

	}

	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_image_search, menu);
		searchMenuItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchMenuItem.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String newQuery) {
				startNewImageSearch(newQuery);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

		});

		searchMenuItem.expandActionView();
		searchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				searchView.onActionViewExpanded();
				if (query != null && searchView != null) {
					searchView.setQuery(query, false);
				}
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true;
			}

		});
		searchMenuItem.expandActionView();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showUpdateSettingsDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startNewImageSearch(String newQuery) {
		if (newQuery != null && !newQuery.isEmpty()) {
			query = newQuery;
			adapter.clear();
			gvResults.smoothScrollToPositionFromTop(0, 0);
			gvResults.setOnScrollListener(new MyOnScrollListener());
			googleImageSearchClient.fetchGoogleImages(query, 0);
			closeInputFromWindow();
		} else {
			Toast.makeText(this, "Please enter keyword(s)", Toast.LENGTH_SHORT).show();
		}
	}

	private void showUpdateSettingsDialog() {
		DialogFragment newFragment = new UpdateSettingsFragment(onSettingsUpdatedListener);
		newFragment.show(getFragmentManager(), "updateSettingsDialog");
	}

	private void closeInputFromWindow() {
		if (searchView != null) {
			searchView.clearFocus();
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		closeInputFromWindow();

	}
}
