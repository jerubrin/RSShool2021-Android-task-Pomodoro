package com.jerubrin.pomodoro.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.jerubrin.pomodoro.MainActivity
import com.jerubrin.pomodoro.R
import com.jerubrin.pomodoro.extentions.displayTime
import com.jerubrin.pomodoro.timer.CountDownController
import com.jerubrin.pomodoro.values.*
import kotlinx.coroutines.*

@SuppressLint("RemoteViewLayout")
class ForegroundService : Service() {

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null
    private var job: Job? = null

    var remoteViews: RemoteViews? = null
    var remoteSmallViews: RemoteViews? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                commandStart()
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart() {
        if (isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer()
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer() {
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                val timerMs = CountDownController.getTimeMs() + 999L
                val allMs = CountDownController.getAllMs()
                val progressNumber = ( (timerMs * 10_000) / allMs ).toInt()

                when {
                    CountDownController.isWorking -> {
                        notifyRemoteViews(timerMs.displayTime(), progressNumber, true)
                    }
                    CountDownController.isFinished -> {
                        notifyRemoteViews("FINISHED!", 0, false)
                        delay(WAIT_AFTER_FINISH)
                        commandStop()
                    }
                    else -> {
                        notifyRemoteViews("STOPPED!", progressNumber, false)
                        delay(WAIT_AFTER_STOP)
                        commandStop()
                    }
                }
                MainActivity.timersDataList.forEachIndexed { index, timerData ->
                    if(timerData.id == CountDownController.getId()) {
                        MainActivity.timersDataList[index] =
                            CountDownController.getTimerData() ?: MainActivity.timersDataList[index]
                    }
                }
                delay(INTERVAL)
            }
        }
    }

    private fun notifyRemoteViews(displayTime: String, progressNumber: Int, buttonVisible: Boolean) {
        remoteViews = RemoteViews(packageName, R.layout.timer_notify)
        remoteViews?.setTextViewText(R.id.timer_text_notify, displayTime)
        remoteViews?.setProgressBar(R.id.progressbar_notify, MAX_PROGRESS, MAX_PROGRESS-progressNumber, false)

        remoteSmallViews = RemoteViews(packageName, R.layout.timer_small_notify)
        remoteSmallViews?.setTextViewText(R.id.timer_text_notify_small, displayTime)
        remoteSmallViews?.setProgressBar(R.id.progressbar_notify_small, 10_000, 10_000-progressNumber, false)

        if(buttonVisible) {
            remoteViews?.setOnClickPendingIntent(R.id.button_stop_notify, getButtonPendingIntent())
        } else {
            remoteViews?.setViewVisibility(R.id.button_stop_notify, View.INVISIBLE)
            remoteViews?.setOnClickPendingIntent(R.id.button_stop_notify, getPendingIntent())
        }

        notificationManager?.notify(
            NOTIFICATION_ID,
            builder
                .setCustomContentView(remoteSmallViews)
                .setCustomBigContentView(remoteViews)
                .build()
        )
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStop()")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        startForeground(NOTIFICATION_ID, builder.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun getButtonPendingIntent(): PendingIntent? {
        val actionIntent = Intent(this, ButtonStopBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(this, 1, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}