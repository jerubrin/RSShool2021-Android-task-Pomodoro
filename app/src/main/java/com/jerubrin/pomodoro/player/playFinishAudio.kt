package com.jerubrin.pomodoro.player

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.jerubrin.pomodoro.R

fun playFinishAudio(context: Context) {
    MediaPlayer.create(context, R.raw.finish).start()
    Toast.makeText(context, "Time is over!", Toast.LENGTH_SHORT).show()
}