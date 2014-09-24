package com.sunnydaycorp.gridimagesearch.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunnydaycorp.gridimagesearch.R;
import com.sunnydaycorp.gridimagesearch.models.GoogleImage;

public class GoogleImagesGridItemAdapter extends ArrayAdapter<GoogleImage> {

	private DisplayMetrics outMetrics;

	private static class ViewHolder {
		ImageView ivTumbImage;
		TextView tvImageShortTitle;
	}

	public GoogleImagesGridItemAdapter(Context context, List<GoogleImage> images) {
		super(context, R.layout.google_image_grid_item, images);
		outMetrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GoogleImage image = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.google_image_grid_item, parent, false);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.ivTumbImage = (ImageView) convertView.findViewById(R.id.ivThumbImage);
			viewHolder.tvImageShortTitle = (TextView) convertView.findViewById(R.id.tvImageShortTitle);
			convertView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		// set image title
		if (image.getTitle() != null) {
			viewHolder.tvImageShortTitle.setText(Html.fromHtml(image.getTitle()));
		}

		// calculate and set image height and width to be displayed
		// if image dimensions less than 1/3 of display width then image is
		// enlarged and cropped to square if required
		// if image dimensions less than 1/3 of display width then image is
		// resized and cropped to fit the square
		int targetWidth = outMetrics.widthPixels / 3;
		int targetHeight = targetWidth;

		// reset image from recycled view
		viewHolder.ivTumbImage.setImageResource(0);

		viewHolder.ivTumbImage.getLayoutParams().height = targetHeight;
		viewHolder.ivTumbImage.getLayoutParams().width = targetWidth;

		// asynch send newtwork request, download, convert to bitmap
		Picasso.with(getContext()).load(image.getThumbUrl()).resize(targetWidth, targetWidth).centerCrop().into(viewHolder.ivTumbImage);

		return convertView;

	}
}
