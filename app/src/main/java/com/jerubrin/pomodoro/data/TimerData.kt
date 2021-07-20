package com.jerubrin.pomodoro.data

data class TimerData(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var allMs: Long,
    var buttonText: String = "Start",
    var isFinished: Boolean = false,
    var allCurrentMs: Long = -1L
)
