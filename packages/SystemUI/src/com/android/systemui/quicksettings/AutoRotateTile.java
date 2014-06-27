package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.internal.view.RotationPolicy;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;

public class AutoRotateTile extends QuickSettingsTile {
    private boolean quickToggle = false;

    public AutoRotateTile(Context context, QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    RotationPolicy.setRotationLock(mContext, getAutoRotation());
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.settings",
                        "com.android.settings.Settings$DisplayRotationSettingsActivity");
                    startSettingsActivity(intent);
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
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.settings",
                        "com.android.settings.Settings$DisplayRotationSettingsActivity");
                    startSettingsActivity(intent);
                } else {
                    RotationPolicy.setRotationLock(mContext, getAutoRotation());
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                }
            }
        };

        qsc.registerObservedContent(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION)
                , this);
    }

    @Override
    public void updateResources() {
        updateTile();
        updateQuickSettings();
    }

    private synchronized void updateTile() {
        if(!getAutoRotation()){
            mDrawable = R.drawable.ic_qs_rotation_locked;
            mLabel = mContext.getString(R.string.quick_settings_rotation_locked_label);
        }else{
            mDrawable = R.drawable.ic_qs_auto_rotate;
            mLabel = mContext.getString(R.string.quick_settings_rotation_unlocked_label);
        }
    }

    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    private boolean getAutoRotation() {
        return !RotationPolicy.isRotationLocked(mContext);
    }

    @Override
    public void onChangeUri(ContentResolver resolver, Uri uri) {
        updateResources();
    }
}
