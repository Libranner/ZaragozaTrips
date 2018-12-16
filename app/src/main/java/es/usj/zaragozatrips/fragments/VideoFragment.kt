package com.example.clarymorlagomez.myvideorecorder

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.usj.zaragozatrips.R
import kotlinx.android.synthetic.main.fragment_video.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VideoFragment : Fragment() {

    private var VideoUri: Uri? = null
    private val VIDEO_REQUEST_CODE = 102
    private var videoPath: String? = null

    private var videoUriListener: OnFragmentVideoUriListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    private fun videoRercord(){

        val values = ContentValues(1)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        VideoUri = activity!!.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

        val videoRecordIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        if (videoRecordIntent.resolveActivity(activity!!.packageManager) != null){
//            videoRecordIntent.putExtra(MediaStore.EXTRA_OUTPUT, VideoUri)
//             videoRecordIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                     or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            startActivityForResult(videoRecordIntent, VIDEO_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            VIDEO_REQUEST_CODE -> {
                if (resultCode == RESULT_OK){
                    VideoUri = data?.data
                    videoView.setVideoURI(VideoUri)
                    videoPath = getRealPathFromURI(VideoUri)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnRecord.setOnClickListener{
            videoRercord()
        }

        btnPlay.setOnClickListener {
            videoUriListener?.onFragmentVideoUri(VideoUri)
            videoView.start()
        }

        btnSave.setOnClickListener {


        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentVideoUriListener) {
            videoUriListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        videoUriListener = null
    }

    interface OnFragmentVideoUriListener {
        fun onFragmentVideoUri(uri: Uri?)
    }

    fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (activity!!.contentResolver != null && uri != null) {
            val cursor = activity!!.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                path = cursor.getString(index)
                cursor.close()
            }
        }
        return path
    }
}
