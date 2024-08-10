package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
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

            itemView.setOnClickListener {
                val intent = Intent(context, ProjectDetailsActivity::class.java).apply {
                    putExtra("PROJECT_ID", project.projectID)
                    putExtra("WORKSPACE_ID", workspaceId)
                }
                context.startActivity(intent)
            }

            binding.ivDots.setOnClickListener {
                val popupMenu = PopupMenu(context, binding.ivDots)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.project_card_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_update -> {
                            val updateDialog = ProjectUpdateDialogFragment(project) { updatedProject ->
                                updateProjectInFirebase(updatedProject)
                            }
                            updateDialog.show(supportFragmentManager, "ProjectUpdateDialog")
                            true
                        }
                        R.id.action_delete -> {
                            val deleteDialog = DeleteProjectDialogFragment(workspaceId, project.projectID) {
                                (context as ProjectsActivity).fetchProjects()
                            }
                            deleteDialog.show(supportFragmentManager, "DeleteProjectDialog")
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }

        }



        private fun updateProjectInFirebase(updatedProject: Project) {
            val dbRef = FirebaseDatabase.getInstance().reference

            // Correct path to update the project
            dbRef.child("workspaces").child(workspaceId).child("projects").child(updatedProject.projectID).setValue(updatedProject)
                .addOnSuccessListener {
                    (context as ProjectsActivity).fetchProjects() // Refresh the project list after update
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update project. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }

    }
}

