package com.mobdeve.s11.santos.andreali.everhourprototype.Members.DiffUtil

import androidx.recyclerview.widget.DiffUtil
import com.mobdeve.s11.santos.andreali.everhourprototype.Member

class MemberDiffCallback(
    private val oldList: List<Member>,
    private val newList: List<Member>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].email == newList[newItemPosition].email

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}
