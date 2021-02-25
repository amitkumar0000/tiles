package com.lpirro.tiledemo.customquicksettings

import android.graphics.drawable.Icon
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationList[position].icon?.let{ holder.smallIcon.setImageIcon(notificationList[position].icon) }
        }
        notificationList[position].title?.apply {  holder.title.text = this}
        notificationList[position].content?.apply {  holder.content.text = this}
    }

    override fun getItemCount() = notificationList.size

    fun setData(newList: List<NotificationModel>) {
        val diffUtils = NotificationDiffUtils(newList, notificationList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        notificationList.clear()
        notificationList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteItem(position: Int) {
        notificationList.removeAt(position)
        notifyItemRemoved(position)
    }

}

data class NotificationModel(val icon: Icon?, val title: String?, val content: String?)