package es.usj.zaragozatrips.fragments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.fragments.MyPlacesFragment.OnListFragmentInteractionListener
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.LocationHelper

class MyPlaceRecyclerViewAdapter(private val mValues: Array<Place>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyPlaceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_myplace, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = mValues[position]
        holder.mItem = place
        holder.titleTextView.text = place.name
        val meters = LocationHelper.calculateDistance(place.coordinate)

        if(meters < 3.0) {
            holder.distanceTextView.text = holder.itemView.context.getString(R.string.you_ve_arrived)
        }
        else {
            holder.distanceTextView.text = holder.itemView.context.getString(R.string.km_two_decimals_format).format(meters)
        }

        Picasso.get().load(place.imagesUrl[0]).into(holder.imageView)

        holder.mView.setOnClickListener {
            if (mListener != null) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                val item = holder.mItem
                mListener.onListFragmentInteraction(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val imageView: ImageView
        val titleTextView: TextView
        val distanceTextView: TextView

        lateinit var mItem: Place

        init {
            imageView = mView.findViewById(R.id.place_image_view)
            titleTextView = mView.findViewById(R.id.titleTextView)
            distanceTextView = mView.findViewById(R.id.distanceTextView)
        }
    }
}
