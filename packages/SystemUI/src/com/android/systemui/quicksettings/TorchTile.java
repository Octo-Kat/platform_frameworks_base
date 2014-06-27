package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.android.internal.util.oct.TorchConstants;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;

public class TorchTile extends QuickSettingsTile {
    private boolean mActive = false;
    private boolean quickToggle = false;

    public TorchTile(Context context,
            QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    Intent i = new Intent(TorchConstants.ACTION_TOGGLE_STATE);
                    mContext.sendBroadcast(i);
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                } else {
                    startSettingsActivity(TorchConstants.INTENT_LAUNCH_APP);
                }
                return true;
            }
        };

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    startSettingsActivity(TorchConstants.INTENT_LAUNCH_APP);
                } else {
                    Intent i = new Intent(TorchConstants.ACTION_TOGGLE_STATE);
                    mContext.sendBroadcast(i);
                    if (isFlipTilesEnabled()) {
                        flipTile(0);
                    }
                }
            }
        };

        qsc.registerAction(TorchConstants.ACTION_STATE_CHANGED, this);
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
        if (mActive) {
            mDrawable = R.drawable.ic_qs_torch_on;
            mLabel = mContext.getString(R.string.quick_settings_torch);
        } else {
            mDrawable = R.drawable.ic_qs_torch_off;
            mLabel = mContext.getString(R.string.quick_settings_torch_off);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mActive = intent.getIntExtra(TorchConstants.EXTRA_CURRENT_STATE, 0) != 0;
        updateResources();
    }
}
