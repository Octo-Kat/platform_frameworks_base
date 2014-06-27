package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationController.LocationSettingsChangeCallback;


public class GPSTile extends QuickSettingsTile implements LocationSettingsChangeCallback {

    ContentResolver mContentResolver;
    private LocationController mLocationController;
    private boolean mLocationEnabled;
    private boolean quickToggle = false;

    public GPSTile(Context context, QuickSettingsController qsc) {
        super(context, qsc);

        mContentResolver = mContext.getContentResolver();
        mLocationController = new LocationController(mContext);
        mLocationController.addSettingsChangedCallback(this);

        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
	                mLocationController.setLocationEnabled(!mLocationEnabled);
        	        if (isFlipTilesEnabled()) {
                	    flipTile(0);
	                }
		} else {
	                startSettingsActivity(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		}
                return true;
            }
        };

        mOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
	                startSettingsActivity(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		} else {
                        mLocationController.setLocationEnabled(!mLocationEnabled);
                        if (isFlipTilesEnabled()) {
                            flipTile(0);
                        }
		}
            }
        };
    }

    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        updateQuickSettings();
    }

    private synchronized void updateTile() {
        int textResId = mLocationEnabled ? R.string.quick_settings_location_label
                : R.string.quick_settings_location_off_label;
        mLabel = mContext.getText(textResId).toString();
        mDrawable = mLocationEnabled
                ? R.drawable.ic_qs_location_on : R.drawable.ic_qs_location_off;
    }

    @Override
    public void onLocationSettingsChanged(boolean locationEnabled) {
        mLocationEnabled = locationEnabled;
        updateResources();
    }
}
