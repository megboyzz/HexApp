package ru.megboyzz.hexapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StatusReceiver(private val hexGenerator: HexGenerator) : BroadcastReceiver() {

    var serviceStatus = ServiceStatus.NO_STATUS

    override fun onReceive(context: Context, intent: Intent) {

        Log.i("StatusReceiver", "onReceive! action is ${intent.action}, status is $serviceStatus")
        Log.i("StatusReceiver", "${intent.getStringExtra("iam")}")

        if(intent.action == toggleReadingModeAction){
            serviceStatus = ServiceStatus.READING
            hexGenerator.startGeneratingAndSending()
        }
        else if(intent.action == leaveReadingModeAction) {
            serviceStatus = ServiceStatus.RUNNING
            hexGenerator.stopGeneratingAndSending()
        }

    }
}