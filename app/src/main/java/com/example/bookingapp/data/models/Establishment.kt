package com.example.bookingapp.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalTime

data class Establishment(
    @DocumentId
    val establishmentId: String,
    val name: String,
    val description: String,
    val address: String,
    val workingTimeStart: Timestamp,
    val workingTimeEnd: Timestamp,
    val phoneNumbers: String,
    val tableNumber: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp(0, 0),
        parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp(0, 0),
        parcel.readString() ?: "",
        parcel.readInt()
    ) {
    }

    @Suppress("unused")
    constructor() : this("", "", "", "", Timestamp(0, 0), Timestamp(0, 0), "", 0)
    constructor(
        name: String,
        description: String,
        address: String,
        workingTimeStart: Timestamp,
        workingTimeEnd: Timestamp,
        phoneNumber: String,
        tableNumber: Int
    ) : this(
        "",
        name,
        description,
        address,
        workingTimeStart,
        workingTimeEnd,
        phoneNumber,
        tableNumber
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(establishmentId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(address)
        parcel.writeParcelable(workingTimeStart, flags)
        parcel.writeParcelable(workingTimeEnd, flags)
        parcel.writeString(phoneNumbers)
        parcel.writeInt(tableNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Establishment> {
        override fun createFromParcel(parcel: Parcel): Establishment {
            return Establishment(parcel)
        }

        override fun newArray(size: Int): Array<Establishment?> {
            return arrayOfNulls(size)
        }
    }
}