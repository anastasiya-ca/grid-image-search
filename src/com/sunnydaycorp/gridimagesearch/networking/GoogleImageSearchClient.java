package com.sunnydaycorp.gridimagesearch.networking;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sunnydaycorp.gridimagesearch.activities.GridImageSearchApp;
import com.sunnydaycorp.gridimagesearch.interfaces.GoogleImageSearchClientListener;
import com.sunnydaycorp.gridimagesearch.models.GoogleImage;
import com.sunnydaycorp.gridimagesearch.models.SearchPreferences;

public class GoogleImageSearchClient {

	public static final String LOG_TAG_CLASS = GoogleImageSearchClient.class.getSimpleName();
	public static final String LOG_TAG_URL = "URL";
	public static final int PAGE_SIZE = 8;

	public static final String GOOGLE_IMAGE_SEARCH_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=" + PAGE_SIZE;
	public static final String GOOGLE_IMAGE_QUERY_PARAMETER_VALUE = "q";
	public static final String GOOGLE_IMAGE_SIZE_PARAMETER_VALUE = "imgsz";
	public static final String GOOGLE_IMAGE_COLOR_PARAMETER_VALUE = "imgcolor";
	public static final String GOOGLE_IMAGE_TYPE_PARAMETER_VALUE = "imgtype";
	public static final String GOOGLE_SITE_SEARCH_PARAMETER_VALUE = "as_sitesearch";
	public static final String GOOGLE_SEARCH_START_VALUE = "start";
	public static final String OUT_OF_RANGE_RESPONSE_DETAILS_TEXT = "out of range start";
	public static final String QPS_RATE_EXCEEDED_RESPONSE_DETAILS_TEXT = "qps rate exceeded";

	public enum ResultCode {
		JSON_PARSING_EXCEPTION, FAILED_REQUEST, NO_INTERNET, EXCEEDED_QPS
	};

	private final AsyncHttpClient client;
	private final GoogleImageSearchClientListener listener;
	private final Context context;

	public GoogleImageSearchClient(Context context, GoogleImageSearchClientListener listener) {
		this.context = context;
		this.listener = listener;
		this.client = new AsyncHttpClient();
	}

	public void fetchGoogleImages(String query, int page) {
		// check for Internet connection up to 3 times
		for (int i = 0; i < 3; i++) {
			if (isNetworkAvailable()) {
				break;
			} else if (i == 2) {
				Log.w(LOG_TAG_CLASS, "No Internet connection");
				onError(ResultCode.NO_INTERNET);
				return;
			}
		}

		client.get(buildURL(query, page), new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					List<GoogleImage> images = new ArrayList<GoogleImage>();

					// out of range start response
					if (response.isNull("responseData") && !response.isNull("responseDetails")
							&& response.optString("responseDetails").equals(OUT_OF_RANGE_RESPONSE_DETAILS_TEXT)) {
						// do nothing
					} else if (response.isNull("responseData") && !response.isNull("responseDetails")
							&& response.optString("responseDetails").equals(QPS_RATE_EXCEEDED_RESPONSE_DETAILS_TEXT)) {
						// TODO wait and re-try request or allow user to reload
						// the page
						Log.e(LOG_TAG_CLASS, "QPS rate exceeded response received");
						onError(ResultCode.EXCEEDED_QPS);
					}

					else {
						JSONArray imagesJSON = response.getJSONObject("responseData").getJSONArray("results");
						images = GoogleImage.fromJSONArray(imagesJSON);
						if (listener != null) {
							listener.onGoogleImagesFetched(images);
						}
					}

				} catch (JSONException e) {
					Log.e(LOG_TAG_CLASS, "Error processing JSON response from Google Images Search", e);
					onError(ResultCode.JSON_PARSING_EXCEPTION);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
				Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
				onError(ResultCode.FAILED_REQUEST);
			}
		});
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	private void onError(ResultCode resultCode) {
		if (listener != null) {
			listener.onError(resultCode);
		}
	}

	private String buildURL(String query, int page) {
		SearchPreferences searchPreferences = ((GridImageSearchApp) context.getApplicationContext()).getSearchPreferences();

		String size = searchPreferences.getImageSize();
		String color = searchPreferences.getColorFilter();
		String type = searchPreferences.getImageType();
		String siteName = searchPreferences.getSiteName();

		Uri.Builder builder = Uri.parse(GOOGLE_IMAGE_SEARCH_URL).buildUpon();
		builder.appendQueryParameter(GOOGLE_IMAGE_QUERY_PARAMETER_VALUE, query);
		if (!size.isEmpty() && !size.equals("any")) {
			builder.appendQueryParameter(GOOGLE_IMAGE_SIZE_PARAMETER_VALUE, size);
		}
		if (!color.isEmpty() && !color.equals("any")) {
			builder.appendQueryParameter(GOOGLE_IMAGE_COLOR_PARAMETER_VALUE, color);
		}
		if (!type.isEmpty() && !type.equals("any")) {
			builder.appendQueryParameter(GOOGLE_IMAGE_TYPE_PARAMETER_VALUE, type);
		}
		if (!siteName.isEmpty() && !siteName.equals("any")) {
			builder.appendQueryParameter(GOOGLE_SITE_SEARCH_PARAMETER_VALUE, siteName);
		}
		builder.appendQueryParameter(GOOGLE_SEARCH_START_VALUE, String.valueOf(page * PAGE_SIZE));
		String URL = builder.build().toString();

		Log.i(LOG_TAG_URL, URL);
		return URL;
	}

}
