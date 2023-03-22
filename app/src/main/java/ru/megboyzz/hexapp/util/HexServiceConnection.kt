package ru.megboyzz.hexapp.util

import android.app.Service
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class HexServiceConnection: ServiceConnection {

    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
        Log.i("HexServiceConnection", "connected!")
    }

    override fun onServiceDisconnected(componentName: ComponentName?) {
        Log.i("HexServiceConnection", "disconnected!")
    }
}