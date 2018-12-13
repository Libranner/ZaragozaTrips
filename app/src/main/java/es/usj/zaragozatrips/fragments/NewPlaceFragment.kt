package es.usj.zaragozatrips.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.CustomDataManager
import kotlinx.android.synthetic.main.fragment_new_place.*
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import java.io.File
import java.nio.file.Files.exists



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewPlaceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class NewPlaceFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var fileUri: Uri? = null
    private var imagePath: String? = null
    private val TAKE_PHOTO_REQUEST = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_place, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePhotoButton.setOnClickListener {
            askCameraPermission()
        }

        CustomDataManager.getData {
            if(it.isNotEmpty()) {
                val path = it.last().imagesUrl[0]
                val imgFile = File(path)

                if (imgFile.exists()) {
                    takePhotoButton.setImageURI(Uri.parse(path))
                    //val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    //takePhotoButton.setImageBitmap(myBitmap)
                    //Picasso.get().load(imgFile).centerCrop().into(takePhotoButton)
                }
            }
        }

        saveButton.setOnClickListener {
            if(validateData()) {
                Toast.makeText(activity, "New place saved", Toast.LENGTH_SHORT).show()

                val coord = Coordinate(0.0, 0.0,0.0)
                val images = ArrayList<String>()
                images.add(imagePath!!)

                val place = CustomPlace(UUID.randomUUID(),
                        nameTextView.text.toString(),
                        descriptionTextView.text.toString(),
                        coord,
                        arrayListOf(),
                        images
                        )
                CustomDataManager.saveNewCustomPlace(place)
                fragmentManager.beginTransaction().replace(R.id.fragment_container, MyCustomPlacesFragment()).commit()
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

    private fun validateData(): Boolean {
        if (imagePath == null || imagePath!!.isBlank() || nameTextView.text.isBlank()) {
            Toast.makeText(activity, getString(R.string.invalid_place_message), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //ask for permission to take photo
    fun askCameraPermission() = Dexter.withActivity(activity)
            .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                    if(report.areAllPermissionsGranted()){
                        //once permissions are granted, launch the camera
                        launchCamera()
                    }else{
                        Toast.makeText(activity, "All permissions need to be granted to take photo", Toast.LENGTH_LONG).show()
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

    //override function that is called once the photo has been taken
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == TAKE_PHOTO_REQUEST) {
            //photo from camera
            //display the photo on the imageview
            takePhotoButton.setImageURI(fileUri)
            imagePath = getRealPathFromURI(fileUri)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (activity.contentResolver != null && uri != null) {
            val cursor = activity.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
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
