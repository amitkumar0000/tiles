package com.lpirro.tiledemo.customquicksettings

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding

class NotificationViewHolder(binding: CustomNotificationLayoutBinding): RecyclerView.ViewHolder(binding.root) {
    val smallIcon: ImageView = binding.icon
    val title: TextView = binding.title
    val content: TextView = binding.content
    val layout = binding.notificationRowLayout
}