package es.usj.zaragozatrips.fragments

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.fragments.VideoGridFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_videogrid.view.*

class MyVideoGridRecyclerViewAdapter(
        private val mValues: Array<String>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyVideoGridRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_videogrid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoView = holder.videoView
        val item = mValues[position]
        videoView.setVideoURI(Uri.parse(item))
        videoView.seekTo(1);

        with(holder.mView) {
            tag = item
            setOnClickListener {
                if (mListener != null) {
                    mListener.onVideoFragmentInteraction(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val videoView: VideoView = mView.iv_video

    }
}
