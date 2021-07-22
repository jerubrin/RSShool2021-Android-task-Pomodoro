package com.jerubrin.pomodoro.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jerubrin.pomodoro.MainActivity
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.TimerHolderBinding
import com.jerubrin.pomodoro.holders.TimerHolder
import com.jerubrin.pomodoro.interfaces.TimerListener

class TimerListAdapter(
    private val listener: TimerListener,
    val activity: MainActivity
): ListAdapter<TimerData, TimerHolder>(itemComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerHolderBinding.inflate(layoutInflater, parent, false)
        return TimerHolder(binding, listener, activity)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TimerHolder, position: Int) {
        holder.bind(getItem(position), this, position)
    }

    companion object {

        val itemComparator = object : DiffUtil.ItemCallback<TimerData>() {

            override fun areItemsTheSame(oldItem: TimerData, newItem: TimerData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TimerData, newItem: TimerData): Boolean {
                return  oldItem.currentMs  == newItem.currentMs &&
                        oldItem.isStarted  == newItem.isStarted &&
                        oldItem.isFinished == newItem.isFinished
            }

            override fun getChangePayload(oldItem: TimerData, newItem: TimerData) = Any()
        }
    }
}