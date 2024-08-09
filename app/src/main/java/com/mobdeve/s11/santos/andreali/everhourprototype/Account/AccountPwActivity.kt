package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.AccountPasswordBinding

class AccountPwActivity : AppCompatActivity() {
    private lateinit var binding: AccountPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        // Fetch user info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            dbRef.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val firstName = dataSnapshot.child("fname").getValue(String::class.java)
                    val lastName = dataSnapshot.child("lname").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)

                    if (firstName != null && lastName != null && email != null) {
                        val fullName = "$firstName $lastName"
                        binding.tvName.text = fullName
                        binding.tvEmail.text = email
                    } else {
                        Toast.makeText(this@AccountPwActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@AccountPwActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show()
        }

        binding.btnSavePassword.setOnClickListener {
            val oldPassword = binding.etvOldPassword.text.toString().trim()
            val newPassword = binding.etvNewPassword.text.toString().trim()
            val confirmNewPassword = binding.etvConfirmPassword.text.toString().trim()

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, oldPassword)
                    user.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Password changed successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, AccountActivity::class.java)
                                    startActivity(intent)
                                    finish() // Close the activity
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Password change failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(
                                        "ChangePasswordActivity",
                                        "Error: ${updateTask.exception}"
                                    )
                                }
                            }
                        } else {
                            Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("ChangePasswordActivity", "Error: ${task.exception}")
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show()
            }
        }

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener{
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener{
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}