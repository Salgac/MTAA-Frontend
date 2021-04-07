package sk.koronapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import com.android.volley.Request.Method
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import sk.koronapp.models.User
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.ResponseInterface
import java.io.Serializable
import java.util.*

class LoginActivity : AppCompatActivity(), LocationListener, ResponseInterface {

    private var address: String = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
            updateAddress()
            jsonObj.put("address",this.address)
            Toast.makeText(this, this.address, Toast.LENGTH_LONG).show()
        }

        //send request and get response
        HttpRequestManager.sendRequest(this, jsonObj, type, Method.POST, ::responseHandler)
    }

    override fun responseHandler(response:JSONObject){
        //create new user
        val resp = JSONObject(response.toString())

        if(!resp.has("user")){
            //TODO: error handling
            Toast.makeText(this, resp.toString(), Toast.LENGTH_SHORT).show()
            return
        }

        val token = resp.get("token").toString()
        val respObj = resp.getJSONObject("user")
        val username = respObj.get("username").toString()
        val address = respObj.get("address").toString()
        val avatar = respObj.get("avatar").toString()

        val user = User(token,username,address,avatar)

        //launch main activity
        val intent = Intent(this@LoginActivity,MainActivity::class.java)
        intent.putExtra("user", user as Serializable)
        startActivity(intent)
    }

    //updates location and address line
    private fun updateAddress(){
        //check permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //ask for permissions
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),2)
            return updateAddress()
        }

        //update location
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f,this)
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            //get current address from location and return in into variable
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude,location.longitude,1)

            this.address = addresses[0].getAddressLine(0)
        }
        else{
            //handle error
            Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

}