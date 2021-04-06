package sk.koronapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.isEnabled = true
        login_button.setOnClickListener{
            //send login request
            val url = "http://139.162.130.177:8000/api/login/"
            val jsonObj = JSONObject()
            val que = Volley.newRequestQueue(this)

            jsonObj.put("username","string")
            jsonObj.put("password","string")

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url, jsonObj,
                { response ->
                    Toast.makeText(
                        this,
                        //print response json TODO
                        "%s".format(response.toString()),
                        Toast.LENGTH_LONG
                    ).show()
                }, { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String,String> = HashMap()
                    params["Content-Type"] = "application/json"
                    return params
                }
            }

            que.add(jsonObjectRequest)
        }
    }

}