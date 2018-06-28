package com.willowtreeapps.namegame.network.api.model

import android.os.Parcel
import android.os.Parcelable

class SocialLink(val type: String, val callToAction: String, val url: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(callToAction)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SocialLink> {
        override fun createFromParcel(parcel: Parcel): SocialLink {
            return SocialLink(parcel)
        }

        override fun newArray(size: Int): Array<SocialLink?> {
            return arrayOfNulls(size)
        }
    }
}