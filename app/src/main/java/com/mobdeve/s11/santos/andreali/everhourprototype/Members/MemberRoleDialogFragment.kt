package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment

class MemberRoleDialogFragment : DialogFragment() {

    interface OnRoleSetListener {
        fun onRoleSet(email: String, role: String, firstName: String, lastName: String)
    }

    private var listener: OnRoleSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.member_role_ol, null)

        val roleEditText = view.findViewById<TextInputEditText>(R.id.etRole)
        val setButton = view.findViewById<Button>(R.id.btnSetRoleName)

        val email = arguments?.getString("EMAIL") ?: ""
        val firstName = arguments?.getString("FIRST_NAME") ?: ""
        val lastName = arguments?.getString("LAST_NAME") ?: ""

        // Initialize the role EditText if needed
        val initialRole = arguments?.getString("ROLE") ?: ""
        roleEditText.setText(initialRole)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        // Set up the button click listener
        setButton.setOnClickListener {
            val role = roleEditText.text.toString()
            if (role.isNotEmpty()) {
                if (role.equals("Admin", ignoreCase = true)) {
                    roleEditText.error = "Role 'Admin' is not allowed"
                } else {
                    listener?.onRoleSet(email, role, firstName, lastName)
                    dialog.dismiss() // Dismiss the dialog after setting the role
                }
            } else {
                // Handle empty input, show error
                roleEditText.error = "Role cannot be empty"
            }
        }

        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String, role: String, firstName: String, lastName: String): MemberRoleDialogFragment {
            val fragment = MemberRoleDialogFragment()
            val args = Bundle().apply {
                putString("EMAIL", email)
                putString("ROLE", role)
                putString("FIRST_NAME", firstName)
                putString("LAST_NAME", lastName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnRoleSetListener(listener: OnRoleSetListener) {
        this.listener = listener
    }
}
