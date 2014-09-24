package com.sunnydaycorp.gridimagesearch.activities;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessScrollListener implements OnScrollListener {

	private int visibleThreshold = 3;
	private int startingPageIndex = -1;

	private int lastLoadedPage = -1;
	private int previousTotalItemCount = 0;
	private boolean isLoading = true;

	public EndlessScrollListener() {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		// If the total item count is zero then the initial page is still
		// loading
		if (totalItemCount == 0) {
			this.isLoading = true;
			this.lastLoadedPage = this.startingPageIndex;
			this.previousTotalItemCount = totalItemCount;

		}

		// If previous total item count was higher than total item count
		// then the list is invalidated however
		// we do not know how many pages correspond to current totalItem Count
		// so this scenario does not give us correct lastLoadedPage number

		// If it’s still loading, we check to see if the dataset count has
		// changed, if so we conclude it has finished loading and update the
		// current page
		// number and total item count.
		if (isLoading && (totalItemCount > previousTotalItemCount)) {
			isLoading = false;
			previousTotalItemCount = totalItemCount;
			lastLoadedPage++;
		}

		// If it isn’t currently loading, we check to see if we have breached
		// the visibleThreshold and need to reload more data.
		// If we do need to reload some more data, we execute onLoadMore to
		// fetch the data.
		if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			onLoadMore(lastLoadedPage + 1);
			isLoading = true;
		}
	}

	public abstract void onLoadMore(int page);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}