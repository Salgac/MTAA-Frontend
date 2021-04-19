package sk.koronapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import sk.koronapp.models.User
import sk.koronapp.utilities.HttpRequestManager
import java.io.Serializable

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1500
    private val PREF_TOKEN: String = "token"
    private val PREF_USER: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //get shared preferences
        val pref: SharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val token = pref.getString(PREF_TOKEN, null)
        lateinit var user: User

        if (token != null) {
            //login user
            HttpRequestManager.setToken(token)
            val userJson = pref.getString(PREF_USER, null)
            user = ObjectMapper().readValue(userJson.toString(), User::class.java)
        }

        Handler().postDelayed({
            if (token == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user", user as Serializable)
                startActivity(intent)
            }
            finish()
        }, SPLASH_TIME_OUT)
    }
}