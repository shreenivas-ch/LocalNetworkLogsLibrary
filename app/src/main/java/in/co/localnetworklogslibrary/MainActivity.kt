package `in`.co.localnetworklogslibrary

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mainViewModel = MainViewModel()
        mainViewModel.getUsers()

        findViewById<TextView>(R.id.txtCallAPI).setOnClickListener {
            mainViewModel.getUserData()
        }
    }
}