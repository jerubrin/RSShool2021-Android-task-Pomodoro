package com.jerubrin.pomodoro.extentions

import com.jerubrin.pomodoro.values.*

fun Long.displayTime(): String {
    if (this <= STOP_TIME) {
        return STOP_TIME_TEXT
    }
    val hours = this.getHours()
    val min = this.getMinutes()
    val sec = this.getSeconds()

    return "${hours.displaySlot()}:${min.displaySlot()}:${sec.displaySlot()}"
}

fun Long.displaySlot(): String {
    return if (this / 10L > 0) {
        "$this"
    } else {
        "0$this"
    }
}

private fun Long.getHours(): Long =
    this / 1000 / 3600

private fun Long.getMinutes(): Long =
    this / 1000 % 3600 / 60

private fun Long.getSeconds(): Long =
    this / 1000 % 60