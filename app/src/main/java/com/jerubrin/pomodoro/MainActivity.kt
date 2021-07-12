package com.jerubrin.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerubrin.pomodoro.adapters.TimerListAdapter
import com.jerubrin.pomodoro.data.TimerData
import com.jerubrin.pomodoro.data.changeTimerData
import com.jerubrin.pomodoro.databinding.ActivityMainBinding
import com.jerubrin.pomodoro.interfaces.TimerListener


class MainActivity : AppCompatActivity(), TimerListener {
    private lateinit var binding: ActivityMainBinding

    private val timerListAdapter = TimerListAdapter(this)
    private val timersDataList = mutableListOf<TimerData>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerListAdapter
        }

        binding.addButton.setOnClickListener {
            val currentMin = binding.textInputMinutes.text.toString().toLongOrNull() ?: 0L
            if (currentMin != 0L) {
                val currentMs = currentMin * 60L * 1000L
                timersDataList.add(TimerData(nextId++, currentMs, false))
                timerListAdapter.submitList(timersDataList.toList())
            } else {
                Toast.makeText(this, "Неверное значение!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* interface TimerListener */

    override fun start(id: Int) {
        timersDataList.changeTimerData(id, null, true, timerListAdapter)
    }

    override fun stop(id: Int, currentMs: Long) {
        timersDataList.changeTimerData(id, currentMs, false, timerListAdapter)
    }

    override fun reset(id: Int) {
        timersDataList.changeTimerData(id, 0L, false, timerListAdapter)
    }

    override fun delete(id: Int) {
        timersDataList.remove(timersDataList.find { it.id == id })
        timerListAdapter.submitList(timersDataList.toList())
    }
}