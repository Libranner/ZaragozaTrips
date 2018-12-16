package es.usj.zaragozatrips.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.usj.zaragozatrips.R
import kotlinx.android.synthetic.main.fragment_video.*

class VideoPreviewFragment : Fragment() {
    companion object {
        const val EXTRA_MEDIA_KEY = "VideoPreviewFragment.EXTRA_PHOTO_KEY"
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.title = getString(R.string.title_base, getString(R.string.videos))
        val item = this.arguments!!.getString(VideoPreviewFragment.EXTRA_MEDIA_KEY)
        videoView.setVideoURI(Uri.parse(item))
        videoView.start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}
