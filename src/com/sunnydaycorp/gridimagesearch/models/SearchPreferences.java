package com.sunnydaycorp.gridimagesearch.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SearchPreferences {

	public static final String IMAGE_SIZE_KEY = "imagesize";
	public static final String COLOR_FILTER_KEY = "colorfilter";
	public static final String IMAGE_TYPE_KEY = "imagetype";
	public static final String SITE_NAME_KEY = "sitename";

	private String imageSize;
	private String colorFilter;
	private String imageType;
	private String siteName;
	private final SharedPreferences pref;

	public SearchPreferences(Context context) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		imageSize = pref.getString(IMAGE_SIZE_KEY, "");
		colorFilter = pref.getString(COLOR_FILTER_KEY, "");
		imageType = pref.getString(IMAGE_TYPE_KEY, "");
		siteName = pref.getString(SITE_NAME_KEY, "");
	}

	public boolean savePreferences(String imageSize, String colorFilter, String imageType, String siteName) {
		this.imageSize = imageSize;
		this.colorFilter = colorFilter;
		this.imageType = imageType;
		this.siteName = siteName;

		Editor prefEditor = pref.edit();
		prefEditor.putString(IMAGE_SIZE_KEY, imageSize);
		prefEditor.putString(COLOR_FILTER_KEY, colorFilter);
		prefEditor.putString(IMAGE_TYPE_KEY, imageType);
		prefEditor.putString(SITE_NAME_KEY, siteName);
		prefEditor.commit();
		return true;
	}

	public String getImageSize() {
		return imageSize;
	}

	public String getColorFilter() {
		return colorFilter;
	}

	public String getImageType() {
		return imageType;
	}

	public String getSiteName() {
		return siteName;
	}

}
