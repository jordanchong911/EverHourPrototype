package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.Projects.ProjectsActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectCardBinding

class ProjectAdapter(
    private var projects: MutableList<Project>,
    private val supportFragmentManager: androidx.fragment.app.FragmentManager,
    private val workspaceId: String,
    private val context: Context
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ProjectCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int = projects.size

    fun updateProjects(newProjects: List<Project>) {
        projects.clear()
        projects.addAll(newProjects)
        notifyDataSetChanged()
    }

    inner class ProjectViewHolder(private val binding: ProjectCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.tvProjectName.text = project.name
            binding.tvRoleIC.text = project.roleIC

            // Handle click event for the card to navigate to ProjectDetailsActivity
            itemView.setOnClickListener {
                val intent = Intent(context, ProjectDetailsActivity::class.java).apply {
                    putExtra("PROJECT_ID", project.projectID)
                    putExtra("WORKSPACE_ID", workspaceId)
                }
                context.startActivity(intent)
            }

            // Handle the click event for the dots (options menu)
            binding.ivDots.setOnClickListener {
                val dialog = DeleteProjectDialogFragment(workspaceId, project.projectID)
                dialog.setOnProjectDeletedListener(object : DeleteProjectDialogFragment.OnProjectDeletedListener {
                    override fun onProjectDeleted() {
                        // Refresh projects after deletion
                        (context as ProjectsActivity).fetchProjects()
                    }
                })
                dialog.show(supportFragmentManager, "DeleteProjectDialog")
            }
        }
    }
}

