package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class Demand : Serializable {
    @JsonProperty("id")
    var id: Int = 0

    @JsonProperty("address")
    var address: String = ""

    @JsonProperty("title")
    var title: String = ""

    @JsonProperty("created_at")
    var createdAt: Date = Date()

    @JsonProperty("expired_at")
    var expiredAt: Date = Date()

    @JsonProperty("state")
    var state: State = State.created

    @JsonProperty("client")
    var client: User = User()

    @JsonProperty("volunteer")
    var volunteer: User? = null

    @JsonProperty("items")
    var items: List<Item>? = null

    private var formatter: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
    fun expiredAtString(): String {
        return formatter.format(expiredAt)
    }

    fun createdAtString(): String {
        return formatter.format(expiredAt)
    }

    override fun toString(): String {
        return title
    }

    enum class State() {
        created, accepted, completed, approved, expired,
    }
}
