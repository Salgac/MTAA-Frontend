package sk.koronapp.ui.demand_detail

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.NetworkImageView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_demand_detail.*
import kotlinx.android.synthetic.main.content_demand_detail.*
import org.json.JSONObject
import sk.koronapp.MainActivity
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.models.Item
import sk.koronapp.utilities.CustomImageLoader
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.Urls
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DemandDetailActivity : AppCompatActivity() {

    enum class Type(val value: String) : Serializable {
        NEW("new"),
        CLIENT("client"),
        VOLUNTEER("volunteer")
    }

    private lateinit var toolbar: CollapsingToolbarLayout
    private lateinit var demand: Demand
    private lateinit var adapter: ItemRecyclerViewAdapter
    private lateinit var itemRecyclerView: RecyclerView

    private lateinit var imageLoader: CustomImageLoader
    private lateinit var imageView: NetworkImageView

    private val cal = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
    private var color = R.color.stateCreated

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demand_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar = findViewById(R.id.toolbar_layout)
        toolbar.setExpandedTitleColor(resources.getColor(R.color.colorTransparent))

        itemRecyclerView = findViewById(R.id.item_list)
        itemRecyclerView.layoutManager = LinearLayoutManager(this)

        cal.set(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH) + 2
        )

        imageView = findViewById(R.id.demand_avatar)
        imageLoader = HttpRequestManager.getImageLoader(this)
        if (intent.hasExtra("demand")) {
            demand = intent.getSerializableExtra("demand") as Demand
            toolbar.title = demand.title
            demand_detail_title.setText(demand.title)
            demand_detail_address.setText(demand.address)
            demand_detail_expiration_date.setText(demand.expiredAtString())

            adapter = ItemRecyclerViewAdapter(this, ArrayList(demand.items!!.toMutableList()))

            when (demand.state) {
                Demand.State.created -> color = R.color.stateCreated
                Demand.State.accepted -> color = R.color.stateAccepted
                Demand.State.completed -> color = R.color.stateCompleted
                Demand.State.approved -> color = R.color.stateApproved
                Demand.State.expired -> color = R.color.stateExpired
            }

        } else {
            demand_detail_button.text = resources.getString(R.string.demand_button_create)
            adapter = ItemRecyclerViewAdapter(this, emptyArray<Item>().toMutableList())
            demand_detail_expiration_date.setText(dateFormatter.format(cal.time))
        }

        itemRecyclerView.adapter = adapter

        demand_detail_new_item.visibility = View.GONE
        toolbar.setBackgroundColor(resources.getColor(color))
        toolbar.setContentScrimColor(resources.getColor(color))

        when (intent.getSerializableExtra("type")) {
            Type.NEW.value -> prepareEditableDemandLayout(false)
            Type.CLIENT.value -> prepareClientDemandLayout()
            Type.VOLUNTEER.value -> prepareVolunteerDemandLayout()
        }
    }

    private fun sendStateRequest(state: Demand.State) {
        val jsonObject = JSONObject()
        jsonObject.put("state", state)
        HttpRequestManager.sendRequest(
            this,
            jsonObject,
            RequestType.DEMAND,
            Request.Method.PATCH,
            fun(jsonObject: JSONObject, success: Boolean) {
                if (success) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            demand.id.toString()
        )
    }

    private fun prepareVolunteerDemandLayout() {
        imageView.setImageUrl(Urls.AVATAR + demand.client.username + ".png", imageLoader)
        demand_detail_username.text = demand.client.username
        when (demand.state) {
            Demand.State.created -> {
                demand_detail_button.text = resources.getString(R.string.demand_button_accept)
                demand_detail_button.setOnClickListener {
                    sendStateRequest(Demand.State.accepted)
                }
            }
            Demand.State.accepted -> {
                demand_detail_button.text = resources.getString(R.string.demand_button_complete)
                demand_detail_button.setOnClickListener {
                    sendStateRequest(Demand.State.completed)
                }
            }
            Demand.State.completed -> {
                demand_detail_button.isEnabled = false
                demand_detail_button.text = resources.getString(R.string.demand_button_approve)
            }
            else -> {
                demand_detail_button.visibility = View.GONE
            }
        }
    }

    private fun prepareClientDemandLayout() {
        if (demand.volunteer != null) {
            imageView.setImageUrl(
                Urls.AVATAR + demand.volunteer!!.getUsernameUrlEncoded() + ".png",
                imageLoader
            )
        }
        demand_detail_username.text = demand.volunteer?.username
        when (demand.state) {
            Demand.State.created -> {
                demand_detail_button.text = resources.getString(R.string.demand_button_update)
                prepareEditableDemandLayout(true)
                demand_detail_username.text = getString(R.string.demand_not_accepted)
            }
            Demand.State.accepted -> {
                demand_detail_button.text = resources.getString(R.string.demand_button_complete)
                demand_detail_button.isEnabled = false
            }
            Demand.State.completed -> {
                demand_detail_button.text = resources.getString(R.string.demand_button_approve)
                demand_detail_button.setOnClickListener {
                    sendStateRequest(Demand.State.approved)
                }
            }
            else -> {
                demand_detail_button.visibility = View.GONE
            }
        }
    }

    private fun prepareEditableDemandLayout(update: Boolean) {
        demand_detail_title.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                toolbar.title = demand_detail_title.text.toString()
        }

        if (!update) {
            demand_detail_address.setText(HttpRequestManager.getUser()?.address)
        }

        demand_detail_new_item.visibility = View.VISIBLE
        demand_detail_new_item.setOnClickListener {
            app_bar.setExpanded(false)
            newItemDialog()
        }

        demand_detail_expiration_date.setOnClickListener {
            val listener =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    demand_detail_expiration_date.setText("$dayOfMonth." + (monthOfYear + 1) + ".$year")
                }
            val dpDialog = DatePickerDialog(
                this,
                listener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dpDialog.datePicker.minDate = cal.timeInMillis
            dpDialog.show()
        }

        demand_detail_button.setOnClickListener {
            if (!update)
                demand = Demand()
            demand.items = adapter.getItems()
            demand.title = demand_detail_title.text.toString()
            demand.address = demand_detail_address.text.toString()
            demand.expiredAt =
                dateFormatter.parse(demand_detail_expiration_date.text.toString())
            if (update)
                sendDemand(demand, Request.Method.PUT, demand.id.toString())
            else
                sendDemand(demand, Request.Method.POST)
        }

    }

    private fun sendDemand(demand: Demand, method: Int, extra: String = "") {
        val mapper = ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val jsonObject = JSONObject(mapper.writeValueAsString(demand))

        HttpRequestManager.sendRequest(
            this,
            jsonObject,
            RequestType.DEMAND,
            method,
            fun(jsonObject: JSONObject, success: Boolean) {
                if (success) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            extra
        )
    }

    private fun newItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.new_item_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        val btnAdd = dialogView.findViewById<Button>(R.id.new_item_btn_add)
        val btnCancel = dialogView.findViewById<Button>(R.id.new_item_btn_cancel)
        val nameEditText = dialog.findViewById<EditText>(R.id.new_item_name)
        val quantityEditText = dialog.findViewById<EditText>(R.id.new_item_quantity)
        val unitSpinner = dialog.findViewById<Spinner>(R.id.new_item_unit_spinner)
        val priceEditText = dialog.findViewById<EditText>(R.id.new_item_price)

        ArrayAdapter.createFromResource(
            this,
            R.array.units,
            android.R.layout.simple_spinner_dropdown_item
        )
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                unitSpinner!!.adapter = adapter
            }

        btnCancel.setOnClickListener {
            dialog.cancel()
        }

        btnAdd.setOnClickListener {
            val item = Item()
            item.name = nameEditText!!.text.toString()
            item.quantity = quantityEditText!!.text.toString().toFloat()
            item.unit = unitSpinner!!.selectedItem.toString()
            item.price = priceEditText!!.text.toString().toFloat()
            if (!adapter.addItem(item)) {
                //TODO error - multiple items with same name
            } else {
                dialog.dismiss()
            }
        }
    }

}