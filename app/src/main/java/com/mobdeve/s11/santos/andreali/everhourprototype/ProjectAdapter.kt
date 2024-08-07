package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectCardBinding
import android.content.Intent
import android.view.View

class ProjectAdapter(
    private var projects: MutableList<Project>,
    private val fragmentManager: FragmentManager,
    private val workspaceId: String,
    private val context: Context  // Pass context to handle navigation
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

    inner class ProjectViewHolder(private val binding: ProjectCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.apply {
                tvProjectName.text = project.name
                ivDots.setOnClickListener {
                    showOptionsDialog(project)
                }

                // Set up the click listener to navigate to the ProjectDetailsActivity
                root.setOnClickListener {
                    val intent = Intent(context, ProjectDetailsActivity::class.java).apply {
                        putExtra("PROJECT_ID", project.projectID)
                        putExtra("WORKSPACE_ID", workspaceId)
                    }
                    context.startActivity(intent)
                }
            }
        }

        private fun showOptionsDialog(project: Project) {
            val optionsDialog = AlertDialog.Builder(binding.root.context)
                .setItems(arrayOf("Update", "Delete")) { _, which ->
                    when (which) {
                        0 -> showUpdateDialog(project)
                        1 -> showDeleteDialog(project)
                    }
                }
                .create()
            optionsDialog.show()
        }

        private fun showUpdateDialog(project: Project) {
            val updateDialog = ProjectUpdateDialogFragment(project) { updatedProject ->
                updateProjectInFirebase(updatedProject)
            }
            updateDialog.show(fragmentManager, "ProjectUpdateDialog")
        }

        private fun showDeleteDialog(project: Project) {
            val deleteDialog = ProjectDeleteDialogFragment {
                deleteProjectFromFirebase(project.projectID)
            }
            deleteDialog.show(fragmentManager, "ProjectDeleteDialog")
        }

        private fun updateProjectInFirebase(project: Project) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("projects")
                .child(workspaceId).child(project.projectID)
            databaseReference.setValue(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Project updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed to update project", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun deleteProjectFromFirebase(projectID: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("projects")
                .child(workspaceId).child(projectID)
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Project deleted successfully", Toast.LENGTH_SHORT).show()
                    // Update the projects list and notify the adapter
                    projects.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                } else {
                    Toast.makeText(binding.root.context, "Failed to delete project", Toast.LENGTH_SHORT).show()
                    Log.e("DeleteProject", "Delete failed: ${task.exception?.message}")
                }
            }
        }
    }

    fun updateProjects(newProjects: List<Project>) {
        projects.clear()
        projects.addAll(newProjects)
        notifyDataSetChanged()
    }
}
