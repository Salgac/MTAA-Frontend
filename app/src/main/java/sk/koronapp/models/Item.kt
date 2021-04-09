package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonProperty

class Item(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("quantity")
    val quantity: Float,
    @JsonProperty("unit")
    val unit: Unit,
    @JsonProperty("price")
    val price: Float
)