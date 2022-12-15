package com.example.favouritedishes.models.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.favouritedishes.R
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.views.activities.MainActivity

class NotificationWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(){
        val notificationId = 0
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title = applicationContext.getString(R.string.notification_title)
        val subtitle = applicationContext.getString(R.string.notification_subtitle)
        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_logo)
        val icon = NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)
                                             .setContentTitle(title)
                                             .setContentText(subtitle)
                                             .setSmallIcon(R.drawable.ic_stat_notification)
                                             .setLargeIcon(bitmap)
                                             .setDefaults(NotificationCompat.DEFAULT_ALL)
                                             .setContentIntent(pendingIntent)
                                             .setStyle(icon)
                                             .setAutoCancel(true)
        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constants.NOTIFICATION_CHANNEL)
            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audio = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                                           .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                           .build()

            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL, Constants.NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtone, audio)
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, notification.build())
    }

    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}