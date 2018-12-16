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
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.services.CustomDataManager
import kotlinx.android.synthetic.main.fragment_new_place.*
import java.util.*
import kotlin.collections.ArrayList
import com.squareup.picasso.Picasso
import es.usj.zaragozatrips.services.UriHelper
import java.io.File

class NewPlaceFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var fileUri: Uri? = null
    private var imagePath: String? = null
    private val TAKE_PHOTO_REQUEST = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity.title = getString(R.string.title_base, getString(R.string.new_place))
        return inflater.inflate(R.layout.fragment_new_place, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePhotoButton.setOnClickListener {
            askCameraPermission()
        }

        custom_place_image_view.visibility = View.GONE
        custom_place_image_view.setOnClickListener {
            custom_place_image_view.visibility = View.GONE
            imagePath = null
        }
        /*CustomDataManager.getData {
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
        }*/

        saveButton.setOnClickListener {
            if(validateData()) {
                val coordinate = Coordinate(0.0, 0.0,0.0)
                val images = ArrayList<String>()
                images.add(imagePath!!)

                val place = CustomPlace(UUID.randomUUID(),
                        nameTextView.text.toString(),
                        descriptionTextView.text.toString(),
                        coordinate,
                        arrayListOf(),
                        images
                        )
                if(CustomDataManager.saveNewCustomPlace(place)) {
                    Toast.makeText(activity, getString(R.string.new_place_saved), Toast.LENGTH_SHORT).show()
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, MyCustomPlacesFragment()).commit()
                }
                else {
                    Toast.makeText(activity, "There was an error saving the new place", Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == TAKE_PHOTO_REQUEST) {
            //takePhotoButton.setImageURI(fileUri)
            imagePath = UriHelper.getRealPathFromURI(activity, fileUri)

            val file = File(imagePath)
            custom_place_image_view.visibility = View.VISIBLE
            Picasso.get().load(file).into(custom_place_image_view)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}// Required empty public constructor
