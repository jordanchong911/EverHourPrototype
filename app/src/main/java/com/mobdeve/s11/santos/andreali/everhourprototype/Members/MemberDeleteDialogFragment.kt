package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MemberDeleteDialogFragment : DialogFragment() {

    interface OnDeleteListener {
        fun onDelete(email: String)
    }

    private var listener: OnDeleteListener? = null

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String): MemberDeleteDialogFragment {
            val fragment = MemberDeleteDialogFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnDeleteListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnDeleteListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val email = arguments?.getString(ARG_EMAIL) ?: ""

        // Inflate the custom layout
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.member_delete_ol, null)

        // Set up the dialog builder
        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .create().apply {
                // Initialize and set listeners for buttons
                view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                    listener?.onDelete(email)
                    dismiss()
                }
                view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                    dismiss()
                }
            }
    }

    // Method to set the listener
    fun setOnDeleteListener(listener: OnDeleteListener) {
        this.listener = listener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
