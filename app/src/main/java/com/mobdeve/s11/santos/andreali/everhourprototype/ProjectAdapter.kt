package com.mobdeve.s11.santos.andreali.everhourprototype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectCardBinding

class ProjectAdapter(private val projects: List<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(private val binding: ProjectCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.tvProjectName.text = project.name
            binding.tvRoleIC.text = project.role
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ProjectCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size
}
