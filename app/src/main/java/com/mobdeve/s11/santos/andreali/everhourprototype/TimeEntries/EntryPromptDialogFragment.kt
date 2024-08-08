package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class EntryPromptDialogFragment : DialogFragment() {

    private var listener: ((String) -> Unit)? = null

    fun setOnNameEnteredListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.entry_prompt_ol, container, false)

        val editEntry = view.findViewById<TextInputEditText>(R.id.etNameEntry)
        val setButton = view.findViewById<MaterialButton>(R.id.btnSetWorkSpName)

        setButton.setOnClickListener {
            val name = editEntry.text.toString().trim()
            if (name.isNotEmpty()) {
                listener?.invoke(name)
                dismiss()
            }
        }

        return view
    }
}
