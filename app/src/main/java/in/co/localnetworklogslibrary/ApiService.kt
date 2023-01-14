package `in`.co.localnetworklogslibrary

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("users")
    suspend fun getUsers(): Response<UsersResponseModel>
}