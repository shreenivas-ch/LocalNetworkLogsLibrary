package `in`.co.localnetworklogs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocalNetworkLogsManager private constructor() {

    private lateinit var preferences: SharedPreferences

    fun initiate(application: Application)
    {
        application.getSharedPreferences(
            PREFERENCES_FILE_NAME, Context.MODE_PRIVATE
        )
    }

    private val PREFERENCES_FILE_NAME = "LocalNetworkLogsSharedPreferences"

    companion object : SingletonHolder<LocalNetworkLogsManager, Application>(::LocalNetworkLogsManager)

    private fun clearPreferences() {
        preferences.edit().clear().commit()
    }

    private fun setString(value: String?, key: String) {
        preferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String): String? {
        return preferences.getString(key, "")
    }

    fun getHttpLoggingInterceptor(isLogsActive: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message ->

            if (isLogsActive) {
                if (isJSONValid(message)) {
                    Log.i("OkHttp", formatString(message))
                } else {
                    Log.i("OkHttp", message)
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

}