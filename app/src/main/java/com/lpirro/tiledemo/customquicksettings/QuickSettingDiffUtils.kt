package com.lpirro.tiledemo.customquicksettings

import androidx.recyclerview.widget.DiffUtil

internal class QuickSettingDiffUtils(private val oldList: List<QuickSettingModel>, private val newList: List<QuickSettingModel>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }
}