package sk.koronapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.android.volley.Request.Method
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import sk.koronapp.models.User
import sk.koronapp.utilities.AddressManager
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.ResponseInterface
import java.io.Serializable
import java.util.*

class LoginActivity : AppCompatActivity(), ResponseInterface {

    private lateinit var addressManager:AddressManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        addressManager = AddressManager(this)

        usernameField.doAfterTextChanged {
            textWatch()
        }

        passwordField.doAfterTextChanged {
            textWatch()
        }

        login_button.setOnClickListener{
            sendRequest(usernameField.text.toString().trim(), passwordField.text.toString().trim(), RequestType.LOGIN)
        }

        register_button.setOnClickListener{
            sendRequest(usernameField.text.toString().trim(), passwordField.text.toString().trim(), RequestType.REGISTER)
        }
    }

    private fun textWatch(){
        val usernameEmpty: Boolean = usernameField.text.toString().trim().isEmpty()
        val passwordEmpty: Boolean = passwordField.text.toString().trim().isEmpty()

        if(usernameEmpty || passwordEmpty){
            login_button.isEnabled = false
            login_button.setBackgroundColor(resources.getColor(R.color.colorButtonShade))

            register_button.isEnabled = false
            register_button.setBackgroundColor(resources.getColor(R.color.colorButtonShadeLight))
        }
        else{
            login_button.isEnabled = true
            login_button.setBackgroundColor(resources.getColor(R.color.colorPrimary))

            register_button.isEnabled = true
            register_button.setBackgroundColor(resources.getColor(R.color.colorPrimaryLight))
        }
    }

    //function that sends requests
    private fun sendRequest(username:String, password:String, type:RequestType){

        val jsonObj = JSONObject()
        jsonObj.put("username",username)
        jsonObj.put("password",password)
        if(type==RequestType.REGISTER){
                jsonObj.put("address", addressManager.getAddress())
        }
        //send request and get response
        HttpRequestManager.sendRequest(this, jsonObj, type, Method.POST, ::responseHandler)
    }

    override fun responseHandler(response: Any) {
        //create new user
        val resp = JSONObject(response.toString())

        if (!resp.has("user")) {
            //TODO: error handling
            Toast.makeText(this, resp.toString(), Toast.LENGTH_SHORT).show()
            return
        }

        val token = resp.get("token").toString()
        val respObj = resp.getJSONObject("user")
        val user = ObjectMapper().readValue(respObj.toString(), User::class.java)

        HttpRequestManager.setToken(token)

        //launch main activity
        val intent = Intent(this@LoginActivity,MainActivity::class.java)
        intent.putExtra("user", user as Serializable)
        startActivity(intent)
    }
}