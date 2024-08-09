package com.mobdeve.s11.santos.andreali.everhourprototype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.Member
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.MemberCardBinding

class MemberAdapter(
    private val onRoleClickListener: OnRoleClickListener?,
    private val onOptionsClickListener: OnOptionsClickListener?
) : ListAdapter<Member, MemberAdapter.MemberViewHolder>(MemberDiffCallback()) {

    inner class MemberViewHolder(private val binding: MemberCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.tvUsername.text = "${member.lname}, ${member.fname}"
            binding.tvRole.text = member.role

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
        holder.bind(getItem(position))
    }

    interface OnRoleClickListener {
        fun onRoleClick(email: String, currentRole: String)
    }

    interface OnOptionsClickListener {
        fun onOptionsClick(email: String)
    }
}

class MemberDiffCallback : DiffUtil.ItemCallback<Member>() {
    override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem == newItem
    }
}
