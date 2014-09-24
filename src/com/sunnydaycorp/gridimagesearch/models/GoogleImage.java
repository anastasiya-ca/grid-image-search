package com.sunnydaycorp.gridimagesearch.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleImage implements Parcelable {

	private String title;
	private String fullUrl;
	private String thumbUrl;;
	private int imageHeight;
	private int imageWidth;

	public GoogleImage() {
	}

	public static GoogleImage fromJSON(JSONObject imageJSON) throws JSONException {
		GoogleImage image = new GoogleImage();
		image.setTitle(imageJSON.optString("title"));
		image.setFullUrl(imageJSON.optString("url"));
		image.setThumbUrl(imageJSON.optString("tbUrl"));
		image.setImageWidth(imageJSON.optInt("width"));
		image.setImageHeight(imageJSON.optInt("height"));
		return image;
	}

	// the assumption is that we want to process all images or none
	public static List<GoogleImage> fromJSONArray(JSONArray jsonImageArray) throws JSONException {
		ArrayList<GoogleImage> images = new ArrayList<GoogleImage>(jsonImageArray.length());
		for (int i = 0; i < jsonImageArray.length(); i++) {
			JSONObject imageJSON = jsonImageArray.getJSONObject(i);
			images.add(fromJSON(imageJSON));
		}
		return images;
	}

	public GoogleImage(Parcel in) {
		String[] data = new String[5];
		in.readStringArray(data);
		this.title = data[0];
		this.fullUrl = data[1];
		this.thumbUrl = data[2];
		this.imageHeight = Integer.valueOf(data[3]);
		this.imageWidth = Integer.valueOf(data[4]);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.title, this.fullUrl, this.thumbUrl, String.valueOf(this.imageHeight),
				String.valueOf(this.imageWidth) });
	}

	public static final Parcelable.Creator<GoogleImage> CREATOR = new Parcelable.Creator<GoogleImage>() {
		public GoogleImage createFromParcel(Parcel in) {
			return new GoogleImage(in);
		}

		public GoogleImage[] newArray(int size) {
			return new GoogleImage[size];
		}
	};

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String imageUrl) {
		this.fullUrl = imageUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String smallImageUrl) {
		this.thumbUrl = smallImageUrl;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

}
