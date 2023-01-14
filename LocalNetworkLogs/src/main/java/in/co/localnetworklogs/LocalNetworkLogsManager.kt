package `in`.co.localnetworklogs

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocalNetworkLogsManager {

    private lateinit var preferences: SharedPreferences
    private lateinit var application: Application

    fun initiate(application: Application) {
        preferences = application.getSharedPreferences(
            PREFERENCES_FILE_NAME, Context.MODE_PRIVATE
        )

        this.application = application
    }

    private val PREFERENCES_FILE_NAME = "LocalNetworkLogsSharedPreferences"

    companion object : SingletonHolder<LocalNetworkLogsManager>(::LocalNetworkLogsManager)

    private fun clearPreferences() {
        preferences.edit().clear().commit()
    }

    fun clearLogs() {
        setString("logs", "")
    }

    private fun setString(key: String?, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return preferences.getString(key, "") ?: ""
    }

    fun getHttpLoggingInterceptor(isLogsActive: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message ->

            if (isLogsActive) {
                if (isJSONValid(message)) {
                    Log.e("LocalNetworkLogsManager", formatString(message))
                } else {
                    Log.e("LocalNetworkLogsManager", message)
                }

                if (message.contains("-->") && message.contains("http")) {
                    LocalNetworkLogsManager.getInstance().createNotification(message)
                }

                val log = getString("logs")
                if (log === "") {
                    setString("logs", message + "\n")
                } else {
                    setString("logs", message + "\n" + log)
                }
            }
        }
        return logging
    }

    private fun isJSONValid(test: String): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }

        }
        return true
    }

    private fun formatString(text: String): String {

        val json = StringBuilder()
        var indentString = ""

        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append("\n" + indentString + letter + "\n")
                    indentString += "\t"
                    json.append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append("\n" + indentString + letter)
                }
                ',' -> json.append(letter + "\n" + indentString)

                else -> json.append(letter)
            }
        }

        return json.toString()
    }

    fun createNotification(text: String) {

        val intent =
            Intent(application.applicationContext, LocalNetworkLogsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        var builder = NotificationCompat.Builder(application.applicationContext, "2001")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_networklogs_bug)
            .setContentTitle("Last request")
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        createNotificationChannel()

        val notificationManager: NotificationManager =
            application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2001, builder.build())

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Network Logs Notification"
            val descriptionText = "This will show local network logs in the notification"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("2001", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}