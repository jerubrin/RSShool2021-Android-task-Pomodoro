package com.jerubrin.pomodoro.timer

import android.os.CountDownTimer
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.extentions.changeTimerData
import com.jerubrin.pomodoro.values.*

object CountDownController {
    private var timer: CountDownTimer? = null

    private var currentTimerData: TimerData? = null
    private var currentAdapter: TimerListAdapter? = null

    private var currentTime = 0L

    var isWorking = false
    var isFinished = false
    fun getTimeMs() = currentTimerData?.currentMs ?: -1999L
    fun getAllMs() = currentTimerData?.allMs ?: 0

    fun stopTimerNow() {
        if (currentTimerData != null && currentAdapter != null) {
            val nonNullCurrentTimerData = currentTimerData !!
            val nonNullCurrentAdapter = currentAdapter !!
            stopTimer(nonNullCurrentTimerData.id, nonNullCurrentAdapter)
        }
    }

    fun startTimer(id: Int, adapter: TimerListAdapter?) {
        timer?.cancel()
        currentAdapter = adapter
        currentTimerData = adapter?.currentList?.find { it.id == id }
        adapter?.currentList?.changeTimerData(
            id,
            currentTimerData?.currentMs ?: 0,
            true,
            currentTimerData?.allMs ?: 0,
            adapter,
            false,
            currentTimerData?.allCurrentMs ?: -1
        )
        isWorking = true
        timer = getCountDownTimer(adapter)
        currentTime = System.currentTimeMillis()
        timer?.start()
    }

    fun stopTimer(id: Int, adapter: TimerListAdapter?) {
        timer?.cancel()
        currentTimerData = adapter?.currentList?.find { it.id == id }
        if (currentTimerData != null) {
            currentTimerData?.apply {
                adapter?.currentList?.changeTimerData(id, currentMs, false, allMs, adapter, allCurrentMs = currentMs)
            }
        }
        isWorking = false
    }

    private fun getCountDownTimer(adapter: TimerListAdapter?): CountDownTimer {
        return object : CountDownTimer(PERIOD_DAY, COUNT_DOWN_MILLISECONDS) {


            override fun onTick(millisUntilFinished: Long) {
                currentTimerData = adapter?.currentList?.find { it.id == currentTimerData?.id }?.copy()
                if (currentTimerData?.isStarted == true) {
                    currentTimerData?.currentMs =
                        currentTimerData?.allCurrentMs?.minus(System.currentTimeMillis() - currentTime) ?: 0
                    if (currentTimerData != null)
                        countDown(currentTimerData!!, adapter)
                }
            }

            override fun onFinish() {
                if (currentTimerData != null) {
                    countDown(currentTimerData!!, adapter)
                }
            }
        }
    }

    private fun countDown(currentTimerData: TimerData, adapter: TimerListAdapter?){
        isFinished = currentTimerData.currentMs <= 0
        if (!isFinished) {
            adapter?.currentList?.changeTimerData(
                currentTimerData.id,
                currentTimerData.currentMs,
                currentTimerData.isStarted,
                currentTimerData.allMs,
                adapter,
                false,
                currentTimerData.allCurrentMs
            )
            isWorking = true
        } else {
            adapter?.currentList?.changeTimerData(
                currentTimerData.id,
                currentTimerData.allMs,
                false,
                currentTimerData.allMs,
                adapter,
                true,
                -1L
            )
            isWorking = false
        }
    }
}