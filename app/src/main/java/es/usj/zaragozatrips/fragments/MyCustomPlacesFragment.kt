package es.usj.zaragozatrips.fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.CustomDataManager
import es.usj.zaragozatrips.services.DataManager
import kotlinx.android.synthetic.main.fragment_myplace_list.*

class MyCustomPlacesFragment : Fragment() {

    private var mCustomColumnCount = 1
    private var mCustomListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity.title = getString(R.string.title_base, getString(R.string.my_places))

        if (arguments != null) {
            mCustomColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    private lateinit var rView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rView = inflater.inflate(R.layout.fragment_my_custom_places_list, container, false)

        CustomDataManager.getData(::onDataReady)
        return rView
    }

    private fun onDataReady(customPlaces: Array<CustomPlace>) {
        if(customPlaces.isEmpty()){
            loadingError()
            return
        }

        val recyclerView = rView.findViewById(R.id.custom_place_list) as RecyclerView
        if (mCustomColumnCount <= 1) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, mCustomColumnCount)
        }
        showPlaces(customPlaces)
    }

    private fun showPlaces(customPlaces: Array<CustomPlace>) {
        val recyclerView = rView.findViewById(R.id.custom_place_list) as RecyclerView
        recyclerView.adapter = MyCustomPlaceRecyclerViewAdapter(customPlaces, mCustomListener)
    }

    private fun loadingError() {
        Toast.makeText(activity, getString(R.string.problem_loading), Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mCustomListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCustomListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(customPlace: CustomPlace)
    }

    companion object {
        private val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int): MyPlacesFragment {
            val fragment = MyPlacesFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
