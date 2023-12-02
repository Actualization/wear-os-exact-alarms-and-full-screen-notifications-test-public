package com.example.devTestAlarmScheduling

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = AlarmReceiver::class.java.simpleName
        fun scheduleAlert(context: Context) {
            Log.d(
                TAG,
                "scheduleAlert()"
            )
            try {
                val timeTillAlarm = 180000L // 3 minutes
                val triggerAtMilliseconds = System.currentTimeMillis() + timeTillAlarm
                val alarmIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
                alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                alarmIntent.putExtra("SCHEDULED_ALARM_TIME", triggerAtMilliseconds.toString())
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        Random.nextInt(100000, 999999999),
                        alarmIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                val alarmManager =
                    context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

                alarmManager!!.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMilliseconds,
                    pendingIntent
                )

                Log.d(
                    TAG,
                    "scheduleAlert() : Current time in milliseconds: " + System.currentTimeMillis()
                )

                val simpleDateFormatEastUs = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
                val triggerAtDateTime = simpleDateFormatEastUs.format(triggerAtMilliseconds)
                Log.d(
                    TAG,
                    "scheduleAlert() : AlarmScheduledForDateTime: $triggerAtDateTime - ${timeTillAlarm / 1000 / 60 } minutes and ${timeTillAlarm / 1000 % 60} seconds in the future"
                )
            } catch (e: Exception) {
                Log.d(TAG, "scheduleAlert() : Exception: " + e.message)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        var currentTime = System.currentTimeMillis()
        var scheduledAlarmTime = intent.getStringExtra("SCHEDULED_ALARM_TIME").toString().toLong()
        Log.d(
            TAG,
            "onReceive() : Difference from Scheduled time in seconds: " + (currentTime - scheduledAlarmTime) / 1000f
        )

        val channelId = context.getString(R.string.notification_channel_id)
        var extras: Bundle = Bundle()

        extras.putString("ALARM_TIME_SCHEDULED", scheduledAlarmTime.toString())
        extras.putString("ALARM_TIME_FIRED", currentTime.toString())

        val fullScreenIntent = Intent()
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        fullScreenIntent.setClass(context, AlarmFiringDisplayActivity::class.java)
        fullScreenIntent.putExtras(extras)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
//                FIXME consider making this a unique id for the notification, perhaps database tracked
            4636,
            fullScreenIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification: Notification =
            NotificationCompat.Builder(context, channelId)
                .setContentTitle(context.getString(R.string.notification_full_screen))
                .setContentText(context.getString(R.string.notification_full_screen))
                .setSmallIcon(R.drawable.cc0_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSilent(true)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build()


        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // cancelAll added due to error "Package has already posted or enqueued 25 notifications.  Not showing more."
//                FIXME disabled notification manager clearing all notifications
//                notificationManager.cancelAll()

//                FIXME try just one notification ID updated to see if it always shows
//                val uniqueID: Int = Random.nextInt(1000, 999999999)
            val uniqueID: Int = 714617
            notificationManager.notify(uniqueID, notification)

            Log.d(TAG, "onReceive() : scheduling new alarm")
            scheduleAlert(context)
        } catch (error: Exception) {
            Log.e("$TAG.onReceive.ERROR", error.toString())
        }
        Log.d(TAG, "onReceive() : end function")
    }
}