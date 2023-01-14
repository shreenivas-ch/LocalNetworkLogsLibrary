package `in`.co.localnetworklogslibrary

import `in`.co.localnetworklogs.LocalNetworkLogsManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mainViewModel = MainViewModel()
        mainViewModel.getUsers()
    }
}