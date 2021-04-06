package sk.koronapp.models

import java.io.Serializable

class User constructor(
    private val token: String,
    private val username: String,
    private val address: String,
    private val avatar: String):Serializable{

    fun get_token(): String {
        return this.token
    }

    fun get_username(): String {
        return this.username
    }

    fun get_address(): String {
        return this.address
    }

    fun get_avatar(): String {
        return this.avatar
    }
}