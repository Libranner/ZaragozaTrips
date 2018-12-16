package es.usj.zaragozatrips.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomMedia(val url: String, val title: String) : Parcelable