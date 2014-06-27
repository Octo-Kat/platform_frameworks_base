package com.android.systemui.quicksettings;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;

public class SleepScreenTile extends QuickSettingsTile {

    private PowerManager pm;
    private boolean quickToggle = false;

    public SleepScreenTile(Context context, final QuickSettingsController qsc) {
        super(context, qsc);
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    qsc.mBar.collapseAllPanels(true);
                    pm.goToSleep(SystemClock.uptimeMillis());
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                } else {
                    startSettingsActivity("android.settings.DISPLAY_SETTINGS");
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
                    startSettingsActivity("android.settings.DISPLAY_SETTINGS");
                } else {
                    qsc.mBar.collapseAllPanels(true);
                    pm.goToSleep(SystemClock.uptimeMillis());
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
        super.updateResources();
    }

    private synchronized void updateTile() {
        mDrawable = R.drawable.ic_qs_sleep;
        mLabel = mContext.getString(R.string.quick_settings_screen_sleep);
    }

}
