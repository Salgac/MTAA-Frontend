package sk.koronapp.utilities

import android.content.Context
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

enum class RequestType {
    LOGIN, REGISTER, USER, AVATAR, DEMAND
}

class HttpRequestManager {
    companion object {
        //function that sends requests
        fun sendRequest(
            context: Context,
            jsonObj: JSONObject,
            type: RequestType,
            method: Int,
            handlerFunction: (response:JSONObject) -> Unit,
            token: String = "",
            urlExtra: String = ""
        ) {
            val que = Volley.newRequestQueue(context)
            val url = getUrlFromType(type) + urlExtra

            val jsonObjectRequest = object : JsonObjectRequest(
                method, url , jsonObj,
                { response ->
                    handlerFunction(response)
                }, { error ->
                    handlerFunction(JSONObject(String(error.networkResponse.data)))
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"

                    if(token != ""){
                        params["Authorization"] = "Token $token"
                    }
                    return params
                }
            }
            que.add(jsonObjectRequest)
        }

        private fun getUrlFromType(type:RequestType): String {
            return when(type){
                RequestType.LOGIN -> Urls.LOGIN
                RequestType.REGISTER -> Urls.REGISTER
                RequestType.USER -> Urls.USER
                RequestType.AVATAR -> Urls.AVATAR
                RequestType.DEMAND -> Urls.DEMAND
            }
        }

    }
}

interface ResponseInterface {
    fun responseHandler(response:JSONObject)
}