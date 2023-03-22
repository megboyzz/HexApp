package ru.megboyzz.hexapp.service

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*

class HexGenerator(private val context: Context) {


    private fun getRandomString() : String {
        val allowedChars = ('A'..'F') + ('0'..'9')
        return (1..24)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @OptIn(DelicateCoroutinesApi::class)
    val job = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        val intent = Intent()
        intent.action = newHexAction
        while (true) {
            val hex = getRandomString()
            Log.i("HexGenerator", hex)
            intent.putExtra(hexIntentParam, hex)
            context.sendBroadcast(intent)
            delay(1000)
        }
    }

    fun startGeneratingAndSending(){
        job.start()
    }

    fun stopGeneratingAndSending(){
        job.cancel()
    }

}