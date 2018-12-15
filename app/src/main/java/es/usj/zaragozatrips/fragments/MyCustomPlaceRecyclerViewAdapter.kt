package es.usj.zaragozatrips.fragments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.R.id.imageView
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import java.io.File


class MyCustomPlaceRecyclerViewAdapter(private val mValues: Array<CustomPlace>, private val mCustomListener: MyCustomPlacesFragment.OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyCustomPlaceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_my_custom_places, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = mValues[position]
        holder.mItem = place
        holder.titleTextView.text = place.name
        holder.distanceTextView.text = "O KM"
        //Picasso.get().load(place.imagesUrl[0]).into(holder.imageView)


        val file = File(place.imagesUrl[0])

        Picasso.get().load(file).into(holder.imageView)


        holder.mView.setOnClickListener {
            if (mCustomListener != null) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                val item = holder.mItem
                mCustomListener.onListFragmentInteraction(item)
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

        lateinit var mItem: CustomPlace

        init {
            imageView = mView.findViewById(R.id.custom_place_image_view)
            titleTextView = mView.findViewById(R.id.titleTextViewCustom)
            distanceTextView = mView.findViewById(R.id.distanceTextView)
        }
    }
}
