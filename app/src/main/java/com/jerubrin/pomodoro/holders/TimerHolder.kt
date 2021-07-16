package com.jerubrin.pomodoro.holders

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.jerubrin.pomodoro.data.*

class TimerHolder(
    private val binding: TimerHolderBinding,
    private val listener: TimerListener,
    private val activity: MainActivity
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun bind(timerData: TimerData, adapter: TimerListAdapter) {
        initButtonsListeners(timerData, adapter)
        fillHolderViews(binding, timerData)
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

    @RequiresApi(Build.VERSION_CODES.M)
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
                progressCustom.setCurrent(currentMs)
                @RequiresApi(Build.VERSION_CODES.M)
                if(isFinished){
                    holderConstraint.background = activity.resources.getColor(R.color.blue_light, activity.theme).toDrawable();
                } else {
                    holderConstraint.background = activity.resources.getColor(R.color.material_on_background_disabled, activity.theme).toDrawable();
                }
                binding.progressCustom.setPeriod(timerData.allMs)
            }
        }
    }
}
