package es.usj.zaragozatrips.fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.CustomMedia
import es.usj.zaragozatrips.services.CustomDataManager
import java.util.*

class VideoGridFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mediagrid_list, container, false)

        activity.title = getString(R.string.title_base, getString(R.string.videos))
        val id = this.arguments.getString(getString(R.string.custom_place_item_key))
        val place = CustomDataManager.findPlace(UUID.fromString(id))

        if(place != null) {
            // Set the adapter
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    val videos = arrayOf("/storage/emulated/0/DCIM/Camera/VID_20181216_194525.mp4")
                    //adapter = MyVideoGridRecyclerViewAdapter(place.videosUrl.toTypedArray(), listener)
                    adapter = MyVideoGridRecyclerViewAdapter(videos, listener)
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onVideoFragmentInteraction(item: String)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                VideoGridFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
