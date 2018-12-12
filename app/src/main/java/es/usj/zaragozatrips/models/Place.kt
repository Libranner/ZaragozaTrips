package es.usj.zaragozatrips.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by libranner on 12/12/2018.
 */



data class Place(
    val name: String,
    val tipo: String,
    val schedule: String,
    val coordinate: Coordinate,
    val videUrl: String,
    val imagesUrl: Array<String>,
    val description: String): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Coordinate::class.java.classLoader),
            parcel.readString(),
            parcel.createStringArray(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(tipo)
        parcel.writeString(schedule)
        parcel.writeParcelable(coordinate, flags)
        parcel.writeString(videUrl)
        parcel.writeStringArray(imagesUrl)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (name != other.name) return false
        if (tipo != other.tipo) return false
        if (schedule != other.schedule) return false
        if (coordinate != other.coordinate) return false
        if (videUrl != other.videUrl) return false
        if (!Arrays.equals(imagesUrl, other.imagesUrl)) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tipo.hashCode()
        result = 31 * result + schedule.hashCode()
        result = 31 * result + coordinate.hashCode()
        result = 31 * result + videUrl.hashCode()
        result = 31 * result + Arrays.hashCode(imagesUrl)
        result = 31 * result + description.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }
}

