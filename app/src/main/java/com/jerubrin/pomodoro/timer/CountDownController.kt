package com.jerubrin.pomodoro.timer

import android.os.CountDownTimer
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.data.*
import com.jerubrin.pomodoro.values.*

object CountDownController {
    private var timer: CountDownTimer? = null

    private var timerData: TimerData? = null
    fun getTimer() = timerData

    fun startTimer(timerData: TimerData, adapter: TimerListAdapter?) {
        timer?.cancel()
        this.timerData = timerData
        timer = getCountDownTimer(adapter)
        timer?.start()
    }

    private fun getCountDownTimer(adapter: TimerListAdapter?): CountDownTimer {
        return object : CountDownTimer(PERIOD_DAY, ONE_SECOND) {
            val interval = ONE_SECOND

            override fun onTick(millisUntilFinished: Long) {
                if (timerData?.isStarted == true) {
                    timerData?.currentMs = timerData?.currentMs?.minus(interval) ?: 0
                    if (timerData?.currentMs!! <= 0) countDownEnd(timerData)
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onFinish() {
                adapter?.notifyDataSetChanged()
            }
        }
    }
}