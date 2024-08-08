package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MemberInviteDialogFragment : DialogFragment() {

    interface OnMemberInviteListener {
        fun onMemberInvited(email: String)
    }

    private var listener: OnMemberInviteListener? = null

    companion object {
        private const val ARG_WORKSPACE_ID = "workspace_id"

        fun newInstance(workspaceId: String): MemberInviteDialogFragment {
            val fragment = MemberInviteDialogFragment()
            val args = Bundle()
            args.putString(ARG_WORKSPACE_ID, workspaceId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnMemberInviteListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnMemberInviteListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val workspaceId = arguments?.getString(ARG_WORKSPACE_ID) ?: ""

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.member_invite_ol, null)

            val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
            val editEmail = view.findViewById<TextInputEditText>(R.id.etInvEmail)

            builder.setView(view)
                .setPositiveButton("Invite") { _, _ ->
                    val email = editEmail.text.toString()
                    if (email.isNotBlank()) {
                        listener?.onMemberInvited(email)
                    } else {
                        Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

