package com.jerubrin.pomodoro.holders

import android.graphics.drawable.AnimationDrawable
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.TimerHolderBinding
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.interfaces.TimerListener
import com.jerubrin.pomodoro.data.*

class TimerHolder(
    private val binding: TimerHolderBinding,
    private val listener: TimerListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(timerData: TimerData, adapter: TimerListAdapter) {
        fillHolderViews(binding, timerData)

        initButtonsListeners(timerData, adapter)
    }

    private fun initButtonsListeners(timerData: TimerData, adapter: TimerListAdapter) {
        binding.startStopButton.setOnClickListener {
            if (timerData.isStarted) {
                stopTimer(timerData)
                adapter.notifyDataSetChanged()
            } else {
                startTimer(timerData, adapter)
            }
        }

        binding.deleteButton.setOnClickListener {
            stopTimer(timerData)
            listener.delete(timerData.id)
        }
    }

    private fun fillHolderViews(binding: TimerHolderBinding, timerData: TimerData){
        binding.apply {
            timerData.apply {
                textViewTimer.text = currentMs.displayTime()
                startStopButton.text = buttonText
                blinkingIndicator.isInvisible = invisibleAnimationDrawable
                if (invisibleAnimationDrawable){
                    (blinkingIndicator.background as? AnimationDrawable)?.stop()
                } else {
                    (blinkingIndicator.background as? AnimationDrawable)?.start()
                }
            }
        }
    }
}
