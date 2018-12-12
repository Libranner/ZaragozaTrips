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
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.DataManager
import kotlinx.android.synthetic.main.fragment_myplace_list.*

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class MyPlacesFragment : Fragment() {
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    private lateinit var rView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rView = inflater.inflate(R.layout.fragment_myplace_list, container, false)

        DataManager.getData(::onDataReady)
        return rView
    }

    private fun onDataReady(places: Array<Place>) {
        if(places.isEmpty()){
            loadingError()
            return
        }

        val recyclerView = rView.findViewById(R.id.place_list) as RecyclerView
        recyclerView.adapter = MyPlaceRecyclerViewAdapter(DataManager.places.toTypedArray(), mListener)

        if (mColumnCount <= 1) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, mColumnCount)
        }
        showPlaces(places)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodPlacesButton.setOnClickListener {
            filterPlaces("Comida & Bebida")
        }

        museumPlaceButton.setOnClickListener {
            filterPlaces("Museo & Monumento")
        }

        entertainmentPlacesButton.setOnClickListener {
            filterPlaces("Entretenimiento")
        }

        allPlacesButton.setOnClickListener {
            filterPlaces(null)
        }
    }

    private fun filterPlaces(type: String?){
        val places = DataManager.filterPlaces(type)
        if(places == null) {
            showPlaces(arrayOf())
        }
        else {
            showPlaces(places)
        }
    }

    private fun showPlaces(places: Array<Place>) {
        val recyclerView = rView.findViewById(R.id.place_list) as RecyclerView
        recyclerView.adapter = MyPlaceRecyclerViewAdapter(places, mListener)
    }

    private fun loadingError() {
        Toast.makeText(activity, getString(R.string.problem_loading), Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
        // TODO: Update argument type and name
        fun onListFragmentInteraction(place: Place)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): MyPlacesFragment {
            val fragment = MyPlacesFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
