package sk.koronapp.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class Item
    : Serializable {
    @JsonProperty("id")
    var id: Int = 0

    @JsonProperty("name")
    var name: String = ""

    @JsonProperty("quantity")
    var quantity: Float = 0F

    @JsonProperty("unit")
    var unit: String = ""

    @JsonProperty("price")
    var price: Float = 0F
}