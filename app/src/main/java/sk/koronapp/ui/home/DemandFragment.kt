package sk.koronapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.demand_list_item.view.*
import org.json.JSONArray
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType

class DemandFragment : Fragment() {

    private lateinit var demandList: Array<Demand>
    private lateinit var view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HttpRequestManager.sendRequestForJsonArray(
            requireContext(),
            RequestType.DEMAND,
            { jsonArray: JSONArray, success: Boolean ->
                if (success) {
                    demandList =
                        ObjectMapper().readValue(jsonArray.toString(), Array<Demand>::class.java)

                    with(view) {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DemandRecyclerViewAdapter(context, demandList)
                    }
                } else {
                    TODO("Error popup")
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.demand_list, container, false) as RecyclerView
        return view
    }

}