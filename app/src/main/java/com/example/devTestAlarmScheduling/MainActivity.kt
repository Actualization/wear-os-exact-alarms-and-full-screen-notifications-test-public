/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.devTestAlarmScheduling

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.devTestAlarmScheduling.presentation.theme.DevTestAlarmSchedulingTheme
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNotObtained()
        setContent {
            WearApp()
        }
    }

    companion object {
        private val TAG = "MainActivity"
        private val REQUEST_POST_NOTIFICATIONS = 1
    }

    /* handle the result of the permission request */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_POST_NOTIFICATIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "onRequestPermissionsResult() : Permission granted")
                    createFullScreenNotificationChannel()
                } else {
                    Log.d(TAG, "onRequestPermissionsResult() : Permission denied")
                }
                return
            }

            else -> {
                Log.e(TAG, "onRequestPermissionsResult() : Unexpected request code")
            }
        }
    }

    /* request the permission if it has not been obtained */
    private fun requestNotificationPermissionIfNotObtained() {
        Log.d(TAG, "requestNotificationPermissionIfNotObtained()")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "requestNotificationPermissionIfNotObtained() : Permission not yet granted")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_NOTIFICATIONS
            )
        } else {
            Log.d(TAG, "requestNotificationPermissionIfNotObtained() : Permission already granted")
            createFullScreenNotificationChannel()
        }
    }

    /* create the notification channel */
    private fun createFullScreenNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.notification_channel_name)

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the channel if it doesn't exist.
        if (notificationManager.getNotificationChannel(channelId) == null) {
            Log.d(
                TAG,
                "createFullScreenNotificationChannel() : ChannelId: $channelId, ChannelName: $channelName"
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    @Composable
    fun WearApp() {
        DevTestAlarmSchedulingTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center
            ) {
                Greeting()
                StartAlarmsButton()
            }
        }
    }

    @Composable
    fun Greeting() {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 2.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = stringResource(R.string.main_activity_description)
        )
    }

    @Composable
    fun StartAlarmsButton() {
        val context = LocalContext.current
        val buttonStartAlarmsText = getString(R.string.main_activity_start_alarms_button_text)
        val buttonAlarmsStartedText = getString(R.string.main_activity_alarms_started_button_text)
        val buttonText = remember { mutableStateOf(buttonStartAlarmsText) }
        Button(
            onClick = {
                Log.d(
                    TAG,
                    "StartAlarmsButton() : Current time in milliseconds: " + System.currentTimeMillis()
                )
                AlarmReceiver.scheduleAlert(context)
                buttonText.value = buttonAlarmsStartedText
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = buttonText.value)
        }
    }

    @Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        WearApp()
    }
}