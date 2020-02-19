package com.suraj.vmsotp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class ConnectionCheck {
    public static boolean isConnected(ConnectivityManager connectivityManager,NetworkInfo networkInfo,Context context){
        connectivityManager=(ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        networkInfo=connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null&&networkInfo.isConnected());
    }
}
