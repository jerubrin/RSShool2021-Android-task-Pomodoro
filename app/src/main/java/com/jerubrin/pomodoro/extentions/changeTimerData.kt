package com.jerubrin.pomodoro.extentions

import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.timer.CountDownController

fun MutableList<TimerData>.changeTimerData(
    id: Int,
    currentMs: Long,
    isStarted: Boolean,
    allMs: Long,
    adapter: TimerListAdapter,
    isFinished: Boolean? = null,
    allCurrentMs: Long = -1L
) {
    val newTimers = mutableListOf<TimerData>()
    this.forEach {
        if (it.id == id) {
            newTimers.add(
                TimerData(
                    id,
                    currentMs,
                    isStarted && isFinished == false,
                    allMs,
                    if (isStarted) "Stop" else "Start",
                    isFinished ?: it.isFinished,
                    if (allCurrentMs == -1L) allMs else allCurrentMs
                )
            )
        } else {
            val itCopy = it.copy()
            itCopy.isStarted = false
            itCopy.buttonText = "Start"
            itCopy.allCurrentMs = itCopy.currentMs
            newTimers.add(itCopy)
        }
    }
    adapter.submitList(newTimers)
}