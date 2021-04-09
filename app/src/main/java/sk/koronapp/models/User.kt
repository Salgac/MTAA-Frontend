package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class User(
    @JsonProperty("username")
    val username: String,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("avatar")
    val avatar: String
) : Serializable