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
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.services.CustomDataManager
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CustomPlaceDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class CustomPlaceDetailFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_LOCATION = 100
    lateinit var place: CustomPlace

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        return inflater.inflate(R.layout.fragment_custom_place_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = this.arguments.getString(getString(R.string.custom_place_item_key))
        val result = CustomDataManager.findPlace(UUID.fromString(id))

        if(result != null) {
            place = result
            activity.title = place.name
            displayData(place)
        }
    }

    private fun displayData(place: CustomPlace) {
        place_name_text_view.text = place.name
        description_text_view.text = place.description
        Picasso.get().load(place.imagesUrl[0]).into(main_image_view)
        distance_text_view.text = getString(R.string.calculating)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
        }
        else {
            if(place.coordinate.latitude > 0) {
                calculateDistance()
            }
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
                        if(meters < 3.0) {
                            distance_text_view.text = getString(R.string.you_ve_arrived)
                        }
                        else {
                            distance_text_view.text = getString(R.string.km_two_decimals_format).format(meters)
                        }
                    }
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
        fun onFragmentInteraction(uri: Uri)
    }
}
