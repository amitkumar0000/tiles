package com.lpirro.tiledemo.sharing

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Config(val shownNotifications: ShownNotifications, val shownElements: ShownElements): Parcelable
@Parcelize
data class ShownNotifications(val notification: List<Notification>): Parcelable
@Parcelize
data class Notification(val applicationName: String, val packageName: String): Parcelable
@Parcelize
data class ShownElements(var elements: ArrayList<Elements>): Parcelable
@Parcelize
data class Elements(val elementName: String, val accessLevel: String): Parcelable