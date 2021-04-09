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
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.utilities.HttpRequestManager
import sk.koronapp.utilities.RequestType
import sk.koronapp.utilities.ResponseInterface

class DemandFragment : Fragment(), ResponseInterface {

    private var demandList: Array<Demand> = emptyArray()
    private lateinit var view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HttpRequestManager.sendRequestForJsonArray(
            requireContext(),
            RequestType.DEMAND,
            ::responseHandler
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.demand_list, container, false) as RecyclerView
        return view
    }

    override fun responseHandler(response: Any) {
        demandList = ObjectMapper().readValue(response.toString(), Array<Demand>::class.java)

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = DemandRecyclerViewAdapter(context, demandList)
        }
    }

}