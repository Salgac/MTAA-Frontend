package sk.koronapp.ui.demand_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType

class DemandListFragment(private val queryPair: Pair<String, String>? = null) : Fragment() {

    private lateinit var demandList: List<Demand>
    private lateinit var view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var client: Boolean? = null
        var query = ""
        if (queryPair != null) {
            if (queryPair.first == "user") {
                client = true
                client = queryPair.second == "client"
            }
            query = "?" + queryPair.first + "=" + queryPair.second
        }
        HttpRequestManager.sendRequestForJsonArray(
            requireContext(),
            RequestType.DEMAND,
            { jsonArray: JSONArray, success: Boolean ->
                if (success) {
                    demandList = ObjectMapper().readValue(
                        jsonArray.toString(),
                        object : TypeReference<List<Demand>>() {})

                    with(view) {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DemandRecyclerViewAdapter(context, demandList, client)
                    }
                } else {
                    TODO("Error popup")
                }
            },
            query
        )
    }

    fun displayDemandsByAddress(address: String?) {
        var demands = demandList
        if (address != null)
            demands = demandList.filter { it.address.toLowerCase().contains(address.toLowerCase()) }
        with(view) {
            view.adapter = DemandRecyclerViewAdapter(context, demands, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.demand_list, container, false) as RecyclerView
        return view
    }

}