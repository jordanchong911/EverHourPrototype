package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectUpdateOlBinding

class ProjectUpdateDialogFragment(
    private val project: Project,
    private val updateCallback: (Project) -> Unit
) : DialogFragment() {

    private lateinit var binding: ProjectUpdateOlBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = ProjectUpdateOlBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        binding.apply {
            // Set current project data
            tilName.editText?.setText(project.name)
            tilClient.editText?.setText(project.client)
            tilTeam.editText?.setText(project.roleIC)

            btnSetWorkSpName.setOnClickListener {
                val updatedProject = project.copy(
                    name = tilName.editText?.text.toString(),
                    client = tilClient.editText?.text.toString(),
                    roleIC = tilTeam.editText?.text.toString()
                )
                // Ensure projectID and workspaceID are included in the updated project
                updateCallback(updatedProject)
                dismiss()
            }
        }

        return builder.create()
    }
}
