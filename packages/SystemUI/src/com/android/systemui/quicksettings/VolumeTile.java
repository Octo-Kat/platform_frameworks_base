package com.android.systemui.quicksettings;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;

public class VolumeTile extends QuickSettingsTile {
    private boolean quickToggle = false;

    public VolumeTile(Context context,
            final QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnLongClick = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    qsc.mBar.collapseAllPanels(true);
                    AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    am.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                } else {
                    startSettingsActivity(android.provider.Settings.ACTION_SOUND_SETTINGS);
                }
                return true;
            }
        };

        mOnClick = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    startSettingsActivity(android.provider.Settings.ACTION_SOUND_SETTINGS);
                } else {
                    qsc.mBar.collapseAllPanels(true);
                    AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    am.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
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
        mDrawable = R.drawable.ic_qs_volume;
        mLabel = mContext.getString(R.string.quick_settings_volume);
    }
}
