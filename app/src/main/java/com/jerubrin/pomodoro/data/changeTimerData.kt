package com.jerubrin.pomodoro.data

import com.jerubrin.pomodoro.adapters.TimerListAdapter

fun MutableList<TimerData>.changeTimerData(
    id: Int,
    currentMs: Long?,
    isStarted: Boolean,
    timerListAdapter: TimerListAdapter,
) {
    val newTimers = mutableListOf<TimerData>()
    this.forEach {
        if (it.id == id) {
            newTimers.add(TimerData(it.id, currentMs ?: it.currentMs, isStarted))
        } else {
            newTimers.add(it)
        }
    }
    timerListAdapter.submitList(newTimers)
    this.clear()
    this.addAll(newTimers)
}