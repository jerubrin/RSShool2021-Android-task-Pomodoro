package com.jerubrin.pomodoro.data

import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.timer.CountDownController

fun stopTimer(timerData: TimerData?){
    timerData?.apply {
        isStarted = false
        invisibleAnimationDrawable = true
        buttonText = "Start"
    }
}

fun startTimer(timerData: TimerData, adapter: TimerListAdapter){
    stopTimer(CountDownController.getTimer())
    timerData.apply {
        isStarted = true
        invisibleAnimationDrawable = false
        buttonText = "Stop"
    }
    CountDownController.startTimer(timerData, adapter)
}

fun countDownEnd(timerData: TimerData?) {
    stopTimer(timerData);
    timerData?.currentMs = timerData?.allMs ?: 0
    timerData?.isFinished = true
}