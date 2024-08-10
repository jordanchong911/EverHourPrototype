package com.mobdeve.s11.santos.andreali.everhourprototype.SignIn

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.R

class ForgotPwActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var emailEt: EditText
    private lateinit var resetPasswordBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_forgot)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        emailEt = findViewById(R.id.etvSIFEmail)
        resetPasswordBtn = findViewById(R.id.btnResetPW)

        resetPasswordBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show()
            } else {
                checkIfEmailExists(email)
            }
        }
    }

    private fun checkIfEmailExists(email: String) {
        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email exists in the database, send password reset email
                        sendPasswordResetEmail(email)
                    } else {
                        // Email does not exist
                        Toast.makeText(this@ForgotPwActivity, "Email not found in users.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ForgotPwActivity, "Database error: ${databaseError.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK) // Set result code here
                    finish() // Finish activity and remove from back stack
                } else {
                    Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG).show()
                }
            }
    }
}