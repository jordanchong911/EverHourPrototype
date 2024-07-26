package com.mobdeve.s11.santos.andreali.everhourprototype

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.MemberCardBinding

class MemberAdapter(private var members: List<Member>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(private val binding: MemberCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.tvUsername.text = member.name
            binding.tvRole.text = member.role
            Log.d("MemberAdapter", "Name: ${member.name}, Role: ${member.role}") // Add this for debugging
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = MemberCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }
}
