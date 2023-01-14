package `in`.co.localnetworklogslibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var apiInterface = RetrofitFactory.makeRetrofitService()

    fun getUsers() {
        viewModelScope.launch {
            apiInterface.getUsers()
        }
    }
}