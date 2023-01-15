package `in`.co.localnetworklogslibrary

data class UserDataResponseModel(
    var data: ModelUser
) {
    data class ModelUser(
        var id: String,
        var email: String,
        var first_name: String,
        var last_name: String,
        var avatar: String
    )
}
