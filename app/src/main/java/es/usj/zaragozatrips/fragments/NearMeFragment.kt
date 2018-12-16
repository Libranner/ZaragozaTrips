package es.usj.zaragozatrips.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker

import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.DataManager
import kotlinx.android.synthetic.main.fragment_near_me.*


class NearMeFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var map: GoogleMap
    private val PERMISSIONS_REQUEST_LOCATION = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity.title = getString(R.string.title_base, getString(R.string.near_me))
        DataManager.loadData(::onDataReady)
        return inflater.inflate(R.layout.fragment_near_me, container, false)
    }

    private fun onDataReady(places: Array<Place>) {
        if(places.isEmpty()){
            loadingError()
            return
        }

        showPlacesInMap(places)
        selectButton(allPlacesButton)
    }

    private fun showPlacesInMap(places: Array<Place>?) {
        map.clear()

        places?.forEach {
            with(it) {
                map.addMarker(com.google.android.gms.maps.model.MarkerOptions()
                        .position(com.google.android.gms.maps.model.LatLng(coordinate.latitude, coordinate.longitude))
                        .title(name)
                )
            }
        }
    }

    private fun loadingError() {
        Toast.makeText(activity, getString(R.string.problem_loading), Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

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
        showPlacesInMap(places)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
        }
        else {
            map.isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                map.isMyLocationEnabled = true
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val coordinate = Coordinate(marker.position.latitude, marker.position.longitude, 0.0)
        val place = DataManager.findPlace(coordinate)

        if(place != null) {
            val bundle = Bundle()
            bundle.putParcelable(getString(R.string.place_item_key), place)
            val fragment =  PlaceDetailFragment()
            fragment.arguments = bundle

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out) //animations, this must always be set before the replace
                    .replace(R.id.fragment_container, fragment) //replacing current fragment with the new one
                    .addToBackStack(null)
                    .commit()
        }
        return false
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
}// Required empty public constructor
