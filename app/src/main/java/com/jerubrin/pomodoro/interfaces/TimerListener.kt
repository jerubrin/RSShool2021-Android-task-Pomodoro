package com.jerubrin.pomodoro.interfaces

import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData

interface TimerListener {
    fun delete(id: Int)
    fun reset(id: Int)
    fun addToList(currentMin: Long, id: Int, adapter: TimerListAdapter): MutableList<TimerData>
}