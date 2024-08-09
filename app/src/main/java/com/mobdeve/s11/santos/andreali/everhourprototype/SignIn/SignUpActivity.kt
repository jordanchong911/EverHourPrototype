package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.SignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: SignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        dbRef = db.reference.child("users")

        binding.btnSignUpMain.setOnClickListener {
            val fname = binding.etvFName.text.toString()
            val lname = binding.etvLName.text.toString()
            val email = binding.etvEmail.text.toString()
            val password = binding.etvPassword.text.toString()
            val confirmPassword = binding.etvConfirmPassword.text.toString()

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                            val user = UserModel(
                                fname = fname,
                                lname = lname,
                                email = email
                            )

                            dbRef.child(userId).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to save user to database: ${it.message}", Toast.LENGTH_SHORT).show()
                                }

                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message} !", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.lloPrompt.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
