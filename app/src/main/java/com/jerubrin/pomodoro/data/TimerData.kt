package com.jerubrin.pomodoro.data

import com.jerubrin.pomodoro.timer.CountDownController

data class TimerData(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var countDownController: CountDownController
)
