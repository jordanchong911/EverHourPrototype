package com.mobdeve.s11.santos.andreali.everhourprototype

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.MemberCardBinding

class MemberAdapter(private var members: List<Member>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var onRoleClickListener: OnRoleClickListener? = null
    private var onOptionsClickListener: OnOptionsClickListener? = null

    inner class MemberViewHolder(private val binding: MemberCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.tvUsername.text = member.email
            binding.tvRole.text = member.role
            Log.d("MemberAdapter", "Name: ${member.email}, Role: ${member.role}")

            // Set up click listener for role TextView
            binding.tvRole.setOnClickListener {
                onRoleClickListener?.onRoleClick(member.email, member.role)
            }

            // Set up click listener for options ImageView
            binding.ivOptions.setOnClickListener {
                onOptionsClickListener?.onOptionsClick(member.email)
            }
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

    fun setOnRoleClickListener(listener: OnRoleClickListener) {
        onRoleClickListener = listener
    }

    fun setOnOptionsClickListener(listener: OnOptionsClickListener) {
        onOptionsClickListener = listener
    }

    interface OnRoleClickListener {
        fun onRoleClick(email: String, currentRole: String)
    }

    interface OnOptionsClickListener {
        fun onOptionsClick(email: String)
    }
}
