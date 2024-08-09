package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.AccountEditBinding

class AccountEditActivity : AppCompatActivity() {
    private lateinit var binding: AccountEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountEditBinding.inflate(layoutInflater)
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
                        Toast.makeText(this@AccountEditActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@AccountEditActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveChanges.setOnClickListener {
            val newFirstName = binding.tilFirstname.editText?.text.toString().trim()
            val newLastName = binding.tilLastname.editText?.text.toString().trim()
            val newEmail = binding.etvEmail.text.toString().trim()

            if (newFirstName.isEmpty() || newLastName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email already exists
            dbRef.child("users").orderByChild("email").equalTo(newEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.children.any { it.key != currentUser?.uid }) {
                            Toast.makeText(this@AccountEditActivity, "Email already in use", Toast.LENGTH_SHORT).show()
                        } else {
                            updateUserInfo(newFirstName, newLastName, newEmail)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@AccountEditActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener {
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUserInfo(firstName: String, lastName: String, email: String) {
        val currentUser = auth.currentUser ?: return

        val userId = currentUser.uid

        // Update Firebase Authentication profile (without email, since it's deprecated)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$firstName $lastName")
            .build()

        currentUser.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
            if (profileTask.isSuccessful) {
                // Update Realtime Database
                val userUpdates = mapOf(
                    "fname" to firstName,
                    "lname" to lastName,
                    "email" to email
                )

                dbRef.child("users").child(userId).updateChildren(userUpdates)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AccountActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to update profile in database", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}