package ru.megboyzz.hexapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.megboyzz.hexapp.util.*
import ru.megboyzz.hexapp.viewmodel.ServiceStatus

class HexServiceReceiver : BroadcastReceiver() {

    var onNewHexAction: (String) -> Unit = {}
    var onStatusResult: (ServiceStatus) -> Unit = {}

    fun setHandlers(
        onNewHexAction: (String) -> Unit,
        onStatusResult: (ServiceStatus) -> Unit
    ){
        this.onNewHexAction = onNewHexAction
        this.onStatusResult = onStatusResult
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("HexServiceReceiver", "onReceive! action is ${intent.action}")

        if(intent.action == newHexAction){
            val stringExtra = intent.getStringExtra(hexIntentParam)
            onNewHexAction(stringExtra ?: "")
        }

    }
}