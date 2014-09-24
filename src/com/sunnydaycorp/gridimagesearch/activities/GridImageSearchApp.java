package com.sunnydaycorp.gridimagesearch.activities;

import android.app.Application;

import com.sunnydaycorp.gridimagesearch.models.SearchPreferences;

public class GridImageSearchApp extends Application {

	private SearchPreferences searchPreferences;

	public SearchPreferences getSearchPreferences() {
		return searchPreferences;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		searchPreferences = new SearchPreferences(this);
	}

}