package com.jerubrin.pomodoro.holders

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.jerubrin.pomodoro.MainActivity
import com.jerubrin.pomodoro.R
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.TimerHolderBinding
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.interfaces.TimerListener
import com.jerubrin.pomodoro.timer.CountDownController

class TimerHolder(
    private val binding: TimerHolderBinding,
    private val listener: TimerListener,
    private val activity: MainActivity
) : RecyclerView.ViewHolder(binding.root) {
    private var id = -1

    fun bind(timerData: TimerData, adapter: TimerListAdapter, position: Int) {
        id = timerData.id
        initButtonsListeners(id, adapter)
        fillHolderViews(id, binding, adapter)
    }

    private fun initButtonsListeners(id: Int, adapter: TimerListAdapter) {
        binding.startStopButton.setOnClickListener {
            val timerItem = adapter.currentList.find { it.id == id }
            if (timerItem?.isStarted == true) {
                CountDownController.stopTimer(id, adapter)
            } else {
                CountDownController.startTimer(id, adapter)
            }
        }
        binding.resetButton.setOnClickListener {
            CountDownController.stopTimer(id, adapter)
            listener.reset(id)
        }
        binding.deleteButton.setOnClickListener {
            CountDownController.stopTimer(id, adapter)
            listener.delete(id)
        }
    }

    private fun fillHolderViews(id: Int, binding: TimerHolderBinding, adapter: TimerListAdapter){
        binding.apply {
            val timerData = adapter.currentList.find { it.id == id }
            textViewTimer.text = if (timerData != null) getTimerString(timerData) else 0L.displayTime()
            startStopButton.text = timerData?.buttonText
            blinkingIndicator.isInvisible = timerData?.isStarted == false
            if (blinkingIndicator.isInvisible){
                (blinkingIndicator.background as? AnimationDrawable)?.stop()
            } else {
                (blinkingIndicator.background as? AnimationDrawable)?.start()
            }
            progressCustom.setCurrent(timerData?.currentMs ?: 0)
            holderConstraint.background = setHolderBackgroundColor(timerData, activity)
            binding.progressCustom.setPeriod(timerData?.allMs)
        }
    }

    private fun setHolderBackgroundColor(timerData: TimerData?, activity: MainActivity): Drawable? {
        return if(timerData?.isFinished == true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.resources.getColor(R.color.red_dark, activity.theme).toDrawable()
            } else {
                activity.resources.getColor(R.color.red_dark).toDrawable()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.resources.getColor(R.color.design_default_color_on_primary, activity.theme).toDrawable()
            } else {
                activity.resources.getColor(R.color.design_default_color_on_primary).toDrawable()
            }
        }
    }

    private fun getTimerString(timerData: TimerData): String =
            if ((timerData.currentMs + 899L) <= timerData.allMs)
                (timerData.currentMs + 899L).displayTime()
            else
                timerData.allMs.displayTime()
}
