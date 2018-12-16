package es.usj.zaragozatrips.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.ContentValues
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
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.services.CustomDataManager
import es.usj.zaragozatrips.services.UriHelper
import kotlinx.android.synthetic.main.fragment_custom_place_detail.*
import java.io.File
import java.util.*

class CustomPlaceDetailFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_LOCATION = 100
    lateinit var place: CustomPlace
    private var fileUri: Uri? = null
    private val TAKE_PHOTO_REQUEST = 101
    private var VideoUri: Uri? = null
    private val VIDEO_REQUEST_CODE = 102
    var type = 1

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

        addPhoto.setOnClickListener {
            type = 1
            askCameraPermission()
        }

        addVideo.setOnClickListener {
            type = 2
            askCameraPermission()
        }
    }

    private fun displayData(place: CustomPlace) {
        place_name_text_view.text = place.name
        description_text_view.text = place.description
        val file = File(place.imagesUrl[0])
        Picasso.get().load(file).into(main_image_view)
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
            val gmmIntentUri = Uri.parse("google.navigation:q=${place.coordinate.latitude}, " +
                    "${place.coordinate.longitude}")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }

        showPicturesButton.setOnClickListener {
            val mediaFragment = MediaGridFragment.newInstance(3)
            replaceFragment(mediaFragment)
        }

        showVideosButton.setOnClickListener {
            val fragment = VideoGridFragment.newInstance(3)
            replaceFragment(fragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.custom_place_item_key), place.id.toString())
        fragment.arguments = bundle

        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragment_container, fragment).commit()
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


    //launch the camera to take photo via intent
    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        fileUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(activity.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    //ask for permission to take photo
    private fun askCameraPermission() = Dexter.withActivity(activity)
            .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                    if(report.areAllPermissionsGranted()){
                        //once permissions are granted, launch the camera
                        if(type == 1) {
                            launchCamera()
                        }
                        else {
                            videoRercord()
                        }
                    }else{
                        Toast.makeText(activity, "All permissions need to be granted to use the camera", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    //show alert dialog with permission options
                    AlertDialog.Builder(activity)
                            .setTitle(
                                    "Permissions Error!")
                            .setMessage(
                                    "Please allow permissions to take photo with camera")
                            .setNegativeButton(
                                    android.R.string.cancel
                            ) { dialog, _ ->
                                dialog.dismiss()
                                token?.cancelPermissionRequest()
                            }
                            .setPositiveButton(android.R.string.ok
                            ) { dialog, _ ->
                                dialog.dismiss()
                                token?.continuePermissionRequest()
                            }
                            .setOnDismissListener {
                                token?.cancelPermissionRequest() }
                            .show()
                }



            }).check()

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == TAKE_PHOTO_REQUEST) {

            val imagePath = UriHelper.getRealPathFromURI(activity, fileUri)
            place.imagesUrl.add(imagePath)
            CustomDataManager.updatePlace(place)
        } else if (resultCode == VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                VideoUri = data?.data
                val videoPath = UriHelper.getRealPathFromURI(activity, VideoUri)
                place.videosUrl.add(videoPath)
                CustomDataManager.updatePlace(place)
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun videoRercord(){

        val values = ContentValues(1)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        VideoUri = activity!!.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

        val videoRecordIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        if (videoRecordIntent.resolveActivity(activity!!.packageManager) != null){
            startActivityForResult(videoRecordIntent, VIDEO_REQUEST_CODE)
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

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
