package `in`.co.localnetworklogs

import kotlin.reflect.KFunction0

open class SingletonHolder<out T>(private val constructor: KFunction0<T>) {

    @Volatile
    private var instance: T? = null

    fun getInstance(): T =
        (instance ?: synchronized(this) {
            instance ?: constructor().also { instance = it }
        })
}