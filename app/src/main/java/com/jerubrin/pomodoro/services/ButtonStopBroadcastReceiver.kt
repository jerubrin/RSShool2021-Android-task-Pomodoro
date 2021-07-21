package com.jerubrin.pomodoro.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jerubrin.pomodoro.timer.CountDownController

class ButtonStopBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        CountDownController.stopTimerNow()
    }
}