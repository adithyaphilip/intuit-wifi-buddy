package com.aaphilip.tools.autowifilogin.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.aaphilip.tools.autowifilogin.network.Requester;
import com.aaphilip.tools.autowifilogin.util.Constants;
import com.aaphilip.tools.autowifilogin.util.Logger;
import com.aaphilip.tools.autowifilogin.util.NotificationHelper;
import com.aaphilip.tools.autowifilogin.util.Preferences;

import java.io.IOException;
import java.util.Date;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private Context mContext;
    private Logger mLogger;

    @SuppressLint("HandlerLeak")
    private Handler mStartRequestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            makeAsyncRequest(Constants.INTUIT_USERNAME, Constants.INTUIT_PASSWORD);
        }
    };

    private PendingResult mPendingResult;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mLogger = new Logger();

        if (!Preferences.getPreferences(context).isLocallyAuthenticated()) {
            return;
        }

        Log.d("RECEIVER", "Received! " + intent.getAction());
        mLogger.addToLog(new Date(System.currentTimeMillis()), "Received:" + intent.getAction());
        String action = intent.getAction();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            Log.d("NetworkInfo", "" + info);
            Log.d("NetworkInfo", "" + (info != null ? info.isConnected() : ":("));
            mLogger.addToLog(new Date(System.currentTimeMillis()),
                    "NW_info:" + (info != null ? info.getState() : "null"));

            if(info != null && info.isConnected()) {

                boolean connected = isConnectedToDesiredWifi(context, Constants.WIFI_NAME);

                if (connected) {
                    bindProcessToWifi();
                    mPendingResult = goAsync();
//                    mStartRequestHandler.sendEmptyMessage(0);
                    msgOnWifiAvailable(mStartRequestHandler);
                }
            }
        }
    }

    public void msgOnWifiAvailable(final Handler handler) {
        Log.d("WifiAvailable", "Checking if Wi-Fi available");
        // ideally, we should be able to tell when the network starts allowing data transfer
        handler.sendEmptyMessageDelayed(0, Constants.CONNECTIVITY_WAIT_MS);
    }

    /**
     * forces the device to Wi-Fi to make the request even if network data is available.
     * Requires different versions for different Android versions as methods have had their
     * functionality removed/renamed over time
     */
    public void bindProcessToWifi() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            connectivityManager.setNetworkPreference(ConnectivityManager.TYPE_WIFI);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            for(Network network: connectivityManager.getAllNetworks()) {
                NetworkInfo info = connectivityManager.getNetworkInfo(network);
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    //noinspection deprecation
                    ConnectivityManager.setProcessDefaultNetwork(network);
                    break;
                }
            }
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(Network network: connectivityManager.getAllNetworks()) {
                NetworkInfo info = connectivityManager.getNetworkInfo(network);
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    connectivityManager.bindProcessToNetwork(network);
                    break;
                }
            }
            return;
        }
    }

    public void makeAsyncRequest(String username, String password) {

        @SuppressLint("HandlerLeak")
        Requester.ResultHandler resultHandler = new Requester.ResultHandler() {
            @Override
            public void onError(IOException e) {
                Toast.makeText(mContext, "Exception while logging in!", Toast.LENGTH_SHORT).show();
                mLogger.addToLog(new Date(System.currentTimeMillis()), "Result:Exception");
                finish();
            }

            @Override
            public void onSuccess(String response) {
                NotificationHelper.showSuccessfulLoginNotification(mContext);
                mLogger.addToLog(new Date(System.currentTimeMillis()), "Result:success");
                finish();
            }

            @Override
            public void onFailure(String response) {
                NotificationHelper.showAlreadyLoggedInNotification(mContext);
                mLogger.addToLog(new Date(System.currentTimeMillis()), "Result:already_in");
                finish();
            }

            private void finish() {
                if (mPendingResult != null) {
                    mPendingResult.finish();
                }
            }
        };

        new Requester(resultHandler).makeAsyncRequest(username, password);
    }

    /**
     * Detect you are connected to a specific network.
     */
    private boolean isConnectedToDesiredWifi(Context context, String desiredSsid) {

        WifiManager wifiManager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifi = wifiManager.getConnectionInfo();

        if (wifi == null) {
            return false;
        }

        String currentSsid = wifi.getSSID();
        if (currentSsid.startsWith("\"") && currentSsid.endsWith("\"")) {
            currentSsid = currentSsid.substring(1, currentSsid.length() - 1);
        }

        return currentSsid.equals(desiredSsid);
    }
}