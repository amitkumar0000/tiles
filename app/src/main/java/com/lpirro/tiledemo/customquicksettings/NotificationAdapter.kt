package com.lpirro.tiledemo.customquicksettings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding

class NotificationAdapter: RecyclerView.Adapter<NotificationViewHolder>() {

    var notificationList = arrayListOf<NotificationModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NotificationViewHolder(CustomNotificationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.smallIcon.setImageResource(notificationList[position].icon)
        holder.title.text = notificationList[position].title
        holder.content.text = notificationList[position].content
    }

    override fun getItemCount() = notificationList.size

    fun setData(newList: List<NotificationModel>) {
        val diffUtils = NotificationDiffUtils(newList, notificationList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        notificationList.clear()
        notificationList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

}

data class NotificationModel(val icon: Int = R.drawable.bluetooth, val title: String = "", val content: String = "")