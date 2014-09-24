package com.sunnydaycorp.gridimagesearch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sunnydaycorp.gridimagesearch.R;
import com.sunnydaycorp.gridimagesearch.activities.GridImageSearchApp;
import com.sunnydaycorp.gridimagesearch.interfaces.OnSettingsUpdatedListener;
import com.sunnydaycorp.gridimagesearch.models.SearchPreferences;

public class UpdateSettingsFragment extends DialogFragment {

	private OnSettingsUpdatedListener onSettingsUpdatedListener;
	private InputMethodManager imm;
	private SearchPreferences searchPreferences;

	private Spinner spImageSize;
	private Spinner spColorFilter;
	private Spinner spImageType;
	private EditText etSiteName;

	private Button btnSave;

	public UpdateSettingsFragment() {
	}

	public UpdateSettingsFragment(OnSettingsUpdatedListener onSettingsUpdatedListener) {
		this.onSettingsUpdatedListener = onSettingsUpdatedListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		searchPreferences = ((GridImageSearchApp) getActivity().getApplicationContext()).getSearchPreferences();

		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialog);

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_settings, null);
		builder.setView(view);

		// set up initial values for fields
		spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
		spColorFilter = (Spinner) view.findViewById(R.id.spColorFilter);
		spImageType = (Spinner) view.findViewById(R.id.spImageType);
		etSiteName = (EditText) view.findViewById(R.id.etSiteName);

		setSpinnerSelection(spImageSize, searchPreferences.getImageSize());
		setSpinnerSelection(spColorFilter, searchPreferences.getColorFilter());
		setSpinnerSelection(spImageType, searchPreferences.getImageType());
		etSiteName.setText(searchPreferences.getSiteName());

		etSiteName.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String siteNameText = etSiteName.getText().toString().trim();
				if (!siteNameText.isEmpty() && !Patterns.WEB_URL.matcher(siteNameText).matches()) {
					etSiteName.setError("Please enter a valid site name or none", getResources().getDrawable(R.drawable.ic_action_error));
					if (btnSave != null) {
						btnSave.setEnabled(false);
					}
				} else {
					etSiteName.setError(null);
					if (btnSave != null) {
						btnSave.setEnabled(true);
					}
				}
			}
		});

		builder.setTitle(R.string.update_settings_dialog_title);
		builder.setPositiveButton(R.string.save_button_label, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String imageSize = spImageSize.getSelectedItem().toString();
				String colorFilter = spColorFilter.getSelectedItem().toString();
				String imageType = spImageType.getSelectedItem().toString();
				String siteName = etSiteName.getText().toString().trim();

				searchPreferences.savePreferences(imageSize, colorFilter, imageType, siteName);
				closeInputFromWindow();
				if (onSettingsUpdatedListener != null) {
					onSettingsUpdatedListener.onSettingsUpdated(dialog);
				}
			}
		});

		builder.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				closeInputFromWindow();
			}
		});

		return builder.create();

	}

	private void setSpinnerSelection(Spinner spinner, String value) {
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
		int position = adapter.getPosition(value);
		if (position < 0) {
			position = 0;
		}
		spinner.setSelection(position);

	}

	private void closeInputFromWindow() {
		if (etSiteName != null) {
			imm.hideSoftInputFromWindow(etSiteName.getWindowToken(), 0);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = this.getDialog();

		// update Title styling
		int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
		View titleDividerView = dialog.findViewById(titleDividerId);
		if (titleDividerView != null) {
			titleDividerView.setBackgroundColor(getResources().getColor(R.color.custom_actionbar_color));
		}

		// initialize btnSave
		btnSave = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
	}

}
