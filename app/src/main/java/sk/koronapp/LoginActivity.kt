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
import java.io.Serializable
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var addressManager: AddressManager
    private var userIsDummy = false

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

        login_button.setOnClickListener {
            if (passwordLengthValidator())
                sendRequest(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                    RequestType.LOGIN
                )
        }

        register_button.setOnClickListener {
            if (passwordLengthValidator())
                sendRequest(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                    RequestType.REGISTER
                )
        }
    }

    private fun passwordLengthValidator(): Boolean {
        return if (passwordField.text.toString().trim().length < 6) {
            passwordLayout.error = getString(R.string.pass_error_length)
            userIsDummy = true
            false
        } else {
            passwordLayout.error = null
            true
        }
    }

    private fun textWatch() {
        val usernameEmpty: Boolean = usernameField.text.toString().trim().isEmpty()
        val passwordEmpty: Boolean = passwordField.text.toString().trim().isEmpty()

        if (usernameEmpty || passwordEmpty) {
            login_button.isEnabled = false
            register_button.isEnabled = false
        } else {
            login_button.isEnabled = true
            register_button.isEnabled = true
        }

        //check for password length
        if (userIsDummy) {
            if (passwordField.text.toString().length >= 6) {
                passwordLayout.error = null
            } else {
                passwordLayout.error = getString(R.string.pass_error_length)
            }
        }
    }

    //function that sends requests
    private fun sendRequest(username: String, password: String, type: RequestType) {

        val jsonObj = JSONObject()
        jsonObj.put("username", username)
        jsonObj.put("password", password)
        if (type == RequestType.REGISTER) {
            jsonObj.put("address", addressManager.getAddress())
        }
        //send request and get response
        HttpRequestManager.sendRequest(this, jsonObj, type, Method.POST,
            fun(jsonObject: JSONObject, success: Boolean) {
                //create new user
                if (!success) {
                    //TODO: error handling
                    Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show()
                    return
                }

                val token = jsonObject.get("token").toString()
                val userJson = jsonObject.getJSONObject("user")
                val user = ObjectMapper().readValue(userJson.toString(), User::class.java)

                HttpRequestManager.setToken(token)

                //launch main activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("user", user as Serializable)
                startActivity(intent)
            })
    }

}