package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.text.SimpleDateFormat
import java.util.*

enum class State() {
    created, accepted, completed, approved, expired,
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Demand(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("created_at")
    val createdAt: Date,
    @JsonProperty("expired_at")
    val expiredAt: Date,
    @JsonProperty("state")
    val state: State,

    @JsonProperty("client")
    val client: User,
    @JsonProperty("volunteer")
    val volunteer: User?,

    @JsonProperty("items")
    val items: List<Item>?
) {
    val formatter: SimpleDateFormat = SimpleDateFormat("dd.MM hh:mm")
    fun expiredAtString(): String {
        return formatter.format(expiredAt)
    }

    fun createdAtString(): String {
        return formatter.format(expiredAt)
    }
}
