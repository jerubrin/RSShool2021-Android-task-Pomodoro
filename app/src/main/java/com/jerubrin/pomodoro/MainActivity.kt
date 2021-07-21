package com.jerubrin.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
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

        if (binding.recyclerView.adapter == null){
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = timerListAdapter
            }
        }

        if (timersDataList.isNotEmpty()){
            (binding.recyclerView.adapter as TimerListAdapter).submitList(timersDataList)
            val runningTimer = timerListAdapter.currentList.find { it.isStarted }
            if (runningTimer != null){
                runningTimer.allCurrentMs = runningTimer.currentMs
                CountDownController.startTimer(runningTimer.id, timerListAdapter)
            }
        }

        binding.addButton.setOnClickListener {
            val currentMin = binding.textInputMinutes.text.toString().toLongOrNull() ?: 0L
            if (currentMin != 0L) {
                timersDataList = addToList(currentMin, nextId++, timerListAdapter)
                timerListAdapter.submitList(timersDataList)
            } else {
                Toast.makeText(this, "Неверное значение!", Toast.LENGTH_SHORT).show()
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    /* interface TimerListener */

    override fun delete(id: Int) {
        timersDataList = mutableListOf()
        timerListAdapter.currentList.forEach { if(it.id != id) timersDataList.add(it) }
        timerListAdapter.submitList(timersDataList)
    }

    override fun addToList(currentMin: Long, id: Int, adapter: TimerListAdapter): MutableList<TimerData> {
        val currentMs = currentMin * 60L * 1000L
        val timersDataList = mutableListOf<TimerData>()
        adapter.currentList.forEach { timersDataList.add(it) }
        timersDataList.add(TimerData(id, currentMs, false, currentMs))
        return timersDataList
    }

    companion object {
        private var timersDataList = mutableListOf<TimerData>()
        private var nextId = 0
    }
}