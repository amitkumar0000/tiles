package com.lpirro.tiledemo.customquicksettings

import androidx.recyclerview.widget.DiffUtil

class NotificationDiffUtils(private val newItem: List<NotificationModel>, private val oldItem: List<NotificationModel>) : DiffUtil.Callback() {

    override fun getOldListSize() =
            oldItem.size

    override fun getNewListSize() =
            newItem.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItem[oldItemPosition].title == newItem[newItemPosition].title && oldItem[oldItemPosition].icon == oldItem[oldItemPosition].icon
                    && newItem[newItemPosition].content == oldItem[oldItemPosition].content

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areItemsTheSame(oldItemPosition, newItemPosition)
}