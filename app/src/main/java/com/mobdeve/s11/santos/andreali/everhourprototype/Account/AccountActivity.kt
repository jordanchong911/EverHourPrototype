package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.AccountSettingsBinding

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: AccountSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountSettingsBinding.inflate(layoutInflater)
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
                        Toast.makeText(this@AccountActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                        Log.e("AccountActivity", "Failed to load user data: firstName, lastName, or email is null")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@AccountActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AccountActivity", "Database error: ${databaseError.message}")
                }
            })
        } else {
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show()
            Log.e("AccountActivity", "No authenticated user")
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
            // Already here
        }

        // Edit Profile
        binding.lloEdit.setOnClickListener {
            val intent = Intent(this, AccountEditActivity::class.java)
            startActivity(intent)
        }

        // Edit Password
        binding.lloChange.setOnClickListener {
            val intent = Intent(this, AccountPwActivity::class.java)
            startActivity(intent)
        }

        // Logout
        binding.lloLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}