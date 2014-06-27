package com.android.systemui.quicksettings;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.policy.NetworkController;

public class WiFiTile extends NetworkTile {
    private boolean mWifiConnected;
    private boolean mWifiNotConnected;
    private int mWifiSignalIconId;
    private String mDescription;
    private boolean quickToggle = false;

    public WiFiTile(Context context, QuickSettingsController qsc, NetworkController controller) {
        super(context, qsc, controller, R.layout.quick_settings_tile_wifi);

        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quickToggle = Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.QUICK_TOGGLE, mContext.getResources().getBoolean(R.bool.config_quickToggle));

                // Quick Toggle is Off, function normally
                if (!quickToggle) {
                    WifiManager wfm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    wfm.setWifiEnabled(!wfm.isWifiEnabled());
                } else {
                    startSettingsActivity(android.provider.Settings.ACTION_WIFI_SETTINGS);
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
                    startSettingsActivity(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    WifiManager wfm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    wfm.setWifiEnabled(!wfm.isWifiEnabled());
                }
            }
        };

    }

    @Override
    protected void updateTile() {
        if (mWifiConnected) {
            mDrawable = mWifiSignalIconId;
            mLabel = mDescription.substring(1, mDescription.length()-1);
        } else if (mWifiNotConnected) {
            mDrawable = R.drawable.ic_qs_wifi_0;
            mLabel = mContext.getString(R.string.quick_settings_wifi_label);
        } else {
            mDrawable = R.drawable.ic_qs_wifi_no_network;
            mLabel = mContext.getString(R.string.quick_settings_wifi_off_label);
        }
    }

    @Override
    public void onWifiSignalChanged(boolean enabled, int wifiSignalIconId,
            boolean activityIn, boolean activityOut,
            String wifiSignalContentDescriptionId, String description) {
        mWifiConnected = enabled && (wifiSignalIconId > 0) && (description != null);
        mWifiNotConnected = (wifiSignalIconId > 0) && (description == null);
        mWifiSignalIconId = wifiSignalIconId;
        mDescription = description;
        setActivity(activityIn, activityOut);
        updateResources();
    }

    @Override
    public void onMobileDataSignalChanged(boolean enabled, int mobileSignalIconId,
            String mobileSignalContentDescriptionId, int dataTypeIconId,
            boolean activityIn, boolean activityOut,
            String dataTypeContentDescriptionId, String description) {
    }

    @Override
    public void onAirplaneModeChanged(boolean enabled) {
    }
}
