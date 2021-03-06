package sk.koronapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var addressManager: AddressManager
    private var userIsDummy = false

    private val PREF_TOKEN: String = "token"
    private val PREF_USER: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        addressManager = AddressManager(this)

        login_toolbar.setTitleTextAppearance(this, R.style.CustomLoginTitle)

        usernameField.doAfterTextChanged {
            textWatch()
        }

        passwordField.doAfterTextChanged {
            textWatch()
        }

        login_button.setOnClickListener {
            if (passwordLengthValidator()) {
                login_button.isEnabled = false
                sendRequest(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                    RequestType.LOGIN
                )
            }
        }

        register_button.setOnClickListener {
            if (passwordLengthValidator()) {
                register_button.isEnabled = false
                sendRequest(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                    RequestType.REGISTER
                )
            }
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

        //clear error messages
        usernameLayout.error = null
        inputLayout.error = null
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
                //handle errors
                if (!success) {
                    if (jsonObject.has("detail")) {
                        //auth error - throw error
                        inputLayout.error = getString(R.string.auth_error)
                    } else if (jsonObject.has("username")) {
                        //username already exists - throw error on username
                        usernameLayout.error = getString(R.string.reg_error_duplicate)
                    }
                    //enable buttons
                    login_button.isEnabled = true
                    register_button.isEnabled = true
                    return
                }

                //create new user
                val userJson = jsonObject.getJSONObject("user")
                val user = ObjectMapper().readValue(userJson.toString(), User::class.java)
                val token = jsonObject.get("token").toString()
                user.token = token
                HttpRequestManager.setUser(user)


                //set token and preferences
                HttpRequestManager.setUser(user)
                getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
                    .putString(PREF_TOKEN, token)
                    .putString(PREF_USER, userJson.toString())
                    .apply()


                //launch main activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
    }

}