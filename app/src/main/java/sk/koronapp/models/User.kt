package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.net.URLEncoder

class User : Serializable {
    @JsonProperty("username")
    val username: String = ""

    @JsonProperty("address")
    val address: String = ""

    @JsonProperty("avatar")
    var avatar: String = ""

    var token: String = ""

    fun getUsernameUrlEncoded(): String {
        return URLEncoder.encode(username, "utf-8")
    }

}