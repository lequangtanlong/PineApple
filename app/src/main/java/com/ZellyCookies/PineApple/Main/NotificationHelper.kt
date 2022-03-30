package com.ZellyCookies.PineApple.Main

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ZellyCookies.PineApple.Matched.Matched_Activity
import com.ZellyCookies.PineApple.R

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null
    @TargetApi(Build.VERSION_CODES.O)
    fun createChannels() {
        val channel1 =
            NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_HIGH)
        channel1.enableLights(true)
        channel1.enableVibration(true)
        channel1.lightColor = Color.GREEN
        channel1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel1.vibrationPattern = longArrayOf(0, 1000, 1000, 1000)
        Log.d("notification", "we are in create channels1 \n ")
        manager!!.createNotificationChannel(channel1)
        Log.d("notification", "we are in create channels2 \n ")
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }

    fun getChannel1Notification(title: String?, message: String?): NotificationCompat.Builder {
        val intent = Intent(this, Matched_Activity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        Log.d("notification", "we are in getChaneel1Notification function \n ")
        return NotificationCompat.Builder(applicationContext, channel1ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(notificationIcon)
            .setAutoCancel(true)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setContentIntent(pi)
    }//return R.drawable.notification_app_icon;

    //compare SDK version to set the app icon as silhouette or regular one
    private val notificationIcon: Int
        private get() {
            val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useWhiteIcon) R.drawable.notification_app_icon else R.mipmap.ic_launcher_true
            //return R.drawable.notification_app_icon;
        }

    companion object {
        const val channel1ID = "channel1ID"
        const val channel1Name = "channel 1"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }
}