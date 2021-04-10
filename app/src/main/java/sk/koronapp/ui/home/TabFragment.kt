package sk.koronapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sk.koronapp.R
import sk.koronapp.ui.demand_list.DemandListFragment
import java.util.*

class TabFragment : Fragment() {
    private var index: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.home_tab_fragment, container, false)

        val queryPair = Pair(
            "user",
            requireContext().resources.getString(TAB_TITLES[index - 1]).toLowerCase(Locale.ENGLISH)
        )
        val demandFragment = DemandListFragment(queryPair)
        root.id = R.id.home_tab_fragment
        childFragmentManager.beginTransaction().add(root.id, demandFragment, "demand_fragment")
            .commit()

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int): TabFragment {
            return TabFragment().apply {
                index = sectionNumber
            }
        }
    }
}