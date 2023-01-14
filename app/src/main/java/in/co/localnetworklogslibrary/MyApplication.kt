package `in`.co.localnetworklogslibrary

import `in`.co.localnetworklogs.LocalNetworkLogsManager
import android.app.Application

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        LocalNetworkLogsManager.getInstance(this)
    }
}