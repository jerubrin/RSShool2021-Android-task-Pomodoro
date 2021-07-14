package com.jerubrin.pomodoro.data

import android.content.IntentSender
import android.graphics.drawable.AnimationDrawable
import com.jerubrin.pomodoro.timer.CountDownController

data class TimerData(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var allMs: Long,
    var isFinished: Boolean = false,
    var buttonText: String = "Start",
    var invisibleAnimationDrawable: Boolean = true
)
