package es.usj.zaragozatrips.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import es.usj.zaragozatrips.R
import kotlinx.android.synthetic.main.fragment_media_preview.*
import java.io.File

class MediaPreviewFragment : Fragment() {
    companion object {
        const val EXTRA_MEDIA_KEY = "MediaPreviewFragment.EXTRA_PHOTO_KEY"
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.title = getString(R.string.title_base, getString(R.string.photos))

        val item = this.arguments!!.getString(MediaPreviewFragment.EXTRA_MEDIA_KEY)
        val file = File(item)
        Picasso.get().load(file).into(imageView)
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
