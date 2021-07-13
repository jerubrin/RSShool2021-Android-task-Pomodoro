package com.jerubrin.pomodoro.holders

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.os.persistableBundleOf
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.TimerHolderBinding
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.interfaces.TimerListener
import com.jerubrin.pomodoro.interfaces.TimerViewChanger
import com.jerubrin.pomodoro.timer.CountDownController
import com.jerubrin.pomodoro.values.*

class TimerHolder(
    private val binding: TimerHolderBinding,
    private val listener: TimerListener,
) : RecyclerView.ViewHolder(binding.root), TimerViewChanger {

    fun bind(timerData: TimerData) {
        timerData?.countDownController = CountDownController(this)
        binding.textViewTimer.text = timerData.currentMs.displayTime()
        binding.titleTemp.text = position.toString()
        if (timerData.isStarted) {
            timerData.countDownController.startTimer(timerData)
            changeViewsToStart()
        } else {
            timerData.countDownController.stopTimer(timerData)
            changeViewsToStop()
        }
        initButtonsListeners(timerData)
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

    private fun changeViewsToStart(){
        binding.startStopButton.text = "Stop"
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun changeViewsToStop(){
        binding.startStopButton.text = "Start"
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    override fun changeTimerView(timerText: String) {
        //if(position == timerData.id) {
            binding.textViewTimer.text = timerText
        //}
    }
}
