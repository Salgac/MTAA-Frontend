package sk.koronapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.ImageLoader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.drawer_layout.view.*
import org.json.JSONObject
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.Urls
import java.io.File
import java.io.FileOutputStream

open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerNavView: NavigationView
    private lateinit var appBarHeader: View
    private lateinit var imageLoader: ImageLoader

    private val PREF_TOKEN: String = "token"
    private val SELECT_IMAGE = 420
    private val CROP_IMAGE = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextAppearance(this, R.style.MainTitle)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerNavView = findViewById(R.id.drawer_nav_view)
        appBarHeader = drawerNavView.getHeaderView(0)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_available_demands, R.id.navigation_new_demand
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setDrawerValues()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setDrawerValues() {
        imageLoader = HttpRequestManager.getImageLoader(this)
        appBarHeader.drawer_image.setImageUrl(
            Urls.AVATAR + HttpRequestManager.getUser()!!.getUsernameUrlEncoded() + ".png",
            imageLoader
        )

        appBarHeader.drawer_name.text = HttpRequestManager.getUser()!!.username
        appBarHeader.drawer_address.text = HttpRequestManager.getUser()!!.address

        logout_button.setOnClickListener {
            //return to Login page
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            getSharedPreferences("pref", Context.MODE_PRIVATE).edit().putString(PREF_TOKEN, null)
                .apply()
            startActivity(intent)
            finish()
        }

        appBarHeader.drawer_image.setOnClickListener {
            //load galery in new intent, and get the picked image in onActivityResult method
            try {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, SELECT_IMAGE)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.error_image, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val path = data.data
                cropImage(path!!)
            }
        }

        if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val bitmap: Bitmap = data.extras?.getParcelable("data")!!
                sendImage(bitmap)
            }
            setDrawerValues()
        }
    }

    private fun cropImage(path: Uri) {
        try {
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(path, "image/*")
            intent.putExtra("crop", true)

            // indicate aspect of desired crop
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("outputX", 300)
            intent.putExtra("outputY", 300)
            intent.putExtra("scaleUpIfNeeded", true)

            // retrieve data on return
            intent.putExtra("return-data", true)
            startActivityForResult(intent, CROP_IMAGE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_crop, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendImage(bitmap: Bitmap) {
        //save image into tmp storage
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val tempFile = File(storageDir, "Avatar.jpg")

        val fOut = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
        fOut.flush()
        fOut.close()
        appBarHeader.drawer_image.setImageBitmap(BitmapFactory.decodeFile(tempFile.absolutePath))

        //send to server as multipart
        HttpRequestManager.sendRequestWithImage(this, RequestType.USER, tempFile.readBytes(),
            fun(jsonObject: JSONObject, success: Boolean) {
                //handle errors
                if (!success) {
                    //TODO handle error
                    return
                }
                //update image in drawer from server
                HttpRequestManager.removeFromCache(
                    Urls.AVATAR + HttpRequestManager.getUser()!!.getUsernameUrlEncoded() + ".png"
                )
            })
    }
}