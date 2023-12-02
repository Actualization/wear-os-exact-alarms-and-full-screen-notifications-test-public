package com.example.devTestAlarmScheduling

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text

class AlarmFiringDisplayActivity : ComponentActivity() {

    companion object {
        private val TAG = AlarmFiringDisplayActivity::class.java.simpleName
    }


    //Arbitrary values that will be clearly incorrect if not updated rather than overhead of state management throughout that might impede skimability for this dev test
    private var alarmTimeScheduled: Long = 9999999
    private var alarmTimeFired: Long = 9999
    private var currentTime: Long = 99999999999999


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmFiredHelloWorld()
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true)
        }

        alarmTimeScheduled = intent.getStringExtra("ALARM_TIME_SCHEDULED").toString().toLong()
        alarmTimeFired = intent.getStringExtra("ALARM_TIME_FIRED").toString().toLong()
        currentTime = System.currentTimeMillis()

        Log.d(
            TAG,
            "onCreate() : Activity launch difference from scheduled alarm time in seconds: " +
                    "${(currentTime - alarmTimeScheduled) / 1000f}"
        )

        vibrate()

//        close Activity after timeout
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 10000) // 10 seconds
    }

    private fun vibrate() {
        try {
            // vibrate the watch
            val vibrate =
                applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(100, 1000, 10, 100, 1000, 10, 100, 1000, 10)
            if (vibrate != null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) //key
                    .build()
                vibrate.vibrate(pattern, -1, audioAttributes)
            }
        } catch (e: Exception) {
            Log.d(TAG, "onCreate() : Exception during vibration: " + e.message)
        }
    }

    @Composable
    fun AlarmFiredHelloWorld() {
        Column(
            //align content center
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "(Alarm has fired and launched this activity).",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = buildAnnotatedString {
                    append("Alarm Fire Time Diff:")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                    ) {
                        append("\n${(alarmTimeFired - alarmTimeScheduled) / 1000f} seconds")
                    }
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center

            )
            Text(
                text = buildAnnotatedString {
                    append("Alarm Scheduled to Activity Time Diff:")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                    ) {
                        append("\n${(currentTime - alarmTimeScheduled) / 1000f} seconds")
                    }
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

}

