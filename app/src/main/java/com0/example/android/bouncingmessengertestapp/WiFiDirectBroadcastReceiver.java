package com0.example.android.bouncingmessengertestapp;

/**
 * Created by AVI on 16-01-2018.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.widget.TextView;

import com.example.android.wifidirect.discovery.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private Channel channel;
    private TextView statusView;
    private WiFiServiceDiscoveryActivity activity;
    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WiFiServiceDiscoveryActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }
    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(WiFiServiceDiscoveryActivity.TAG, action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
                statusView = (TextView) activity.findViewById(R.id.status_text);
                activity.appendStatus("P2p Enabled");
            }
            else {
                activity.setIsWifiP2pEnabled(false);
                activity.appendStatus("P2p not Enabled");
            }
        }
        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            /*if(manager!=null){
                activity.when_disconnected();
            }*/
        }
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(WiFiServiceDiscoveryActivity.TAG,
                        "Connected to p2p network. Requesting network details");
                manager.requestGroupInfo(channel,activity);
                manager.requestConnectionInfo(channel,
                        (ConnectionInfoListener) activity);

            } else {
                    //activity.when_disconnecte();
                // It's a disconnect
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            //WiFiServiceDiscoveryActivity.username = device.deviceName;
            Log.d(WiFiServiceDiscoveryActivity.TAG, "Device status -" + device.status);
        }
    }
    public boolean checkstatus(){
        statusView = (TextView) activity.findViewById(R.id.status_text);
        String str = statusView.getText().toString();
        String str1 = "P2p Enabled";
        String str2 = "P2p not Enabled";
        Pattern p = Pattern.compile(str);
        Matcher m1 = p.matcher(str1);
        Matcher m2 = p.matcher(str2);
        if(m1.matches()){
            int a = str.indexOf(str1);
            String subString = str.substring(0,a);
            statusView.setText(subString);
            return true;
        }
        if(m2.matches()){
            int a = str.indexOf(str1);
            String subString = str.substring(0,a);
            statusView.setText(subString);
            return true;
        }
        return true;
    }

}
