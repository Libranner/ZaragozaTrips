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
import android.widget.ImageButton
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

        activity.title = getString(R.string.title_base, getString(R.string.places))

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    private lateinit var rView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rView = inflater.inflate(R.layout.fragment_myplace_list, container, false)
        return rView
    }

    private fun onDataReady(places: Array<Place>) {
        if(places.isEmpty()){
            loadingError()
            return
        }

        val recyclerView = rView.findViewById(R.id.place_list) as RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, mColumnCount)
        }
        showPlaces(places)
        selectButton(allPlacesButton)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataManager.loadData(::onDataReady)

        foodPlacesButton.setOnClickListener {
            filterPlaces(getString(R.string.food_beverage_key))
            selectButton(foodPlacesButton)
        }

        museumPlaceButton.setOnClickListener {
            filterPlaces(getString(R.string.museum_key))
            selectButton(museumPlaceButton)
        }

        entertainmentPlacesButton.setOnClickListener {
            filterPlaces(getString(R.string.entertainment_key))
            selectButton(entertainmentPlacesButton)
        }

        allPlacesButton.setOnClickListener {
            filterPlaces(null)
            selectButton(allPlacesButton)
        }
    }

    private fun selectButton(button: ImageButton) {
        val buttons = arrayOf(foodPlacesButton, museumPlaceButton, entertainmentPlacesButton, allPlacesButton)

        buttons.forEach {
            if(it.id == button.id){
                it.backgroundTintList = activity.getColorStateList(R.color.selected_button_color);
            }
            else {
                it.backgroundTintList = activity.getColorStateList(R.color.default_color_button);
            }
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
        fun onListFragmentInteraction(place: Place)
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
