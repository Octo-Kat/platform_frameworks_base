package com.android.systemui.quicksettings;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;

@SuppressWarnings("deprecation")
public class ToggleLockscreenTile extends QuickSettingsTile
        implements OnSharedPreferenceChangeListener {

    private static final String KEY_DISABLED = "lockscreen_disabled";

    private static KeyguardLock sLock = null;
    private static int sLockTileCount = 0;
    private static boolean sDisabledLockscreen = false;
    private boolean quickToggle = false;

    public ToggleLockscreenTile(Context context, QuickSettingsController qsc) {
        super(context, qsc);

        mOnLongClick = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    sDisabledLockscreen = !sDisabledLockscreen;
                    mPrefs.edit().putBoolean(KEY_DISABLED, sDisabledLockscreen).apply();
                    updateLockscreenState();
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                } else {
                    startSettingsActivity("android.settings.SECURITY_SETTINGS");
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
                    startSettingsActivity("android.settings.SECURITY_SETTINGS");
                } else {
                    sDisabledLockscreen = !sDisabledLockscreen;
                    mPrefs.edit().putBoolean(KEY_DISABLED, sDisabledLockscreen).apply();
                    updateLockscreenState();
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                }
            }
        };
    }

    @Override
    void onPostCreate() {
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        if (sLockTileCount == 0) {
            sDisabledLockscreen = mPrefs.getBoolean(KEY_DISABLED, false);
            updateLockscreenState();
        }
        sLockTileCount++;
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void onDestroy() {
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        sLockTileCount--;
        if (sLock != null && sLockTileCount < 1 && sDisabledLockscreen) {
            sLock.reenableKeyguard();
            sLock = null;
        }
        super.onDestroy();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        mLabel = mContext.getString(R.string.quick_settings_lockscreen);
        mDrawable = sDisabledLockscreen ?
                R.drawable.ic_qs_lock_screen_off : R.drawable.ic_qs_lock_screen_on;
    }

    private void updateLockscreenState() {
        if (sLock == null) {
            KeyguardManager keyguardManager = (KeyguardManager)
                    mContext.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            sLock = keyguardManager.newKeyguardLock("LockscreenTile");
        }
        if (sDisabledLockscreen) {
            sLock.disableKeyguard();
        } else {
            sLock.reenableKeyguard();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_DISABLED.equals(key)) {
            updateResources();
        }
    }
}
