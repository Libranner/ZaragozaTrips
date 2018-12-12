package es.usj.zaragozatrips.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso

import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Place
import kotlinx.android.synthetic.main.fragment_place_detail.*
import android.content.Intent

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlaceDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class PlaceDetailFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_LOCATION = 100
    lateinit var place: Place

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        return inflater.inflate(R.layout.fragment_place_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            place = this.arguments.getParcelable("test")
            displayData(place)

    }

    private fun displayData(place: Place) {
        place_name_text_view.text = place.name
        description_text_view.text = place.description
        schedule_text_view.text = place.schedule
        Picasso.get().load(place.imagesUrl[0]).into(main_image_view)
        distance_text_view.text = getString(R.string.calculating)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
        }
        else {
            calculateDistance()
        }

        navigateToButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${place.coordinate.latitude}, ${place.coordinate.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                calculateDistance()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun calculateDistance() {
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if(location != null) {
                        val result = FloatArray(1)
                        Location.distanceBetween(place.coordinate.latitude, place.coordinate.longitude,
                                location.latitude, location.longitude, result)
                        val meters = result[0]/ 1000.0
                        distance_text_view.text = getString(R.string.km_two_decimals_format).format(meters)
                    }
                }
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
}
