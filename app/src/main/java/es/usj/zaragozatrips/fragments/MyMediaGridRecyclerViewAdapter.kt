package es.usj.zaragozatrips.fragments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.fragments.MediaGridFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_mediagrid.view.*
import java.io.File

class MyMediaGridRecyclerViewAdapter(
        private val mValues: Array<String>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyMediaGridRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_mediagrid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageView = holder.imageView
        val item = mValues[position]

        val file = File(item)
        Picasso.get().load(file).into(imageView)

        with(holder.mView) {
            tag = item
            setOnClickListener {
                if (mListener != null) {
                    mListener.onMediaFragmentInteraction(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val imageView: ImageView = mView.iv_photo

    }
}
