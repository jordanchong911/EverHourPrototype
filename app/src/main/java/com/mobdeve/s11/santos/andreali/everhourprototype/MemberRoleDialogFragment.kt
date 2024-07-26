package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MemberRoleDialogFragment : DialogFragment() {

    interface OnRoleSetListener {
        fun onRoleSet(email: String, role: String)
    }

    private var listener: OnRoleSetListener? = null

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String): MemberRoleDialogFragment {
            val fragment = MemberRoleDialogFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnRoleSetListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnRoleSetListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val email = arguments?.getString(ARG_EMAIL) ?: ""

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.member_role_ol, null)

            val tilRole = view.findViewById<TextInputLayout>(R.id.tilRole)
            val editRole = view.findViewById<TextInputEditText>(R.id.editRole)

            builder.setView(view)
                .setPositiveButton("Set Role") { _, _ ->
                    val role = editRole.text.toString()
                    if (role.isNotBlank()) {
                        listener?.onRoleSet(email, role)
                    } else {
                        Toast.makeText(context, "Please enter a role", Toast.LENGTH_SHORT).show()
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
