package com.sunnydaycorp.gridimagesearch.interfaces;

import java.util.List;

import com.sunnydaycorp.gridimagesearch.models.GoogleImage;
import com.sunnydaycorp.gridimagesearch.networking.GoogleImageSearchClient;

public interface GoogleImageSearchClientListener {
	public void onGoogleImagesFetched(List<GoogleImage> images);

	public void onError(GoogleImageSearchClient.ResultCode resultCode);
}
