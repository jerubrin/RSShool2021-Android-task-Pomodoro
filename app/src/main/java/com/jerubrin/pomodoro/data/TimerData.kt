package com.jerubrin.pomodoro.data

data class TimerData(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean
)
