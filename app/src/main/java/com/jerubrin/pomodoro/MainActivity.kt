package com.jerubrin.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.databinding.ActivityMainBinding
import com.jerubrin.pomodoro.interfaces.TimerListener
import com.jerubrin.pomodoro.services.ForegroundService
import com.jerubrin.pomodoro.timer.CountDownController
import com.jerubrin.pomodoro.values.*


class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding

    private val timerListAdapter = TimerListAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTimePickers(0..23, 0..59, 0..59)
        startRecycleView(binding.recyclerView, timerListAdapter)

        restoreData(timersDataList, binding.recyclerView)

        setButtonAddListener(binding)
    }

    private fun setTimePickers(hours: IntRange = 0..23, min: IntRange = 0..59, sec: IntRange = 0..59){
        binding.pickerInputHours.apply {
            minValue = hours.first
            maxValue = hours.last
        }
        binding.pickerInputMinutes.apply {
            minValue = min.first
            maxValue = min.last
        }
        binding.pickerInputSeconds.apply {
            minValue = sec.first
            maxValue = sec.last
        }
    }

    private fun setButtonAddListener(binding: ActivityMainBinding) {
        binding.addButton.setOnClickListener {
            val currentSec =
                ( binding.pickerInputHours.value.toString().toLongOrNull() ?: 0L  ) * 60 * 60 +
                ( binding.pickerInputMinutes.value.toString().toLongOrNull() ?: 0L ) * 60 +
                ( binding.pickerInputSeconds.value.toString().toLongOrNull() ?: 0L )
            if (currentSec != 0L) {
                if (timerListAdapter.currentList.size <= 100) {
                    timersDataList = addToList(currentSec, nextId++, timerListAdapter)
                    timerListAdapter.submitList(timersDataList)
                } else {
                    Toast.makeText(this, "Слишком много таймеров!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Неверное значение!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecycleView(recyclerView :RecyclerView, timerListAdapter: TimerListAdapter) {
        if (recyclerView.adapter == null){
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = timerListAdapter
            }
        }
    }

    private fun restoreData(timersDataList: MutableList<TimerData>, recyclerView: RecyclerView) {
        if (timersDataList.isNotEmpty()){
            val timerListAdapter = (recyclerView.adapter as TimerListAdapter)
            timerListAdapter.submitList(timersDataList)
            val runningTimer = timerListAdapter.currentList.find { it.isStarted }
            if (runningTimer != null){
                runningTimer.allCurrentMs = runningTimer.currentMs
                CountDownController.startTimer(runningTimer.id, timerListAdapter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timersDataList = timerListAdapter.currentList.toMutableList()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (CountDownController.isWorking) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startService(startIntent)
        }
    }

    private lateinit var stopIntent: Intent
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    /* interface TimerListener */

    override fun delete(id: Int) {
        timersDataList = mutableListOf()
        timerListAdapter.currentList.forEach { if(it.id != id) timersDataList.add(it) }
        timerListAdapter.submitList(timersDataList)
    }

    override fun reset(id: Int) {
        val timersDataList = mutableListOf<TimerData>()
        timerListAdapter.currentList.forEach {
            if(it.id != id) {
                timersDataList.add(it)
            } else {
                val itCopy = it.copy()
                itCopy.apply {
                    isStarted = false
                    allCurrentMs = -1L
                    currentMs = allMs
                    buttonText = "Start"
                    isFinished = false
                }
                timersDataList.add(itCopy)
            }
        }
        timerListAdapter.submitList(timersDataList)
    }

    override fun addToList(currentSec: Long, id: Int, adapter: TimerListAdapter): MutableList<TimerData> {
        val currentMs = currentSec * 1000L
        val timersDataList = mutableListOf<TimerData>()
        adapter.currentList.forEach { timersDataList.add(it) }
        timersDataList.add(TimerData(id, currentMs, false, currentMs))
        return timersDataList
    }

    companion object {
        var timersDataList = mutableListOf<TimerData>()
        private var nextId = 0
    }
}