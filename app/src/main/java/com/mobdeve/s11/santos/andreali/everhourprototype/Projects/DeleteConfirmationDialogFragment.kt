package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class ProjectDeleteDialogFragment(
    private val deleteCallback: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this project?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCallback()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
