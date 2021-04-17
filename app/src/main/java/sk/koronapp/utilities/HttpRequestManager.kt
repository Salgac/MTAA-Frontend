package sk.koronapp.utilities

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import sk.koronapp.R

enum class RequestType {
    LOGIN, REGISTER, USER, AVATAR, DEMAND
}

class HttpRequestManager {
    companion object {
        private var token: String? = null
        fun setToken(token: String) {
            this.token = token
        }

        //function that sends requests
        fun sendRequest(
            context: Context,
            jsonObj: JSONObject?,
            type: RequestType,
            method: Int,
            handlerFunction: (response: JSONObject, success: Boolean) -> Unit,
            urlExtra: String = ""
        ) {
            val que = Volley.newRequestQueue(context)
            val url = getUrlFromType(type) + urlExtra

            val jsonObjectRequest = object : JsonObjectRequest(
                method, url, jsonObj,
                { response ->
                    handlerFunction(response, true)
                }, { error ->
                    if (noConnectionErrorPresent(context, error))
                        handlerFunction(JSONObject(String(error.networkResponse.data)), false)
                    else
                        handlerFunction(JSONObject(), false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return defaultHeaders()
                }
            }
            que.add(jsonObjectRequest)
        }

        fun sendRequestForJsonArray(
            context: Context,
            type: RequestType,
            handlerFunction: (response: JSONArray, success: Boolean) -> Unit,
            urlExtra: String = ""
        ) {
            val que = Volley.newRequestQueue(context)
            val url = getUrlFromType(type) + urlExtra

            val jsonArrayRequest = object : JsonArrayRequest(url,
                { response ->
                    handlerFunction(response, true)
                }, { error ->
                    if (noConnectionErrorPresent(context, error))
                        handlerFunction(JSONArray(String(error.networkResponse.data)), false)
                    else
                        handlerFunction(JSONArray(), false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return defaultHeaders()
                }
            }
            que.add(jsonArrayRequest)
        }

        //TODO
        fun sendRequestWithImage(
            context: Context,
            type: RequestType,
            image: ByteArray,
            handlerFunction: (response: JSONObject, success: Boolean) -> Unit
        ) {
            val que = Volley.newRequestQueue(context)
            val url = getUrlFromType(type)

            val multipartRequest = object : VolleyMultipartRequest(
                Method.PUT, url,
                { response ->
                    handlerFunction(JSONObject(String(response.data)), true)
                }, { error ->
                    if (noConnectionErrorPresent(context, error))
                        handlerFunction(JSONObject(String(error.networkResponse.data)), false)
                    else
                        handlerFunction(JSONObject(), false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] = "Token $token"
                    params["Content-Disposition"] = "attachment; filename=avatar.jpg"
                    return params
                }

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params: MutableMap<String, DataPart> = HashMap()
                    params["file"] = DataPart("avatar.jpg", image, "image/jpeg")
                    return params
                }
            }
            que.add(multipartRequest)
        }

        private val cache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(20)
        fun getImageLoader(context: Context): CustomImageLoader {
            return CustomImageLoader(
                Volley.newRequestQueue(context),
                object : ImageLoader.ImageCache {
                    override fun getBitmap(url: String): Bitmap? {
                        return cache.get(url)
                    }

                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                }
            )
        }

        fun defaultHeaders(): MutableMap<String, String> {
            val params: MutableMap<String, String> = HashMap()
            params["Content-Type"] = "application/json"
            if (token != null) {
                params["Authorization"] = "Token $token"
            }
            return params
        }

        private fun getUrlFromType(type: RequestType): String {
            return when (type) {
                RequestType.LOGIN -> Urls.LOGIN
                RequestType.REGISTER -> Urls.REGISTER
                RequestType.USER -> Urls.USER
                RequestType.AVATAR -> Urls.AVATAR
                RequestType.DEMAND -> Urls.DEMAND
            }
        }

        private fun noConnectionErrorPresent(context: Context, error: VolleyError): Boolean {
            when (error) {
                is NetworkError -> Toast.makeText(
                    context,
                    context.getString(R.string.error_connection),
                    Toast.LENGTH_LONG
                ).show()
                is ServerError -> Toast.makeText(
                    context,
                    context.getString(R.string.error_server),
                    Toast.LENGTH_LONG
                ).show()
                is TimeoutError -> Toast.makeText(
                    context,
                    context.getString(R.string.error_timeout),
                    Toast.LENGTH_LONG
                ).show()
                else -> return true
            }
            return false
        }
    }
}