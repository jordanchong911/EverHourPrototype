package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton
import android.widget.Toast

class EntryRateDialogFragment : DialogFragment() {

    private var listener: ((Int) -> Unit)? = null

    fun setOnRateEnteredListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.entry_rate_ol, container, false)

        val editRate = view.findViewById<TextInputEditText>(R.id.etRate)
        val setButton = view.findViewById<MaterialButton>(R.id.btnSetWorkSpName)

        setButton.setOnClickListener {
            val rateText = editRate.text.toString().trim()
            val rate = rateText.toIntOrNull()
            if (rate != null && rate > 0) {
                listener?.invoke(rate)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a valid rate", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
