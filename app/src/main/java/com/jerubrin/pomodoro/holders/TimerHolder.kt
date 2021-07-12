package com.jerubrin.pomodoro.holders

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.TimerHolderBinding
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.interfaces.TimerListener
import com.jerubrin.pomodoro.values.*

class TimerHolder(
    private val binding: TimerHolderBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(timerData: TimerData) {
        binding.textViewTimer.text = timerData.currentMs.displayTime()
        if (timerData.isStarted) {
            startTimer(timerData)
        } else {
            stopTimer(timerData)
        }
        initButtonsListeners(timerData)
    }

    private fun startTimer(timerData: TimerData) {
        binding.startStopButton.text = "Stop"

        timer?.cancel()
        timer = getCountDownTimer(timerData)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(timerData: TimerData) {
        binding.startStopButton.text = "Start"

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timerData: TimerData): CountDownTimer {
        return object : CountDownTimer(PERIOD_DAY, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                timerData.currentMs -= interval
                binding.textViewTimer.text = timerData.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.textViewTimer.text = timerData.currentMs.displayTime()
            }
        }
    }

    private fun initButtonsListeners(timerData: TimerData) {
        binding.startStopButton.setOnClickListener {
            if (timerData.isStarted) {
                listener.stop(timerData.id, timerData.currentMs)
            } else {
                listener.start(timerData.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(timerData.id) }
    }
}
