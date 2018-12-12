package es.usj.zaragozatrips.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.Place
import kotlinx.android.synthetic.main.fragment_place_detail.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlaceDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class PlaceDetailFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        // Inflate the layout for this fragment

        val images = arrayOf("https://bit.ly/2B9pPGC", "https://bit.ly/2QOYp2C")
        val place = Place("El Calamar Bravo", "Comida & Bebida", "Desde las 9AM hasta las 10AM",
                Coordinate(-30f, 150.1f), "https://www.youtube.com/watch?v=cmkV-vWx04o", images,
                "orem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nu")


        return inflater.inflate(R.layout.fragment_place_detail, container, false)
    }

    private fun displayData(place: Place) {
        place_name_text_view.text = place.name
        description_text_view.text = place.description
        schedule_text_view.text = place.schedule
        
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}// Required empty public constructor
