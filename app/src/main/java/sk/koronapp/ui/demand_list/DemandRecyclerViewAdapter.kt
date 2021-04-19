package sk.koronapp.ui.demand_list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.NetworkImageView
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.ui.demand_detail.DemandDetailActivity
import sk.koronapp.utilities.CustomImageLoader
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.Urls

class DemandRecyclerViewAdapter(
    private val context: Context,
    private val demands: MutableList<Demand>,
    private val client: Boolean? = null
) :
    RecyclerView.Adapter<DemandRecyclerViewAdapter.ViewHolder>() {
    private val defaultImage =
        BitmapFactory.decodeStream(context.resources.assets.open("default_avatar.png"))

    private lateinit var imageLoader: CustomImageLoader

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        imageLoader = HttpRequestManager.getImageLoader(context)
        val viewId = if (client == null || client)
            R.layout.demand_list_item
        else
            R.layout.demand_list_item_avatar_right

        val view = LayoutInflater.from(parent.context).inflate(viewId, parent, false)
        return ViewHolder(view) { onDemandClicked(context, demands[it].id) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val demand = demands[position]
        holder.title.text = demand.title
        holder.address.text = demand.address
        holder.expiredAt.text = demand.expiredAtString()

        imageLoader.setProgressBar(holder.progressBar)
        holder.avatar.setErrorImageBitmap(defaultImage)
        holder.avatar.setDefaultImageBitmap(defaultImage)
        if (client == null || !client) {
            holder.avatar.setImageUrl(
                Urls.AVATAR + demand.client.getUsernameUrlEncoded() + ".png",
                imageLoader
            )
        } else if (demand.volunteer != null) {
            holder.avatar.setImageUrl(
                Urls.AVATAR + demand.volunteer!!.getUsernameUrlEncoded() + ".png",
                imageLoader
            )
        } else {
            holder.imageLayout.visibility = View.GONE
        }
        val shape = RoundRectShape(floatArrayOf(12F, 12F, 12F, 12F, 12F, 12F, 12F, 12F), null, null)
        val shapeDrawable = ShapeDrawable(shape)
        var color = R.color.stateCreated
        when (demand.state) {
            Demand.State.created -> color = R.color.stateCreated
            Demand.State.accepted -> color = R.color.stateAccepted
            Demand.State.completed -> color = R.color.stateCompleted
            Demand.State.approved -> color = R.color.stateApproved
            Demand.State.expired -> color = R.color.stateExpired
        }
        shapeDrawable.paint.color = ContextCompat.getColor(context, color)
        holder.itemView.background = shapeDrawable
    }

    private fun onDemandClicked(context: Context, demandId: Int) {
        HttpRequestManager.sendRequest(
            context,
            null,
            RequestType.DEMAND,
            Request.Method.GET,
            { jsonObject: JSONObject, success: Boolean ->
                if (success) {
                    val demand = ObjectMapper().readValue(jsonObject.toString(), Demand::class.java)
                    val intent = Intent(context, DemandDetailActivity::class.java)
                    intent.putExtra("demand", demand)
                    if (client == true)
                        intent.putExtra("type", DemandDetailActivity.Type.CLIENT.value)
                    else
                        intent.putExtra("type", DemandDetailActivity.Type.VOLUNTEER.value)
                    context.startActivity(intent)
                } else {
                    TODO("Error popup")
                }
            },
            demandId.toString()
        )
    }

    private fun onItemLongClicked(position: Int) {
        val demand = demands[position]
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("Do you want to delete demand " + demand.title + "?")
        dialogBuilder.setPositiveButton("Yes") { dialog, which ->
            HttpRequestManager.sendRequest(
                context,
                null,
                RequestType.DEMAND,
                Request.Method.DELETE,
                fun(jsonObject: JSONObject, success: Boolean) {
                    demands.removeAt(position)
                    this.notifyDataSetChanged()
                    dialog.dismiss()

                },
                demand.id.toString()
            )
        }
        dialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        dialogBuilder.show()
    }

    override fun getItemCount(): Int = demands.size

    inner class ViewHolder(view: View, onItemClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
            if (client == true) {
                itemView.setOnLongClickListener {
                    onItemLongClicked(adapterPosition)
                    true
                }
            }
        }

        val title: TextView = view.findViewById(R.id.demand_title)
        val address: TextView = view.findViewById(R.id.demand_address)
        val expiredAt: TextView = view.findViewById(R.id.demand_expired_at)
        val avatar: NetworkImageView = view.findViewById(R.id.demand_avatar)
        val progressBar: ProgressBar = view.findViewById(R.id.demand_progress_bar)
        val imageLayout: LinearLayout = view.findViewById(R.id.demand_image_layout)
        override fun toString(): String {
            return title.text as String
        }
    }
}