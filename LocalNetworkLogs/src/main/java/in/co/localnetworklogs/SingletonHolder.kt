package `in`.co.localnetworklogs

import kotlin.reflect.KFunction0

open class SingletonHolder<out T, in A>(private val constructor: KFunction0<LocalNetworkLogsManager>) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
}