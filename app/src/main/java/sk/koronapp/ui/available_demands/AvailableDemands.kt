package sk.koronapp.ui.available_demands

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import sk.koronapp.R
import sk.koronapp.models.Demand
import sk.koronapp.ui.demand_list.DemandListFragment


class AvailableDemands : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.available_demands_fragment, container, false)
        val searchView: CustomSearchView = root.findViewById(R.id.demand_search)

        val queryPair = Pair("state", Demand.State.created.toString())
        val demandFragment = DemandListFragment(queryPair)

        childFragmentManager.beginTransaction()
            .add(R.id.demand_search_layout, demandFragment, "demand_fragment")
            .commit()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                demandFragment.displayDemandsByAddress(newText)
                return true
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                demandFragment.displayDemandsByAddress(null)
                return false
            }
        })

        return root
    }
}