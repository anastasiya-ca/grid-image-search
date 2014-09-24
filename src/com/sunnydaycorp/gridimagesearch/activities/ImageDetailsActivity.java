package com.sunnydaycorp.gridimagesearch.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;
import com.squareup.picasso.Picasso.Listener;
import com.sunnydaycorp.gridimagesearch.R;
import com.sunnydaycorp.gridimagesearch.models.GoogleImage;
import com.sunnydaycorp.gridimagesearch.networking.GoogleImageSearchClient;

public class ImageDetailsActivity extends Activity {

	public static final String LOG_TAG_CLASS = GoogleImageSearchClient.class.getSimpleName();
	public static final String IMAGE_LOADING_ERROR_MESSAGE = "Sorry full image is not available";

	private ImageView ivImage;
	private TextView tvImageTitle;
	private TextView tvLoadingImageErrorMsg;
	private ProgressBar pbLoadingImage;

	private ShareActionProvider shareAction;
	private MenuItem shareMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_details);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		setupViews();
		GoogleImage googleImage = (GoogleImage) getIntent().getParcelableExtra(ImageSearchActivity.IMAGE_EXTRA_TAG);

		tvImageTitle.setText(Html.fromHtml(googleImage.getTitle()));

		// calculate and set image dimensions
		int height = googleImage.getImageHeight();
		int width = googleImage.getImageWidth();
		double ratio = (double) height / (double) (width);

		DisplayMetrics outMetrics = new DisplayMetrics();
		((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
		int targetWidth = outMetrics.widthPixels;
		int targetHeight = (int) (targetWidth * ratio);

		ivImage.getLayoutParams().width = targetWidth;
		ivImage.getLayoutParams().height = targetHeight;

		// asynch send newtwork request, download, convert to bitmap
		// Picasso picasso = Picasso.with(this);
		Picasso picasso = new Builder(this).listener(new Listener() {

			@Override
			public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
				Log.e(LOG_TAG_CLASS, uri + " " + exception.toString());
			}

		}).build();
		picasso.setLoggingEnabled(true);

		pbLoadingImage.setVisibility(View.VISIBLE);
		picasso.load(googleImage.getFullUrl()).resize(targetWidth, targetHeight).centerInside().error(R.drawable.ic_launcher)
				.into(ivImage, new Callback() {

					@Override
					public void onError() {
						pbLoadingImage.setVisibility(View.GONE);
						tvLoadingImageErrorMsg.setText(IMAGE_LOADING_ERROR_MESSAGE);
					}

					@Override
					public void onSuccess() {
						pbLoadingImage.setVisibility(View.GONE);
						setupShareIntent();
					}
				});
	}

	private void setupShareIntent() {
		Uri bmpUri = getLocalBitmapUri();
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		shareIntent.setType("image/*");
		shareAction.setShareIntent(shareIntent);
	}

	private Uri getLocalBitmapUri() {
		Drawable drawable = ivImage.getDrawable();
		Bitmap bmp = null;
		if (drawable instanceof BitmapDrawable) {
			bmp = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
		} else {
			return null;
		}

		Uri bmpUri = null;
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image_"
					+ System.currentTimeMillis() + ".png");
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_image_details, menu);
		shareMenuItem = menu.findItem(R.id.action_sharing_image);
		shareAction = (ShareActionProvider) shareMenuItem.getActionProvider();
		shareAction.setShareIntent(new Intent());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_sharing_image) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupViews() {
		ivImage = (ImageView) findViewById(R.id.ivImage);
		tvImageTitle = (TextView) findViewById(R.id.tvImageTitle);
		tvLoadingImageErrorMsg = (TextView) findViewById(R.id.tvLoadingImageErrorMsg);
		pbLoadingImage = (ProgressBar) findViewById(R.id.pbLoadingImage);
	}

}
