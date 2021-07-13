package com.jerubrin.pomodoro.timer

import android.os.CountDownTimer
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.interfaces.TimerViewChanger
import com.jerubrin.pomodoro.values.*

class CountDownController(
    private val holder: TimerViewChanger?
) {
    private var timer: CountDownTimer? = null

    fun startTimer(timerData: TimerData) {
        timer?.cancel()
        timer = getCountDownTimer(timerData)
        timer?.start()
    }

    fun stopTimer(timerData: TimerData) {
        timer?.cancel()
    }

    private fun getCountDownTimer(timerData: TimerData): CountDownTimer {
        return object : CountDownTimer(PERIOD_DAY, ONE_SECOND) {
            val interval = ONE_SECOND

            override fun onTick(millisUntilFinished: Long) {
                timerData.currentMs -= interval
                holder?.changeTimerView(timerData.currentMs.displayTime())
            }

            override fun onFinish() {
                //binding?.textViewTimer?.text = timerData.currentMs.displayTime()
            }
        }
    }
}