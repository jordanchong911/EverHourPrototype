package com.mobdeve.s11.santos.andreali.everhourprototype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceCardBinding

class WorkspaceAdapter(
    private var workspaces: MutableList<Workspace>,
    private val onItemClick: (Workspace) -> Unit
) : RecyclerView.Adapter<WorkspaceAdapter.WorkspaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val binding = WorkspaceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkspaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        val workspace = workspaces[position]
        holder.bind(workspace)
    }

    override fun getItemCount(): Int = workspaces.size

    fun updateData(newWorkspaces: List<Workspace>) {
        workspaces.clear()
        workspaces.addAll(newWorkspaces)
        notifyDataSetChanged()
    }

    inner class WorkspaceViewHolder(private val binding: WorkspaceCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(workspace: Workspace) {
            binding.tvWorkspaceName.text = workspace.name

            // Handle click event for the card to navigate to WorkspaceDetailsActivity
            itemView.setOnClickListener {
                onItemClick(workspace)
            }

            // Handle the click event for the dots (delete action)
            binding.ivDots.setOnClickListener {
                // Create and show the delete dialog
                val dialog = DeleteWorkspaceDialogFragment(workspace.id)
                dialog.setOnWorkspaceDeletedListener(object : DeleteWorkspaceDialogFragment.OnWorkspaceDeletedListener {
                    override fun onWorkspaceDeleted() {
                        // Remove the deleted workspace from the list and notify the adapter
                        workspaces.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)

                        // Optionally, notify the activity to fetch the updated list of workspaces
                        (itemView.context as? WorkspaceActivity)?.fetchWorkspaces()
                    }
                })
                dialog.show((itemView.context as AppCompatActivity).supportFragmentManager, "DeleteWorkspaceDialog")
            }
        }
    }
}
