package com.mobdeve.s11.santos.andreali.everhourprototype

import Workspace
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceCardBinding

class WorkspaceAdapter(
    private var workspaces: MutableList<Workspace>,
    private val onItemClick: (Workspace) -> Unit
) : RecyclerView.Adapter<WorkspaceAdapter.WorkspaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workspace_card, parent, false)
        return WorkspaceViewHolder(view)
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

    inner class WorkspaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workspaceName: TextView = itemView.findViewById(R.id.tvWorkspaceName)
        private val role: TextView = itemView.findViewById(R.id.tvRole)
        private val dots: ImageView = itemView.findViewById(R.id.ivDots)

        fun bind(workspace: Workspace) {
            workspaceName.text = workspace.name

            // Handle click event for the card to navigate to WorkspaceDetailsActivity
            itemView.setOnClickListener {
                onItemClick(workspace)
            }

            // Handle the click event for the dots
            dots.setOnClickListener {
                // Create and show the delete dialog
                val dialog = DeleteWorkspaceDialogFragment(workspace.id)
                dialog.show((itemView.context as AppCompatActivity).supportFragmentManager, "DeleteWorkspaceDialog")
            }
        }
    }
}
