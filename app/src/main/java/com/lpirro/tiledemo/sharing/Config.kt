package com.lpirro.tiledemo.sharing

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Config(val shownNotifications: ShownNotifications, val shownElements: ShownElements): Parcelable
@Parcelize
data class ShownNotifications(val applicationName: ArrayList<ApplicationName>): Parcelable
@Parcelize
data class ApplicationName(val packagename: String): Parcelable
@Parcelize
data class ShownElements(val elements: List<Elements>): Parcelable
@Parcelize
data class Elements(val elementName: String, val accessLevel: String): Parcelable