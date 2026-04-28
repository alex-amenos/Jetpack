package com.alxnophis.jetpack.core.ui.parceler

import android.os.Parcel
import android.os.Parcelable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parceler

class ImmutableListParceler<T : Parcelable>(
    val creator: Parcelable.Creator<T>,
) : Parceler<ImmutableList<T>> {
    override fun create(parcel: Parcel): ImmutableList<T> =
        parcel
            .createTypedArrayList(creator)
            .orEmpty()
            .toImmutableList()

    override fun ImmutableList<T>.write(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(this.toList())
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Parcelable> immutableListParceler(): ImmutableListParceler<T> =
    ImmutableListParceler(
        T::class.java.getField("CREATOR").get(null) as Parcelable.Creator<T>,
    )
