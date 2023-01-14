package `in`.co.localnetworklogslibrary

import `in`.co.localnetworklogs.LocalNetworkLogsManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object RetrofitFactory {

    const val BASE_URL: String = "https://reqres.in/api/"

    fun makeRetrofitService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(makeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build().create(ApiService::class.java)
    }

    private fun makeOkHttpClient(
        log: Boolean = true
    ): OkHttpClient {

        var clientBuilder =
            OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val requestBuilder: Request.Builder = chain.request().newBuilder()
                    val decodeUrl = URLDecoder.decode(chain.request().url.toString(), "UTF-8")
                    requestBuilder.url(decodeUrl)
                    requestBuilder.header("Content-Type", "application/json; charset=utf-8")
                    requestBuilder.header("Accept", "application/json")
                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)

        /*clientBuilder.addNetworkInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        )*/
        var httpLoggingInterceptor =
            LocalNetworkLogsManager.getInstance().getHttpLoggingInterceptor(true)

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addNetworkInterceptor(httpLoggingInterceptor)

        //clientBuilder.addInterceptor(httpLoggingInterceptor)

        return clientBuilder.build()
    }
}