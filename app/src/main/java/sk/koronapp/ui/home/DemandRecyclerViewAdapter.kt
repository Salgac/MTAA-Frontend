package sk.koronapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.Urls


class DemandRecyclerViewAdapter(private val context: Context, private val demands: Array<Demand>) :
    RecyclerView.Adapter<DemandRecyclerViewAdapter.ViewHolder>() {

    lateinit var holder: ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.demand_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.holder = holder

        val demand = demands[position]
        holder.title.text = demand.title
        holder.address.text = demand.address
        holder.expiredAt.text = demand.expiredAtString()

        val imageLoader = HttpRequestManager.getImageLoader(context)
        imageLoader.setProgressBar(holder.progressBar)
        holder.avatar.setImageUrl(Urls.AVATAR + demand.client.avatar, imageLoader)
    }

    override fun getItemCount(): Int = demands.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.demand_title)
        val address: TextView = view.findViewById(R.id.demand_address)
        val expiredAt: TextView = view.findViewById(R.id.demand_expired_at)
        val avatar: NetworkImageView = view.findViewById(R.id.demand_avatar)
        val progressBar: ProgressBar = view.findViewById(R.id.demand_progress_bar)
        override fun toString(): String {
            return title.text as String
        }
    }
}