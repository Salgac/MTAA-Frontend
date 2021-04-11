package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class Item(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("quantity")
    val quantity: Float,
    @JsonProperty("unit")
    val unit: String,
    @JsonProperty("price")
    val price: Float
) : Serializable